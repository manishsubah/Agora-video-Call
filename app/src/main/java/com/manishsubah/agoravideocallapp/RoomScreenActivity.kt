package com.manishsubah.agoravideocallapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.manishsubah.agoravideocallapp.databinding.ActivityRoomScreenAcitivityBinding

class RoomScreenActivity : AppCompatActivity() {
    private val permissionReqId = 22
    private lateinit var binding: ActivityRoomScreenAcitivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomScreenAcitivityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (checkPermissions()) {
            initializeAndJoinChannel()
        } else {
            ActivityCompat.requestPermissions(this, getRequiredPermissions(), permissionReqId)
        }

        val channelName = binding.editText.text

        binding.connectionBtn.setOnClickListener {
            // Create an Intent to navigate to the second activity
            val intent = Intent(this, MainActivity::class.java)
            // Add the channel name as extra data
            intent.putExtra("CHANNEL_NAME", channelName)
            // Start the second activity
            startActivity(intent)
        }

    }

    private fun initializeAndJoinChannel() {

    }

    // Obtain recording, camera and other permissions required to implement real-time audio and video interaction
    private fun getRequiredPermissions(): Array<String> {
        // Determine the permissions required when targetSDKVersion is 31 or above
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.RECORD_AUDIO, // Recording permission
                Manifest.permission.CAMERA, // Camera permission
                Manifest.permission.READ_PHONE_STATE, // Permission to read phone status
                Manifest.permission.BLUETOOTH_CONNECT // Bluetooth connection permission
            )
        } else {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
            )
        }
    }

    private fun checkPermissions(): Boolean {
        for (permission in getRequiredPermissions()) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, permission)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

}