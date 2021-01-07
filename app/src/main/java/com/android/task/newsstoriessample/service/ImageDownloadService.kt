package com.android.task.newsstoriessample.service

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

/**
 * Service to download the image from asset url.
 */
interface ImageDownloadService {
	@Streaming
	@GET("{imageName}")
	fun downloadImage(@Path("imageName") imageName: String): Single<ResponseBody>
}