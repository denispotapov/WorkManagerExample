package com.example.workmanagerexample

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.workmanagerexample.workers.BlurWorker
import com.example.workmanagerexample.workers.CleanupWorker
import com.example.workmanagerexample.workers.SaveImageToFileWorker

class BlurViewModel(application: Application) : AndroidViewModel(application) {

    internal var imageUri: Uri? = null
    private val workManager = WorkManager.getInstance(application)

    internal fun applyBlur(blurLevel: Int) {

        var continuation = workManager.beginWith(OneTimeWorkRequest.from(CleanupWorker::class.java))

        for (i in 0 until blurLevel) {
            val blurRequest = OneTimeWorkRequest.Builder(BlurWorker::class.java)

            if (i == 0) {
                blurRequest.setInputData(createInputDataForUri())
            }
            continuation = continuation.then(blurRequest.build())
        }

        val save = OneTimeWorkRequest.Builder(SaveImageToFileWorker::class.java).build()

        continuation = continuation.then(save)

        continuation.enqueue()

    }

    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let {
            builder.putString("KEY_IMAGE_URI", imageUri.toString())
        }
        return builder.build()
    }

    internal fun setImageUri(uri: String?) {
        imageUri = uriOrNull(uri)
    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }
}