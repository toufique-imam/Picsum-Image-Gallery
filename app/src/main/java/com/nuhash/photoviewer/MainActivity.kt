package com.nuhash.photoviewer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nuhash.photoviewer.adapter.GalleryAdapter
import com.nuhash.photoviewer.model.ImageModel
import com.nuhash.photoviewer.model.ProgressListener
import com.nuhash.photoviewer.utils.*
import com.nuhash.photoviewer.utils.CommonFunction.toaster
import com.nuhash.photoviewer.view.ImageOverlayView
import com.stfalcon.imageviewer.StfalconImageViewer


class MainActivity : AppCompatActivity() {
    private lateinit var gallery: RecyclerView
    lateinit var galleryAdapter: GalleryAdapter
    lateinit var gridLayoutManager: GridLayoutManager
    var pageNow = 0
    lateinit var picsumRequest: PicsumRequest
    lateinit var onScrappingCompleted: OnScrappingCompleted
    val listLoadMore = ArrayList<ImageModel>()
    var overlayView: ImageOverlayView? = null
    lateinit var progressBar: ProgressBar
    var imageViewer: StfalconImageViewer<ImageModel>? = null
    var prevsize: Int = -1
    lateinit var clipboardManager: ClipboardManager


    var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        initView()
    }

    private fun initPicsum() {
        onScrappingCompleted = object : OnScrappingCompleted {
            override fun onComplete(response: String) {
                isLoading = false
                progressBar.visibility = View.GONE
                if (safeGet(prevsize) && listLoadMore.get(prevsize).title == GalleryAdapter.LOADING_TEXT) {
                    listLoadMore.removeAt(prevsize)
                    galleryAdapter.removeData(prevsize)
                }
                val items = Parser.picsumParser(response)
                if (!items.isNullOrEmpty()) {
                    listLoadMore.addAll(items)
                    galleryAdapter.onAddData(items)
                }
            }

            override fun onError() {
                isLoading = false
                progressBar.visibility = View.GONE
            }
        }
        picsumRequest = PicsumRequest(this, "")
        picsumRequest.onFinish(onScrappingCompleted)

    }

    lateinit var progressListener: ProgressListener

    private fun initView() {
        gallery = findViewById(R.id.rcv_movies)
        progressBar = findViewById(R.id.progress_bar)
        initPicsum()
        initRecyclerView()
        //initAlertDialog()
        loadImages()
        progressListener = object : ProgressListener {
            override fun onProgressUpdate(percent: Int) {
                overlayView?.progressBar?.progress = percent
            }

            override fun onDownloadDone() {
                overlayView?.isWorking = false
                overlayView?.progressBar?.visibility = View.GONE
                toaster(this@MainActivity, "Loading Finished")
            }

            override fun onDownloadStarted() {
                overlayView?.progressBar?.max = 100
                overlayView?.progressBar?.visibility = View.VISIBLE
                overlayView?.progressBar?.isIndeterminate = true
                overlayView?.progressBar?.progress = 0
                overlayView?.isWorking = true
            }

        }

    }

    private fun downloadImage() {
        Downloader.stateDownload(this, imageModelNow, false, progressListener)
    }

    private fun shareLink() {
        val clip = ClipData.newPlainText(imageModelNow.title, imageModelNow.link)
        clipboardManager.setPrimaryClip(clip)
        toaster(this, "copied to clipboard")
//        val intent = Intent(Intent.ACTION_VIEW)
//        val myUri = Uri.parse(imageModelNow.link)
//        intent.data = myUri
//        //intent.putExtra(Intent.EXTRA_TEXT, myUri)
//        startActivity(Intent.createChooser(intent, "Share link"))
    }

    private fun shareImage() {
        CommonFunction.logger("LINK", imageModelNow.link)
        Downloader.stateDownload(this, imageModelNow, true, progressListener)
    }


    private fun setupImageOverlay(position: Int) {
        overlayView = ImageOverlayView(this).apply {
            update(galleryAdapter.mList[position])

            onBackClick = {
                imageViewer?.close()
                imageViewer = null
            }
            onMenuClick = {
                when (it) {
                    0 -> downloadImage()
                    1 -> shareLink()
                    2 -> shareImage()
                }
            }
        }
    }

    private fun showImages(position: Int) {
        imageModelNow = galleryAdapter.mList[position]
        setupImageOverlay(position)

        imageViewer = StfalconImageViewer.Builder(this, galleryAdapter.mList)
        { view, imageNow ->
            Glide.with(view).load(imageNow.link).skipMemoryCache(true).into(view)
        }.withImageChangeListener {
            if (it + 15 >= galleryAdapter.mList.size) {
                loadImages()
            }
            imageModelNow = galleryAdapter.mList[it]
            overlayView?.update(galleryAdapter.mList[it])
        }.withStartPosition(position)
            .withHiddenStatusBar(true)
            .withOverlayView(overlayView).show()
    }

    var imageModelNow = ImageModel()


    private fun initRecyclerView() {
        gridLayoutManager = GridLayoutManager(this, 2)
        gallery.layoutManager = gridLayoutManager
        gallery.setHasFixedSize(true)
        galleryAdapter = GalleryAdapter { _, i ->
            showImages(i)
        }
        gallery.adapter = galleryAdapter
        gallery.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    gridLayoutManager = gallery.layoutManager as GridLayoutManager
                    if (!isLoading) {
                        if (gridLayoutManager.findLastVisibleItemPosition() >= listLoadMore.size - 15) {
                            isLoading = true
                            loadImages()
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    super.onScrollStateChanged(recyclerView, newState)
                }
            }
        })
    }

    fun safeGet(index: Int): Boolean {
        return index >= 0 && listLoadMore.size > index
    }

    private fun loadImages() {
        pageNow++
        progressBar.visibility = View.VISIBLE
        val linkNow = "https://picsum.photos/v2/list?page=$pageNow&limit=100"
        picsumRequest.updateUrl(linkNow)

        prevsize = listLoadMore.size
        listLoadMore.add(GalleryAdapter.imageModelDummy)
        galleryAdapter.onAddData(GalleryAdapter.imageModelDummy)

        picsumRequest.startScrapping()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).clearMemory()
    }
}