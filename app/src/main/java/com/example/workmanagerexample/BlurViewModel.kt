package com.example.workmanagerexample

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.example.workmanagerexample.workers.BlurWorker
import com.example.workmanagerexample.workers.CleanupWorker
import com.example.workmanagerexample.workers.SaveImageToFileWorker

class BlurViewModel(application: Application) : AndroidViewModel(application) {

    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null
    private val workManager = WorkManager.getInstance(application)

    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    init {
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

    internal fun applyBlur(blurLevel: Int) {

        var continuation = workManager
            .beginUniqueWork(
                IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker::class.java)
            )

        for (i in 0 until blurLevel) {
            val blurRequest = OneTimeWorkRequest.Builder(BlurWorker::class.java)

            if (i == 0) {
                blurRequest.setInputData(createInputDataForUri())
            }
            continuation = continuation.then(blurRequest.build())
        }

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        val save = OneTimeWorkRequest.Builder(SaveImageToFileWorker::class.java)
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT)
            .build()
        continuation = continuation.then(save)

        continuation.enqueue()

    }

    internal fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    internal fun setImageUri(uri: String?) {
        imageUri = uriOrNull(uri)
    }

    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let {
            builder.putString("KEY_IMAGE_URI", imageUri.toString())
        }
        return builder.build()
    }
}