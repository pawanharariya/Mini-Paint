package com.example.minipaint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.minipaint.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val colorRadioGroup = binding.paintColorPicker.root
        colorRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val paintId = when (checkedId) {
                R.id.color_black -> R.color.paint_black
                R.id.color_white -> R.color.paint_white
                R.id.color_green -> R.color.paint_green
                R.id.color_red -> R.color.paint_red
                R.id.color_blue -> R.color.paint_blue
                R.id.color_yellow -> R.color.paint_yellow
                R.id.color_orange -> R.color.paint_orange
                R.id.color_purple -> R.color.paint_purple
                else -> R.color.default_paint_color
            }
            binding.canvas.onPaintColorChanged(paintId)
        }
        hideSystemUI()
    }

    /**
     * Hides the System UI to show current screen as full screen
     */
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}