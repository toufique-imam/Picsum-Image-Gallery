package com.nuhash.photoviewer.utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.Volley
import com.nuhash.photoviewer.utils.CommonFunction.logger
import java.net.HttpURLConnection
import java.net.URL

class PicsumRequest(val context: Context, private var url: String) {
    lateinit var onComplete: OnScrappingCompleted
    var mRequestQueue: RequestQueue? = null
    var textFileUtils = TextFileUtils(context)

    fun updateUrl(url: String) {
        this.url = url
    }

    fun deletePrevious() {
        logger("deletePrev", "called")
        textFileUtils.saveM3UFile(url, "")
    }

    fun startScrapping() {
        val savedData = textFileUtils.getSavedM3UFile(url)
        logger("saved", savedData)
        if (savedData.isNotEmpty()) {
            onComplete.onComplete(savedData)
            return
        }
        logger("startScrapping", "download")
        val headers = HashMap<String, String>()

        val localData = CustomRequest(
            Request.Method.GET, url, headers,
            { response ->
                textFileUtils.saveM3UFile(url, response)
                onComplete.onComplete(response)
            },
            {
                if (url.startsWith("http:")) {
                    url = url.replace("http:", "https:")
                    startScrapping()
                } else {
                    onComplete.onError()
                }
            }
        )
        addToRequestQueue(localData)
    }

    private fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        getRequestQueue().add(req)
    }

    private fun getRequestQueue(): RequestQueue {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this.context, object : HurlStack() {
                override fun createConnection(url: URL?): HttpURLConnection {
                    val connection = super.createConnection(url)
                    connection.instanceFollowRedirects = true
                    return connection
                }
            })
        }
        return mRequestQueue!!
    }

    fun onFinish(onScrappingCompleted: OnScrappingCompleted) {
        onComplete = onScrappingCompleted
    }

    companion object {
        private const val TAG = "APP"
    }
}

interface OnScrappingCompleted {
    fun onComplete(response: String)
    fun onError()
}