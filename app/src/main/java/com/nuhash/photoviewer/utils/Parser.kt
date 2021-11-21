package com.nuhash.photoviewer.utils

import com.nuhash.photoviewer.model.ImageModel
import com.nuhash.photoviewer.utils.CommonFunction.logger
import org.json.JSONArray

object Parser {
    fun picsumParser(data: String): ArrayList<ImageModel> {
        val result = ArrayList<ImageModel>()
        try {
            val jsonArray = JSONArray(data)
            for (i in 0 until jsonArray.length()) {
                val jsonobject = jsonArray.getJSONObject(i)
                val id = jsonobject.getString("id")
                val author = jsonobject.getString("author")
                val width = jsonobject.getInt("width")
                val height = jsonobject.getInt("height")
                val url = jsonobject.getString("url")
                val downloadUrl = jsonobject.getString("download_url")
                val imageModel = ImageModel(id, url, height, width, author, downloadUrl)
                result.add(imageModel)
            }
        } catch (e: Exception) {
            logger("picsumParser", e.message)
        }
        return result
    }
}