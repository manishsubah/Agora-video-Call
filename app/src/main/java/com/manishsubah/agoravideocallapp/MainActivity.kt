package com.manishsubah.agoravideocallapp

import android.os.Bundle
import android.view.SurfaceView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.manishsubah.agoravideocallapp.databinding.ActivityMainBinding
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class MainActivity : AppCompatActivity() {
    // Fill in the App ID obtained from the Agora Console
    private val myAppId = "a8abbef577324e64a57380af96c343b6"//a8abbef577324e64a57380af96c343b6
    // Fill in the channel name
    private var channelName = "subah"
    // Fill in the temporary token generated from Agora Console
    private val token = "007eJxTYNhyPNyi+Ov/q7WzuVe+bFBgMG/OE+hPnLLcdNrRquc3ZrAoMCRaJCYlpaaZmpsbG5mkmpkkmpobWxgkplmaJRubGCeZhSVOSm8IZGSIvjuZmZEBAkF8Vobi0qTEDAYGALziIKk="
    private var mRtcEngine: RtcEngine? = null

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //channelName = intent.getStringExtra("CHANNEL_NAME").toString()

        // Create an RtcEngineConfig instance and configure it
        val config = RtcEngineConfig().apply {
            mContext = applicationContext
            mAppId = myAppId
            mEventHandler = mRtcEventHandler
        }
        // Create and initialize an RtcEngine instance
        mRtcEngine = RtcEngine.create(config)

        // Enable the video module
        mRtcEngine?.enableVideo()

        // Create a SurfaceView object and make it a child object of FrameLayout
        val container = binding.localUser
        val surfaceView = SurfaceView(applicationContext)
        container.addView(surfaceView)
        // Pass the SurfaceView object to the SDK and set the local view
        mRtcEngine?.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))

        // Enable local preview
        mRtcEngine?.startPreview()

        // Create an instance of ChannelMediaOptions and configure it
        val options = ChannelMediaOptions().apply {
            // Set the user role to BROADCASTER or AUDIENCE according to the use-case
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            // In the live broadcast use-case, set the channel profile to COMMUNICATION (live broadcast use-case)
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
        }
        // Use the temporary token to join the channel
        // Specify the user ID yourself and ensure it is unique within the channel
        mRtcEngine?.joinChannel(token, channelName, 0, options)
    }

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Callback when successfully joining the channel
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Join channel success", Toast.LENGTH_SHORT).show()
            }
        }
        // Callback when a remote user or host joins the current channel
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                // When a remote user joins the channel, display the remote video stream for the specified uid
                setupRemoteVideo(uid)
            }
        }
        // Callback when a remote user or host leaves the current channel
        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                Toast.makeText(this@MainActivity, "User offline: $uid", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupRemoteVideo(uid: Int) {
        val container = binding.foreignUser
        val surfaceView = SurfaceView(applicationContext).apply {
            setZOrderMediaOverlay(true)
        }
        container.addView(surfaceView)
        // Pass the SurfaceView object to the SDK and set the remote view
        mRtcEngine?.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop local video preview
        mRtcEngine?.stopPreview()
        // Leave the channel
        mRtcEngine?.leaveChannel()
    }
}

