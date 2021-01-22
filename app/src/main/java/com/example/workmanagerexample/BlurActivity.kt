package com.example.workmanagerexample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
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

        val imageUriExtra = intent.getStringExtra("KEY_IMAGE_URI")
        blurViewModel.setImageUri(imageUriExtra)
        blurViewModel.imageUri?.let { imageUri ->
            Glide.with(this).load(imageUri).into(binding.imageView)
        }

        binding.goButton.setOnClickListener { blurViewModel.applyBlur(blurLevel) }

        blurViewModel.outputWorkInfos.observe(this, workInfosObserver())

    }

    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            val workInfo = listOfWorkInfo[0]

            if (workInfo.state.isFinished) {
                showWorkFinished()

                val outputImageUri = workInfo.outputData.getString("KEY_IMAGE_URI")
                if (!outputImageUri.isNullOrEmpty()) {
                    blurViewModel.setOutputUri(outputImageUri)
                    binding.seeFileButton.visibility = View.VISIBLE
                }
            } else {
                showWorkInProgress()
            }
        }
    }

    private fun showWorkInProgress() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
            goButton.visibility = View.GONE
            seeFileButton.visibility = View.GONE
        }
    }

    private fun showWorkFinished() {
        with(binding) {
            progressBar.visibility = View.GONE
            cancelButton.visibility = View.GONE
            goButton.visibility = View.VISIBLE
        }
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