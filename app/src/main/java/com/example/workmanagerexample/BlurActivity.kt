package com.example.workmanagerexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.workmanagerexample.databinding.ActivityBlurBinding
import timber.log.Timber

class BlurActivity : AppCompatActivity() {

    lateinit var binding: ActivityBlurBinding
    private lateinit var blurViewModel: BlurViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur)
        binding = ActivityBlurBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        blurViewModel = ViewModelProvider(this).get(BlurViewModel::class.java)

        binding.goButton.setOnClickListener { blurViewModel.applyBlur(blurLevel) }
    }

    private val blurLevel: Int
        get() =
            when (binding.radioBlurGroup.checkedRadioButtonId) {
                R.id.radio_blur_lv_1 -> 1
                R.id.radio_blur_lv_2 -> 2
                R.id.radio_blur_lv_3 -> 3
                else -> 1
            }
}