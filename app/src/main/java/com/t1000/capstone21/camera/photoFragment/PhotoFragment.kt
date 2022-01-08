

package com.t1000.capstone21.camera.photoFragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.*
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.t1000.capstone21.KEY_EVENT_EXTRA
import com.t1000.capstone21.models.Photo
import com.t1000.capstone21.R
import com.t1000.capstone21.camera.baseFragment.BaseFragment
import com.t1000.capstone21.camera.baseFragment.CameraViewModel
import com.t1000.capstone21.databinding.FragmentPhotoBinding
import com.t1000.capstone21.utils.*
import com.t1000.capstone21.utils.BottomNavViewUtils.showBottomNavBar
import kotlinx.coroutines.*
import java.lang.Runnable
import java.nio.ByteBuffer
import java.util.ArrayDeque
import kotlin.collections.ArrayList

typealias LumaListener = (luma: Double) -> Unit

private const val TAG = "PhotoFragment"
val EXTENSION_WHITELIST = arrayOf("JPG")


class PhotoFragment : BaseFragment<FragmentPhotoBinding>() {


    override val binding: FragmentPhotoBinding by lazy {
        FragmentPhotoBinding.inflate(layoutInflater)
    }


    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var selectedTimer = CameraTimer.OFF


    var savedUri :Uri? = null



    private val viewModel by lazy { ViewModelProvider(this).get(CameraViewModel::class.java) }



    override  val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                // When the volume down button is pressed, simulate a shutter button click
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    binding?.cameraCaptureButton?.simulateClick()
                }
            }
        }
    }


    override  val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@PhotoFragment.displayId) {
              //  Log.d(TAG, "Rotation changed: ${view.display.rotation}")
                imageCapture?.targetRotation = view.display.rotation
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState)




    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

        postViewFinder(binding.viewFinder)

        gestureListener(binding.viewFinder){
            Navigation.findNavController(view).navigate(R.id.action_camera_to_video)
        }


        binding.cameraCaptureButton.setOnClickListener {
            takePicture()
        }

        binding.cameraSwitchButton.setOnClickListener {
            switchCamera()
        }

        binding.flashAutoButton.setOnClickListener {
            closeFlashOptionsAndSelect(FLASH_MODE_AUTO)
        }
        binding.flashOffButton.setOnClickListener {
            closeFlashOptionsAndSelect(FLASH_MODE_OFF)
        }

        binding.timer10sButton.setOnClickListener {
            closeTimerAndSelect(CameraTimer.SEC10)
        }

        binding.timer3sButton.setOnClickListener {
            closeTimerAndSelect(CameraTimer.SEC3)
        }

        binding.timerOffButton.setOnClickListener {
            closeTimerAndSelect(CameraTimer.OFF)
        }

        binding.flashButton.setOnClickListener {
            showFlashOptions()
        }

        binding.timerButton.setOnClickListener {
            showTimerOptions()
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Rebind the camera with the updated display metrics
        bindCameraUseCases()

        // switching between cameras
        updateCameraSwitchButton(binding.cameraSwitchButton)
    }

    override fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            // Enable or disable switching between cameras
            updateCameraSwitchButton(binding.cameraSwitchButton)

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun bindCameraUseCases() {

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = windowManager.getCurrentWindowMetrics().bounds
       // Log.d(TAG, "Screen metrics: ${metrics.width()} x ${metrics.height()}")

        val screenAspectRatio = aspectRatio(metrics.width(), metrics.height())
      //  Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = binding.viewFinder.display.rotation

        // CameraProvider
        val cameraProvider = cameraProvider
                ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        // Preview
        preview = Preview.Builder()
                // We request aspect ratio but no resolution
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation
                .setTargetRotation(rotation)
                .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                // We request aspect ratio but no resolution to match preview config, but letting
                // CameraX optimize for whatever specific resolution best fits our use cases
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation, we will have to call this again if rotation changes
                // during the lifecycle of this use case
                .setTargetRotation(rotation)
                .build()

        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
                // We request aspect ratio but no resolution
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation, we will have to call this again if rotation changes
                // during the lifecycle of this use case
                .setTargetRotation(rotation)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        // Values returned from our analyzer are passed to the attached listener
                        // We log image analysis results here - you should do something useful
                        // instead!
                       // Log.d(TAG, "Average luminosity: $luma")
                    })
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }


//TODO:not require
//    override fun updateCameraUi() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            outputDirectory.listFiles { file ->
//                EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
//            }?.maxOrNull()?.let {
//              //  setGalleryThumbnail(binding.photoViewButton, Uri.fromFile(it))
//            }
//        }
//
//        binding.cameraSwitchButton.isEnabled = false
//    }

    fun takePicture() {
        lifecycleScope.launch(Dispatchers.Main) {
            when (selectedTimer) {
                CameraTimer.SEC3 -> for (i in 3 downTo 1) {
                    binding.countdown.text = i.toString()
                    delay(1000)
                }

                CameraTimer.SEC10 -> for (i in 10 downTo 1) {
                    binding.countdown.text = i.toString()
                    delay(1000)
                }
                else -> binding.countdown.text = ""
            }
            binding.countdown.text = ""
            imagePhoto()
        }
    }

    private fun imagePhoto() {
        imageCapture?.let { imageCapture ->
            val model = Photo()

            val photoFile = viewModel.getPhotoFile(model)

            // Setup image capture metadata
            val metadata = Metadata().apply {
                // Mirror image when using the front camera
                isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
            }

            // Create output options object which contains file + metadata
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                .setMetadata(metadata)
                .build()

            // Setup image capture listener which is triggered after photo has been taken
            imageCapture.takePicture(
                outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                         savedUri = output.savedUri ?: Uri.fromFile(photoFile)

                        // uploadPhoto()

                        Log.d(TAG, "Photo capture succeeded: $savedUri")

//                        // We can only change the foreground Drawable using API level 23+ API
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            // Update the gallery thumbnail with latest picture taken
//                         //   savedUri?.let { setGalleryThumbnail(binding.photoViewButton , it) }
//                        }
//
//                        // Implicit broadcasts will be ignored for devices running API level >= 24
//                        // so if you only target API level 24+ you can remove this statement
//                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                            requireActivity().sendBroadcast(
//                                Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri)
//                            )
//                        }
//
//                        // If the folder selected is an external media directory, this is
//                        // unnecessary but otherwise other apps will not be able to access our
//                        // images unless we scan them using [MediaScannerConnection]
//                        val mimeType = MimeTypeMap.getSingleton()
//                            .getMimeTypeFromExtension(savedUri?.toFile()?.extension)
//                        MediaScannerConnection.scanFile(
//                            context,
//                            arrayOf(savedUri?.toFile()?.absolutePath),
//                            arrayOf(mimeType)
//                        ) { _, uri ->
//                            Log.d(TAG, "Image capture scanned into media store: $uri")
//                        }
                    }
                })

                // Display flash animation to indicate that photo was captured
                binding.root.postDelayed({
                    binding.root.foreground = ColorDrawable(Color.WHITE)
                    binding.root.postDelayed(
                        { binding.root.foreground = null }, ANIMATION_FAST_MILLIS)
                }, ANIMATION_SLOW_MILLIS)


        }
    }

    fun switchCamera() {
        lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
            binding.cameraSwitchButton.setImageResource(R.drawable.ic_camera_front)
            CameraSelector.LENS_FACING_BACK
        } else {
            binding.cameraSwitchButton.setImageResource(R.drawable.ic_camera_rear)
            CameraSelector.LENS_FACING_FRONT
        }
        bindCameraUseCases()
    }



    fun showTimerOptions() {
        binding.timerConteiner.visibility = View.VISIBLE
    }

    fun closeTimerAndSelect(timer: CameraTimer) {
        selectedTimer = timer
        binding.timerConteiner.visibility = View.GONE
        binding.timerButton.setImageResource(setImageDrawableSelect(timer))
    }

    private fun setImageDrawableSelect(timer: CameraTimer) = when(timer) {
        CameraTimer.OFF -> R.drawable.ic_timer_off
        CameraTimer.SEC3 -> R.drawable.ic_timer_3_sec
        CameraTimer.SEC10 -> R.drawable.ic_timer_10_sec
    }

    fun showFlashOptions() {
        binding.flashConteiner.visibility = View.VISIBLE
    }

    fun closeFlashOptionsAndSelect(flashMode: Int) {
        imageCapture?.flashMode = flashMode
        binding.flashConteiner.visibility = View.GONE
        binding.flashButton.setImageResource(setImageDrawableFlashMode(flashMode))
    }

    private fun setImageDrawableFlashMode(flashMode: Int) = when(flashMode) {
        ImageCapture.FLASH_MODE_OFF -> R.drawable.ic_flash_off
        ImageCapture.FLASH_MODE_AUTO -> R.drawable.ic_flash_auto
        ImageCapture.FLASH_MODE_ON -> R.drawable.ic_flash_on
        else -> R.drawable.ic_flash_auto
    }


    private class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {
        private val frameRateWindow = 8
        private val frameTimestamps = ArrayDeque<Long>(5)
        private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
        private var lastAnalyzedTimestamp = 0L
        var framesPerSecond: Double = -1.0
            private set


        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }


        override fun analyze(image: ImageProxy) {
            // If there are no listeners attached, we don't need to perform analysis
            if (listeners.isEmpty()) {
                image.close()
                return
            }

            // Keep track of frames analyzed
            val currentTime = System.currentTimeMillis()
            frameTimestamps.push(currentTime)

            // Compute the FPS using a moving average
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
            val timestampLast = frameTimestamps.peekLast() ?: currentTime
            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                    frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0

            // Analysis could take an arbitrarily long amount of time
            // Since we are running in a different thread, it won't stall other use cases

            lastAnalyzedTimestamp = frameTimestamps.first

            // Since format in ImageAnalysis is YUV, image.planes[0] contains the luminance plane
            val buffer = image.planes[0].buffer

            // Extract image data from callback object
            val data = buffer.toByteArray()

            // Convert the data into an array of pixel values ranging 0-255
            val pixels = data.map { it.toInt() and 0xFF }

            // Compute average luminance for the image
            val luma = pixels.average()

            // Call all listeners with new value
            listeners.forEach { it(luma) }

            image.close()
        }

    }


    private fun uploadPhoto() {
        val photo = Photo()
      //  savedUri?.let { viewModel.uploadPhotoToStorage(it,photo) }

    }

    override fun onResume() {
        super.onResume()
        showBottomNavBar(activity)
    }


}
