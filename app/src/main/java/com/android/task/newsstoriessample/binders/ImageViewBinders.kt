package com.android.task.newsstoriessample.binders

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter

object ImageViewBinders {

	@JvmStatic
	@BindingAdapter("srcBitMap")
	fun setSrcBitMap(imageView: ImageView, bitmap: Bitmap?) {
		bitmap?.let {
			imageView.setImageBitmap(it)
		}
	}
}