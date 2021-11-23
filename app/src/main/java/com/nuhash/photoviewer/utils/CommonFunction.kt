package com.nuhash.photoviewer.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast

object CommonFunction {
    fun toaster(activity: Activity, message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun logger(TAG: String, message: String?) {
        Log.e("NUHASH$TAG", message ?: "Message")
    }
}
