package com.hemanth.fcmsample
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString("title")
        val message = inputData.getString("message")

        Log.d(TAG, "Executing long task")
        Log.d(TAG, "Title: $title")
        Log.d(TAG, "Message: $message")

        // Simulate long work (API call, DB sync, etc.)
        Thread.sleep(3000)

        return Result.success()
    }

    companion object {
        private const val TAG = "MyWorker"
    }
}
