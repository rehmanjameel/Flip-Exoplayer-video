package org.codebase.fliphorizontally

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.codebase.fliphorizontally.databinding.ActivityDashBoardBinding
import org.codebase.fliphorizontally.filespath.FilePickerHelper
import org.codebase.fliphorizontally.utils.App
import java.io.File

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class DashBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardBinding
    private var isGranted = false
    private var videoPath: String = ""
    private var videoUri: Uri? = null
    private var videoGallery: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pickVideoButtonId.setOnClickListener { pickVideo() }

        binding.seeGuideId.setOnClickListener {
            startActivity(Intent(this, GuideActivity::class.java))
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
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }

    private fun videoPath() {
        videoPath = FilePickerHelper.getPath(applicationContext, videoUri!!).toString()
        videoGallery = File(videoPath)
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
}