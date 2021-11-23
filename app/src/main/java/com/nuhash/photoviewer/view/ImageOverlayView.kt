package com.nuhash.photoviewer.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nuhash.photoviewer.R
import com.nuhash.photoviewer.model.ImageModel


class ImageOverlayView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinatorLayout(context, attributeSet, defStyleAttr) {
    var onBackClick: () -> Unit = {}
    var onMenuClick: (Int) -> Unit = {}
    var isWorking = false

    //    var shareButton: ImageView
    var backButton: ImageView
    var author: TextView
    var bottomNavigationView: BottomNavigationView
    var progressBar: ProgressBar

    init {
        View.inflate(context, R.layout.image_overlay, this)
//        shareButton = findViewById(R.id.posterOverlayShareButton)
        author = findViewById(R.id.posterOverlayAuthor)
        backButton = findViewById(R.id.posterOverlayBackButton)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        progressBar = findViewById(R.id.progress_bar_downloading)
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun update(imageModel: ImageModel) {
        val titleTextNow = imageModel.title + " | " + imageModel.width + "*" + imageModel.height
        author.text = titleTextNow

        backButton.setOnClickListener {
            onBackClick()
        }
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_download -> {
                    if (!isWorking)
                        onMenuClick(0)
                }
                R.id.menu_shareLink -> onMenuClick(1)
                R.id.menu_shareImage -> {
                    if (!isWorking)
                        onMenuClick(2)
                }
            }
            false
        }
    }
}