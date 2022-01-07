package com.t1000.capstone21.camera.baseFragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.camera.core.*
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewbinding.ViewBinding
import androidx.window.WindowManager
import com.t1000.capstone21.KEY_EVENT_ACTION
import com.t1000.capstone21.utils.SwipeGestureDetector
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

abstract class BaseFragment <B:ViewBinding> :Fragment() {

    open var displayId: Int = -1
    open var preview: Preview? = null
    open var camera: Camera? = null
    open var cameraProvider: ProcessCameraProvider? = null
    open lateinit var windowManager: WindowManager

    open lateinit var broadcastManager: LocalBroadcastManager

    abstract val binding : B
    abstract val volumeDownReceiver :BroadcastReceiver
    abstract val displayListener:DisplayManager.DisplayListener

    private val baseViewModel by lazy { ViewModelProvider(this).get(CameraViewModel::class.java) }

    open val displayManager by lazy { requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager}


    open val outputDirectory : File by lazy { baseViewModel.getFileDir() }

    open lateinit var cameraExecutor:ExecutorService


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

//    override fun onResume() {
//        super.onResume()
//        // Make sure that all permissions are still present, since the
//        // user could have removed them while the app was in paused state.
//        if (!CameraPermissions.hasPermissions(requireContext())) {
//            Navigation.findNavController(requireActivity(),R.id.).navigate(
//                CameraPermissionsDirections.actionPermissionsToCamera()
//            )
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Shut down our background executor
        cameraExecutor.shutdown()

        // Unregister the broadcast receivers and listeners
        broadcastManager.unregisterReceiver(volumeDownReceiver)
        displayManager.unregisterDisplayListener(displayListener)
    }

    fun setupView(){
        //Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        view?.let {
            broadcastManager = LocalBroadcastManager.getInstance(it.context)
            windowManager = WindowManager(it.context)

        }

        // Set up the intent filter that will receive events from our main activity
        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        broadcastManager.registerReceiver(volumeDownReceiver, filter)

        displayManager.registerDisplayListener(displayListener, null)

    }

    fun postViewFinder(viewFinder: PreviewView){
        viewFinder.post {

            // Keep track of the display in which this view is attached
            displayId = viewFinder.display.displayId

            // Build UI controls
           // updateCameraUi()

            setUpCamera()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun gestureListener(viewFinder: PreviewView, callable: ()->Unit){

        val swipeGestureDetector = SwipeGestureDetector().apply {
            setSwipeCallback(left = {
                callable.invoke()
            })
        }
            val gestureDetectorCompat = GestureDetector(requireContext(),swipeGestureDetector)
            viewFinder.setOnTouchListener{ v, event ->
                if (gestureDetectorCompat.onTouchEvent(event)) return@setOnTouchListener false
                    return@setOnTouchListener true
            }
         }

    //abstract fun updateCameraUi()

    abstract fun setUpCamera()

    abstract fun bindCameraUseCases()

    //TODO:benefit load imge

//    fun setGalleryThumbnail(imageButton: ImageButton, uri:Uri){
//            imageButton.post {
//                // Remove thumbnail padding
//                imageButton.setPadding(2)
//
//                // Load thumbnail into circular button using Glide
//                Glide.with(imageButton)
//                    .load(uri)
//                    .apply(RequestOptions.circleCropTransform())
//                    .into(imageButton)
//
//        }
//    }


    open fun updateCameraSwitchButton(cameraSwitchButton: ImageButton) {
        try {
            cameraSwitchButton?.isEnabled = hasBackCamera() && hasFrontCamera()
        } catch (exception: CameraInfoUnavailableException) {
            cameraSwitchButton?.isEnabled = false
        }
    }


    open fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    open fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    open fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    companion object {
         const val RATIO_4_3_VALUE = 4.0 / 3.0
         const val RATIO_16_9_VALUE = 16.0 / 9.0

    }


}