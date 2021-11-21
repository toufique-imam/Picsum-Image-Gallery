package com.nuhash.photoviewer.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.nuhash.photoviewer.R
import com.nuhash.photoviewer.model.ImageModel


class ImageOverlayView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    var onBackClick: () -> Unit = {}

    //    var shareButton: ImageView
    var backButton: ImageView
    var title: TextView

    init {
        View.inflate(context, R.layout.image_overlay, this)
//        shareButton = findViewById(R.id.posterOverlayShareButton)
        title = findViewById(R.id.posterOverlayDescriptionText) as TextView
        backButton = findViewById(R.id.posterOverlayBackButton) as ImageView
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun update(imageModel: ImageModel) {
        title.text = imageModel.title

        backButton.setOnClickListener {
            onBackClick()
        }
    }
}