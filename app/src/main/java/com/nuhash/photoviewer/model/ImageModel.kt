package com.nuhash.photoviewer.model

import java.io.Serializable

class ImageModel(
    val id: String = "", val author: String = "", val height: Int = -1,val  width: Int = -1,
    val title: String = "", val link: String = ""
) : Serializable {

}