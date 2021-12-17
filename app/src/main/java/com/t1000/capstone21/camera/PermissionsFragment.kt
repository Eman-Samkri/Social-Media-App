

package com.t1000.capstone21.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.t1000.capstone21.R

private const val TAG = "PermissionsFragment"
private const val PERMISSIONS_REQUEST_CODE = 10

private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.READ_EXTERNAL_STORAGE

)

class PermissionsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasPermissions(requireContext())) {
            // Request camera-related permissions
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        } else {
            // If permissions have already been granted, proceed
            navigateToCamera()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                // Take the user to the success fragment when permission is granted
                Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
                navigateToCamera()
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToCamera() {
   //     lifecycleScope.launchWhenStarted {
        //    view?.let { Navigation.findNavController(it).navigate(R.id.action_permissions_to_photo)
          //      Log.e(TAG,"Heeeer")}

//            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(
//                    PermissionsFragmentDirections.actionPermissionsToCamera())
 //       }
    }

    companion object {
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
