package com.t1000.capstone21

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.t1000.capstone21.databinding.FragmentCameraBinding

//const val KEY_EVENT_ACTION = "key_event_action"
//const val KEY_EVENT_EXTRA = "key_event_extra"
//private const val IMMERSIVE_FLAG_TIMEOUT = 500L

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

     //   binding = FragmentCameraBinding.bind(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentCameraBinding.inflate(layoutInflater)
    }


//    override fun onResume() {
//        super.onResume()
//        binding.fragmentContainer.postDelayed({
//            hideSystemUI()
//        }, IMMERSIVE_FLAG_TIMEOUT)
//    }


//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        return when (keyCode) {
//            KeyEvent.KEYCODE_VOLUME_DOWN -> {
//                val intent = Intent(KEY_EVENT_ACTION).apply { putExtra(KEY_EVENT_EXTRA, keyCode) }
//                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//                true
//            }
//            else -> super.onKeyDown(keyCode, event)
//        }
//    }
//
//    override fun onBackPressed() {
//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
//            finishAfterTransition()
//        } else {
//            super.onBackPressed()
//        }
//    }
//
//
//    private fun hideSystemUI() {
//        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
//        WindowInsetsControllerCompat(requireActivity().window, binding.fragmentContainer).let { controller ->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }
}