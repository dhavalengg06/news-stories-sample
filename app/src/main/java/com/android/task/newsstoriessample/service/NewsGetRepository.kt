package com.android.task.newsstoriessample.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.task.newsstoriessample.model.NewsAsset
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

class NewsGetRepository(retrofit: Retrofit) {

	private val TAG = javaClass.simpleName

	private val newsGetService: NewsGetService by lazy {
		retrofit.create(NewsGetService::class.java)
	}

	fun getNewsGetDisposable(newsAssetsLiveData: MutableLiveData<List<NewsAsset>?>): Disposable {
		return newsGetService.getNewsStories()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(
				{ response ->
					val newsAssets = response.getSortedNewsAssets()
					newsAssetsLiveData.postValue(newsAssets)
				},
				{
					Log.e(TAG, "Getting the news is failed.", it)
				}
			)
	}
}