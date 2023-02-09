package org.codebase.fliphorizontally

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_controller.*
import org.codebase.fliphorizontally.filespath.FilePickerHelper
import org.codebase.fliphorizontally.utils.App
import java.io.File

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : AppCompatActivity() {

    private var playbackPosition = 0L
    private var playWhenReady = true

    private var context: Context? = null

    private var videoPath: String = ""
    private var videoUri: Uri? = null
    lateinit var video: File
    private var videoGallery: File? = null
    private var isGranted = false

//    private lateinit var binding: ActivityMainBinding
    private var exoPlayer: ExoPlayer? = null
    var mOrientationListener: OrientationEventListener? = null

//    private lateinit var imageViewFullScreen: ImageView
//    private lateinit var imageViewLock: ImageView
//    private lateinit var linearLayoutControlUp: LinearLayout
//    private lateinit var linearLayoutControlBottom: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFullScreen()
        setLockScreen()
        preparePlayer()

        buttonId.setOnClickListener {
            player.scaleX = 1f
        }

        buttonIdFlip.setOnClickListener {
            player.scaleX = -1f
        }

        pickVideoButtonId.setOnClickListener {
            pickVideo()
        }
        setOrientation()
    }

    private fun preparePlayer() {
        exoPlayer = ExoPlayer.Builder(this).setSeekBackIncrementMs(INCREMENT_MILLIS)
            .setSeekForwardIncrementMs(INCREMENT_MILLIS)
            .build()
        exoPlayer?.playWhenReady = true
        player.player = exoPlayer

        exoPlayer?.apply {
            player.scaleX = -1f
            setMediaItem(MediaItem.fromUri(App.getString("video_uri")))
//            setMediaSource(buildMediaSource(getPath))
            seekTo(playbackPosition)
            playWhenReady = playWhenReady
            prepare()
        }
    }

    //creating mediaSource
    private fun buildMediaSource(url: String?): MediaSource {
        // Create a data source factory.
        val dataSourceFactory: DefaultHttpDataSource.Factory = DefaultHttpDataSource.Factory()

        // Create a progressive media source pointing to a stream uri.

        return ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(url!!))
    }

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            var isPlay = false

            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    videoUri = intent.data!!

                    videoPath()

                    App.saveString("video_uri", videoUri.toString())

                    preparePlayer()

                    //                        val videoView = videoViewId
                    //                        videoView.visibility = View.VISIBLE
                    //                        videoView.setVideoPath(videoUri.toString())
                    //
                    //                        android.util.Log.e("Uri0", videoUri.toString())
                    //                        val mediaController = MediaController(this@MainActivity)
                    //                        mediaController.setAnchorView(videoView)
                    //                        videoView.setMediaController(mediaController)
                    //
                    //                        videoView.setOnPreparedListener {
                    //
                    //                            videoDurationTextId.text = "Duration: ${Utils.milliSecondsToTimer(videoView.duration.toLong())}\n"
                    //                            isPlay = true
                    //                        }
                    //
                    //                        videoView.setOnClickListener {
                    //                            isPlay = if (isPlay) {
                    //                                videoView.pause()
                    //                                false
                    //                            } else {
                    //                                videoView.start()
                    //                                true
                    //                            }
                    //                        }
                    //                        videoView.setOnCompletionListener {
                    //                            videoView.pause()
                    //                        }
                    //
                    //                        videoView.start()

                }
            }
        }

    private fun pickVideo() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            galleryLauncher.launch(intent)
        } else {
            checkPermissions()
        }
    }


    private fun videoPath() {
        videoPath = FilePickerHelper.getPath(applicationContext, videoUri!!).toString()
        videoGallery = File(videoPath)
    }

    private fun setOrientation() {
        mOrientationListener = object : OrientationEventListener(
            this,
            SensorManager.SENSOR_DELAY_NORMAL
        ) {
            override fun onOrientationChanged(orientation: Int) {
                Log.e(
                    "DEBUG_TAG",
                    "Orientation changed to $orientation"
                )

                when (orientation) {
                    in 1..89 -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                            buttonId.visibility = View.INVISIBLE
                        }
                    }
                    in 180 .. 360 -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            buttonId.visibility = View.INVISIBLE
                        }
                    }
                    in 90 .. 180 -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            buttonId.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun lockScreen(lock: Boolean) {
        if (lock) {
            linearLayoutControlUp.visibility = View.INVISIBLE
            linearLayoutControlBottom.visibility = View.INVISIBLE
        } else {
            linearLayoutControlUp.visibility = View.VISIBLE
            linearLayoutControlBottom.visibility = View.VISIBLE
        }
    }

    private fun setLockScreen() {
        imageViewLock.setOnClickListener {
            if (!isLock) {
                imageViewLock.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_lock
                    )
                )
            } else {
                imageViewLock.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_lock_open
                    )
                )
            }
            isLock = !isLock
            lockScreen(isLock)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setFullScreen() {
        imageViewFullScreen.setOnClickListener {

            if (!isFullScreen) {
                imageViewFullScreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_fullscreen_exit
                    )
                )
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

                buttonId.visibility = View.GONE
                buttonIdFlip.visibility = View.GONE
                pickVideoButtonId.visibility = View.GONE
//                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
//                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                supportActionBar?.hide()
//                window.requestFeature(Window.FEATURE_ACTION_BAR)
//                supportActionBar?.hide()
            } else {
                imageViewFullScreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_fullscreen
                    )
                )
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                buttonId.visibility = View.VISIBLE
                buttonIdFlip.visibility = View.VISIBLE
                pickVideoButtonId.visibility = View.VISIBLE

            }
            isFullScreen = !isFullScreen
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissions() {
        //Ask for permissions
        val externalStorageReadPermission: Int = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        val externalStorageWritePermission: Int = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_MEDIA_VIDEO)
        val listPermissionNeeded: ArrayList<String> = ArrayList()

        if (externalStorageReadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (externalStorageWritePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.READ_MEDIA_VIDEO)
        }
        if (listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toTypedArray(), 1)
        } else {
            isGranted = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                val permissionMap : HashMap<String, Int> = HashMap()

                // Initialize the map with permissions
                permissionMap[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                permissionMap[Manifest.permission.READ_MEDIA_VIDEO] = PackageManager.PERMISSION_GRANTED

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) {
                        permissionMap[permissions[i]] = grantResults[i]
                    }
                    if (permissionMap[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        || permissionMap[Manifest.permission.READ_MEDIA_VIDEO] == PackageManager.PERMISSION_GRANTED
                    ) {
                        isGranted = true
                        pickVideo()
                    } else {
                        isGranted = false
                        android.util.Log.d("Permissions", "Some permissions not granted ask again")

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_VIDEO)) {
                            showDialogOK("Storage Permission required for this app") { dialog, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> checkPermissions()
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                        dialog.dismiss()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    //Dialog for permissions if permission not granted
    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isLock) return
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageViewFullScreen.performClick()
        } else super.onBackPressed()
    }


    override fun onStop() {
        super.onStop()
        exoPlayer?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }

    companion object {
        private const val URL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

        private var isFullScreen = false
        private var isLock = false
        private const val INCREMENT_MILLIS = 5000L
    }

}