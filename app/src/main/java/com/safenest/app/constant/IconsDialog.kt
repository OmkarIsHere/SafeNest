package com.safenest.app.constant

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.safenest.app.R
import com.safenest.app.adapters.IconsAdapter

class IconsDialog(private val context: Context) {

    private var dialog: Dialog? = null

    fun showDialog(
        onImageClick: (String) -> Unit
    ) {
        dialog = Dialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.icons_view, null)
        dialog?.setContentView(view)

        val images = ArrayList<String>()
        images.add(AppConstant.M1)
        images.add(AppConstant.M2)
        images.add(AppConstant.M3)
        images.add(AppConstant.M4)
        images.add(AppConstant.W1)
        images.add(AppConstant.W2)
        images.add(AppConstant.W3)
        images.add(AppConstant.W4)

        val imageGrid: RecyclerView = view.findViewById(R.id.image_grid)
        imageGrid.layoutManager = GridLayoutManager(context, 4)
        imageGrid.adapter = IconsAdapter(context, images) { image ->
            onImageClick(image)
        }

        dialog?.show()
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }
}
