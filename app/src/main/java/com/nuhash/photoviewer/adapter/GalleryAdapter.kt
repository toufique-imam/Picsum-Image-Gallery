package com.nuhash.photoviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nuhash.photoviewer.R
import com.nuhash.photoviewer.model.ImageModel

class GalleryAdapter(val onClick: (ImageModel, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mList = ArrayList<ImageModel>()

    fun onUpdateData(list: ArrayList<ImageModel>) {
        mList = list
        notifyDataSetChanged()
    }

    fun onAddData(list: ArrayList<ImageModel>) {
        val prevSize = mList.size
        mList.addAll(list)
        notifyItemRangeInserted(prevSize, list.size)
    }

    fun onAddData(imageModel: ImageModel) {
        val prevSize = mList.size
        mList.add(imageModel)
        notifyItemInserted(prevSize)
    }

    fun removeData(position: Int) {
        if (position != -1 && mList.size > position) {
            mList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun removeData(imageModel: ImageModel) {
        val pos = mList.lastIndexOf(imageModel)
        if (pos != -1 && mList.size > pos) {
            removeData(pos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
            GalleryItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_loading, parent, false
            )
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GalleryItemViewHolder) {
            loadImageView(holder, position)
        } else {
            loadLoadingView(holder as LoadingViewHolder, position)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList.get(position).title.equals(LOADING_TEXT)) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    private fun loadImageView(holder: GalleryItemViewHolder, position: Int) {
        val imageModel = mList[position]
        Glide.with(holder.imageView)
            .load(imageModel.link)
            .skipMemoryCache(true)
            .thumbnail(0.33f)
            .placeholder(R.drawable.ic_baseline_hourglass_bottom_24)
            .error(R.drawable.ic_baseline_image_not_supported_24)
            .into(holder.imageView)

        holder.titleView.text = imageModel.title
        holder.itemView.setOnClickListener {
            onClick(imageModel, position)
        }
    }

    private fun loadLoadingView(loadingViewHolder: LoadingViewHolder, position: Int) {
        //loading view should be displayed
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    class GalleryItemViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view_item)
        val titleView: TextView = itemView.findViewById(R.id.text_view_title)
    }

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
        const val LOADING_TEXT = "LOADING"
        val imageModelDummy = ImageModel(title = LOADING_TEXT)
    }
}