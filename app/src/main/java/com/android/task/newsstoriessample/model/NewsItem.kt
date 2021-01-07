package com.android.task.newsstoriessample.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.net.URL

class NewsItem(
	val url: String?,
	val headline: String?,
	val theAbstract: String?,
	val byLine: String?,
	val relatedImages: List<RelatedImages>?,
	private val callback: ((model: NewsItem) -> Unit)
) {
	private val TAG = javaClass.simpleName

	private val _selected: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	val selected: LiveData<Boolean> = _selected

	private val _displayImage: MutableLiveData<Bitmap?> = MutableLiveData<Bitmap?>()
	val displayImage: LiveData<Bitmap?> = _displayImage

	fun onClick() {
		callback.invoke(this@NewsItem)
	}

	fun getSmallestRelatedImage(): RelatedImages? {
		if (relatedImages != null) {
			val newList = relatedImages.filter { it.url != null && (it.width ?: 0) > 0 }.sortedBy { (it.width ?: 0) }
			if (newList.isNotEmpty()) {
				return newList.first()
			}
		}
		return null
	}

	fun getBaseAndImageUrl(): Pair<String, String>? {
		val imageUrl = getSmallestRelatedImage()?.url
		val url = URL(imageUrl)
		val imagePath: String? = url.path
		if (imageUrl != null && imagePath != null) {
			val baseUrl = imageUrl.substring(0, imageUrl.length - imagePath.length) //imageUrl.replace(imagePath, "")
			val newImagePath = imageUrl.substring(imageUrl.length - imagePath.length + 1, imageUrl.length) //imagePath.trimStart('/')

			return Pair(baseUrl, newImagePath)
		}
		return null
	}

	fun setJpgImage(imgFile: File) {
		Log.d(TAG, "setJpgImage: $imgFile")
		if (imgFile.exists()) {
			val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
			bitmap?.let {
				_displayImage.postValue(bitmap)
			}
		}
	}

	fun isJpgImageExist(): Boolean {
		return (displayImage.value != null)
	}

	fun setSelected(value: Boolean) {
		_selected.postValue(value)
	}

	companion object {

		private fun getFromAsset(
			newsAsset: NewsAsset,
			callback: ((model: NewsItem) -> Unit)
		): NewsItem {
			return NewsItem(
				newsAsset.url,
				newsAsset.headline,
				newsAsset.theAbstract,
				newsAsset.byLine,
				newsAsset.relatedImages,
				callback
			)
		}

		fun getFromAssets(
			newsAssets: List<NewsAsset>?,
			callback: ((model: NewsItem) -> Unit)
		): List<NewsItem>? {
			return newsAssets?.map {
				getFromAsset(it, callback)
			}
		}
	}
}