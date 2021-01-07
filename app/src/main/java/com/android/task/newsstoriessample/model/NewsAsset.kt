package com.android.task.newsstoriessample.model

import com.google.gson.annotations.SerializedName

data class NewsAsset(
	@SerializedName("url") val url: String?,
	//@SerializedName("lastModified") val lastModified: Long?,
	@SerializedName("timeStamp") val timeStamp: Long?,
	@SerializedName("headline") val headline: String?,
	@SerializedName("theAbstract") val theAbstract: String?,
	@SerializedName("byLine") val byLine: String?,
	@SerializedName("relatedImages") val relatedImages: List<RelatedImages>?
)