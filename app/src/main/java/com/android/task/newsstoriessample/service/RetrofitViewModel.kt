package com.android.task.newsstoriessample.service

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.task.newsstoriessample.model.NewsAsset
import com.google.gson.GsonBuilder
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class RetrofitViewModel(application: Application) : AndroidViewModel(application) {

	private val TAG = javaClass.simpleName

	private val retrofit by lazy { buildRetrofit("https://bruce-v2-mob.fairfaxmedia.com.au") }
	private var gson = GsonBuilder().disableHtmlEscaping().setLenient().create()
	private val okHttpClientLazy by lazy { getOkHttpClient() }
	private val newsGetRepository = NewsGetRepository(retrofit)
	private val imageDownloadRepository = ImageDownloadRepository(application, gson, TIME_OUT_AMOUNT)

	private fun buildRetrofit(url: String): Retrofit {
		return Retrofit.Builder()
			.baseUrl(url)
			.client(okHttpClientLazy)
			.addConverterFactory(GsonConverterFactory.create(gson))
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()
	}

	private fun getOkHttpClient(): OkHttpClient {
		return OkHttpClient()
			.newBuilder()
			.addInterceptor { chain ->
				Log.d(TAG, "Interceptor" + chain.request().toString())
				chain.proceed(chain.request())
			}.apply {
				connectTimeout(TIME_OUT_AMOUNT, TimeUnit.SECONDS)
				readTimeout(TIME_OUT_AMOUNT, TimeUnit.SECONDS)
				writeTimeout(TIME_OUT_AMOUNT, TimeUnit.SECONDS)
				cache(null)
			}.build()
	}

	fun getNewsGetDisposable(newsAssetsLiveData: MutableLiveData<List<NewsAsset>?>): Disposable {
		return newsGetRepository.getNewsGetDisposable(newsAssetsLiveData)
	}

	fun getImageDownloadDisposable(
		context: Context,
		baseUrl: String,
		newImagePath: String,
		imageFileLiveData: MutableLiveData<File?>
	): Disposable {
		return imageDownloadRepository.getImageDownloadDisposable(context, baseUrl, newImagePath, imageFileLiveData)
	}

	companion object {
		private const val TIME_OUT_AMOUNT = 180L
	}
}