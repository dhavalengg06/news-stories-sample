package com.android.task.newsstoriessample.service

import com.android.task.newsstoriessample.model.NewsStoriesResponse
import io.reactivex.Single
import retrofit2.http.GET

/**
 * the following is get service for nes feed.
 */
interface NewsGetService {
	@GET("/1/coding_test/13ZZQX/full")
	fun getNewsStories(): Single<NewsStoriesResponse>
}