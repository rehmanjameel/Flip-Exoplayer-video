package org.codebase.fliphorizontally

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.codebase.fliphorizontally.databinding.ActivityStartBinding

class StartActivity : Activity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        // Check if device is Android 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
            startMainActivity()
        } else {
            // Use the traditional method for older versions
            Handler(Looper.getMainLooper()).postDelayed({ this.startMainActivity() }, 2000)
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, DashBoardActivity::class.java)
        startActivity(intent)
        finish()
    }
}