package org.codebase.fliphorizontally

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.codebase.fliphorizontally.databinding.ActivityGuideBinding

class GuideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}