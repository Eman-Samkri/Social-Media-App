package com.t1000.capstone21.camera.videoFragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.t1000.capstone21.KEY_EVENT_EXTRA
import com.t1000.capstone21.R
import com.t1000.capstone21.models.Video
import com.t1000.capstone21.camera.baseFragment.BaseFragment
import com.t1000.capstone21.camera.baseFragment.BaseViewModel
import com.t1000.capstone21.camera.photoFragment.EXTENSION_WHITELIST
import com.t1000.capstone21.databinding.FragmentVideoBinding
import com.t1000.capstone21.utils.simulateClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "VideoFragment"

class VideoFragment() : BaseFragment<FragmentVideoBinding>(){

override val binding: FragmentVideoBinding by lazy {
    FragmentVideoBinding.inflate(layoutInflater)
}

    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA
    private var videoCapture:VideoCapture? = null
    private var isRecording = false
    private var timer:Timer?=null
    private var recoerSecondFlashd = 0

    var savedUri :Uri? = null



    private val viewModel by lazy { ViewModelProvider(this).get(BaseViewModel::class.java) }

    private val viewModel2 by lazy { ViewModelProvider(this).get(VideoFragmentVM::class.java) }

    override  val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                // When the volume down button is pressed, simulate a shutter button click
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    binding.cameraCaptureButton.simulateClick()
                }
            }
        }
    }
    override  val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit


        @SuppressLint("UnsafeOptInUsageError", "RestrictedApi")
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@VideoFragment.displayId) {
                Log.d(TAG, "Rotation changed: ${view.display.rotation}")
                preview?.targetRotation = view.display.rotation
                videoCapture?.setTargetRotation(view.display.rotation)
            }
        } ?: Unit
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragment = this

        setupView()

        postViewFinder(binding.viewFinder)

        gestureListener(binding.viewFinder){
            Navigation.findNavController(view).navigate(R.id.action_video_to_camera)
        }

    }

    override fun updateCameraUi() {
        lifecycleScope.launch(Dispatchers.IO) {
            outputDirectory.listFiles { file ->
                EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
            }?.maxOrNull()?.let {
                setGalleryThumbnail(binding.photoViewButton, Uri.fromFile(it))
            }
        }

        binding.cameraSwitchButton.isEnabled = false
    }

    override fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            lensFacing = when {
                hasBackCamera() -> CameraSelector.DEFAULT_BACK_CAMERA
                hasFrontCamera() -> CameraSelector.DEFAULT_FRONT_CAMERA
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            // Enable or disable switching between cameras
            updateCameraSwitchButton(binding.cameraSwitchButton)

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("RestrictedApi", "NewApi")
    override fun bindCameraUseCases() {
       val metrics = windowManager.getCurrentWindowMetrics().bounds

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val screenAspectRatio = aspectRatio(metrics.width(), metrics.height())
            val rotation = binding.viewFinder.display.rotation
            val cameraProvider = cameraProvider?: throw java.lang.IllegalStateException("camera ini failed")

            preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

            val videoCaptureConfing = VideoCapture.DEFAULT_CONFIG.config
            videoCapture = VideoCapture.Builder
                .fromConfig(videoCaptureConfing)
                .build()
            cameraProvider.unbindAll()

            try {
                // A variable number of use-cases can be passed here -
                // camera provides access to CameraControl & CameraInfo
                camera = cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                lensFacing,
                preview,
                videoCapture)

                // Attach the viewfinder's surface provider to preview use case
                preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        },ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("RestrictedApi")
    fun recordVideo(){
        val localVideoCapture = videoCapture ?: throw java.lang.IllegalStateException("camera failed")
// TODO: to Repo
        val model = Video()
        val videoFile = viewModel.getVideoFile(model)
        viewModel2.initVideo(System.currentTimeMillis())

        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis())
                put(MediaStore.MediaColumns.MIME_TYPE,"video/mp4")
                put(MediaStore.MediaColumns.RELATIVE_PATH, outputDirectory.path)
            }

            requireContext().contentResolver.run {
                val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                VideoCapture.OutputFileOptions.Builder(this, contentUri,contentValues)
            }
        }else{
            outputDirectory.mkdir()
            VideoCapture.OutputFileOptions.Builder(videoFile)
        }.build()

        if (!isRecording){
            startTimer()

            localVideoCapture.startRecording(
                outputOptions,
                cameraExecutor,
                object :VideoCapture.OnVideoSavedCallback{

                    override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                        savedUri = outputFileResults.savedUri ?: Uri.fromFile(videoFile)
                        uploadVideo()
                        Log.d(TAG, "Photo capture succeeded: $savedUri")

                        // We can only change the foreground Drawable using API level 23+ API
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // Update the gallery thumbnail with latest picture taken
                            savedUri?.let { setGalleryThumbnail(binding.photoViewButton , it) }
                        }

                        // Implicit broadcasts will be ignored for devices running API level >= 24
                        // so if you only target API level 24+ you can remove this statement
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            requireActivity().sendBroadcast(
                                Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri)
                            )
                        }

                        val mimeType = MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(savedUri?.toFile()?.extension)
                        MediaScannerConnection.scanFile(
                            context,
                            arrayOf(savedUri?.toFile()?.absolutePath),
                            arrayOf(mimeType)
                        ) { _, uri ->
                            Log.d(TAG, "video capture scanned into media store: $uri")
                        }
                    }

                    override fun onError(
                        videoCaptureError: Int,
                        message: String,
                        cause: Throwable?
                    ) {
                        Toast.makeText(requireContext(), "videwo recording faild",Toast.LENGTH_LONG).show()
                    }

                }
            )
        }else{
            restTimer()
            stopTimer()
            localVideoCapture.stopRecording()
        }
        isRecording = !isRecording

    }

    private fun startTimer(){
    stopTimer()
        timer = Timer()
        binding.countdown.visibility = View.VISIBLE
        timer?.scheduleAtFixedRate(object :TimerTask(){
            override fun run() {
                updateTimer()
            }
        },0,1000)

  }

    private fun stopTimer(){
        if(timer != null){
            timer?.cancel()
            timer?.purge()
            timer = null
        }
    }

    private fun updateTimer(){
        Handler(Looper.getMainLooper()).post{
            if (isRecording){
                recoerSecondFlashd ++
                binding.countdown.text = "".formatSeconds(recoerSecondFlashd)
            }
        }

    }

    private fun restTimer(){
        recoerSecondFlashd = 0
        binding.countdown.text = "".formatSeconds(recoerSecondFlashd)
        binding.countdown.visibility= View.GONE
        stopTimer()
    }

    fun showFlashOptions(){

        binding.flashConteiner.visibility = View.VISIBLE

    }

    fun closeFlashOptionsAndSelect(active: Boolean) {
        camera?.cameraControl?.enableTorch(active)
        binding.flashConteiner.visibility = View.GONE
        binding.flashButton.setImageResource(setImageDrawableFlashMode(active))
    }

    private fun setImageDrawableFlashMode(active: Boolean) =
        if(active) {
            R.drawable.ic_flash_on
        }else{
            R.drawable.ic_flash_off
        }



    fun switchCamera() {
        lensFacing = if (CameraSelector.DEFAULT_FRONT_CAMERA == lensFacing) {
            binding.cameraSwitchButton.setImageResource(R.drawable.ic_camera_front)
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            binding.cameraSwitchButton.setImageResource(R.drawable.ic_camera_rear)
            CameraSelector.DEFAULT_FRONT_CAMERA
        }
        bindCameraUseCases()
    }




    fun String.formatSeconds(seconds:Int):String?{
        return (getTowDecimalsValue(seconds/3600)+":" +
                getTowDecimalsValue(seconds/60)+":" +
                getTowDecimalsValue(seconds%60))
    }

    private fun getTowDecimalsValue(value :Int):String{
        return if(value in 0..9) {"0$value"}
        else {value.toString()}
    }

    private fun uploadVideo() {
        val video = Video()
        savedUri?.let { viewModel2.uploadVideo(it,video) }

    }
}