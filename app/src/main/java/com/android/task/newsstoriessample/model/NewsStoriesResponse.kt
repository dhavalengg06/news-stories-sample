package com.android.task.newsstoriessample.model

import com.google.gson.annotations.SerializedName

data class NewsStoriesResponse(
	@SerializedName("id") val id: String?,
	@SerializedName("assets") val assets: List<NewsAsset>?
) {
	fun getSortedNewsAssets(): List<NewsAsset>? {
		return assets?.sortedByDescending { it.timeStamp }
	}
}