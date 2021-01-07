package com.android.task.newsstoriessample.model

import com.google.gson.annotations.SerializedName

data class RelatedImages(
	@SerializedName("url") val url: String?,
	//@SerializedName("lastModified") val lastModified: Long?,
	//@SerializedName("timeStamp") val timeStamp: Long?,
	//@SerializedName("height") val height: Int?,
	@SerializedName("width") val width: Int?
)