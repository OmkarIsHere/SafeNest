package com.safenest.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.safenest.app.R

class IconsAdapter(
    private val context: Context,
    private val images: ArrayList<String>,
    private val onImageClick: (String) -> Unit
) : RecyclerView.Adapter<IconsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_icon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageRes = images[position]

        Glide.with(context)
            .load(imageRes)
            .circleCrop()
            .into(holder.image)

        holder.image.setOnClickListener {
            onImageClick(imageRes)
        }
    }

    override fun getItemCount(): Int = images.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgItemIcon)
    }
}