package com.nuhash.photoviewer.model

interface ProgressListener {
    fun onProgressUpdate(percent: Int)
    fun onDownloadDone()
    fun onDownloadStarted()
}