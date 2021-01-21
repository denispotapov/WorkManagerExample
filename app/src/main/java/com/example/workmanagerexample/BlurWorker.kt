package com.example.workmanagerexample

import android.content.Context
import android.graphics.BitmapFactory
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber

class BlurWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        val appContext = applicationContext
        makeStatusNotification("Blurring image", appContext)

        try {
            val picture = BitmapFactory.decodeResource(
                appContext.resources,
                R.drawable.test
            )

            val output = blurBitmap(picture, appContext)
            val outputUri = writeBitmapToFile(appContext, output)

            makeStatusNotification("Output is $outputUri", appContext)

            return Result.success()

        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            return Result.failure()
        }
    }
}