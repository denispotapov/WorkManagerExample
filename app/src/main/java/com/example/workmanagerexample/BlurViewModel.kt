package com.example.workmanagerexample

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class BlurViewModel(application: Application) : AndroidViewModel(application) {

    internal var imageUri: Uri? = null

    private val workManager = WorkManager.getInstance(application)

    internal fun applyBlur(blurLevel: Int) {
        val blurRequest = OneTimeWorkRequestBuilder<BlurWorker>()
            .setInputData(createInputDataForUri())
            .build()

        workManager.enqueue(blurRequest)
    }

    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let {
            builder.putString("KEY_IMAGE_URI", imageUri.toString())
        }
        return builder.build()
    }
}