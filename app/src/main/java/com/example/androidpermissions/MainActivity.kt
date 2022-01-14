package com.example.androidpermissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private val TAG = "PermissionRequest"
    private val REQUEST_CODE_PERMISSIONS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnRequestPermissions).setOnClickListener {
            requestPermissions()
        }
    }

    private fun hasWriteExternalStoragePermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasLocationForegroundPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun hasLocationBackgroundPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Requesting multiple permissions at the same time from user.
     * */
    private fun requestPermissions() {
        val permissionRequest = mutableListOf<String>()
        if (!hasWriteExternalStoragePermission()) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!hasLocationForegroundPermission()) {
            permissionRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionRequest.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    /**
     * For API 30 and above, background location permission is only allowed after
     * user has granted foreground location permission.
     * */
    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            hasLocationForegroundPermission() && !hasLocationBackgroundPermission()
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        /**
         * grantResults contain an array of integers denoting
         * if a certain permission is granted or not.
         * */
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"${permissions[i]} granted.")
                }
            }

            requestBackgroundLocationPermission()
        }
    }
}