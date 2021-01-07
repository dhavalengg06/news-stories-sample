package com.android.task.newsstoriessample.service

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.task.newsstoriessample.utils.ImageUtil
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class ImageDownloadRepository(application: Application, private val gson: Gson, private val timeOut: Long) {

	private val TAG = javaClass.simpleName
	private val cacheOkHttpClient by lazy { getCacheOkHttpClient(application) }

	private fun getCacheOkHttpClient(context: Context): OkHttpClient {
		return OkHttpClient()
			.newBuilder()
			.addInterceptor { chain ->
				Log.d(TAG, "Interceptor" + chain.request().toString())
				// Get the request from the chain.
				var request = chain.request()

				/*
				*  Leveraging the advantage of using Kotlin,
				*  we initialize the request and change its header depending on whether
				*  the device is connected to Internet or not.
				*/
				request = if (hasNetwork(context) == true)
				/*
				*  If there is Internet, get the cache that was stored 5 minutes ago.
				*  If the cache is older than 5 minutes, then discard it,
				*  and indicate an error in fetching the response.
				*  The 'max-age' attribute is responsible for this behavior.
				*/
					request.newBuilder().header("Cache-Control", "public, max-age=" + 60 * 5).build()
				else
				/*
				*  If there is no Internet, get the cache that was stored 10 min ago.
				*  If the cache is older than 10 minutes, then discard it,
				*  and indicate an error in fetching the response.
				*  The 'max-stale' attribute is responsible for this behavior.
				*  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
				*/
					request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 10).build()

				// Add the modified request to the chain.
				chain.proceed(request)
			}.apply {
				connectTimeout(timeOut, TimeUnit.SECONDS)
				readTimeout(timeOut, TimeUnit.SECONDS)
				writeTimeout(timeOut, TimeUnit.SECONDS)
				cache(getCache(context))
			}.build()
	}

	private fun buildRetrofitCached(url: String): Retrofit {
		return Retrofit.Builder()
			.baseUrl(url)
			.client(cacheOkHttpClient)
			.addConverterFactory(GsonConverterFactory.create(gson))
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()
	}

	private fun hasNetwork(context: Context): Boolean? {
		var isConnected: Boolean? = false // Initial Value
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
		val activeNetwork: NetworkInfo? = connectivityManager?.activeNetworkInfo
		if (activeNetwork != null && activeNetwork.isConnected) {
			isConnected = true
		}
		return isConnected
	}

	private fun getCache(context: Context): Cache {
		val cacheSize = (5 * 1024 * 1024).toLong()
		return Cache(context.cacheDir, cacheSize)
	}

	private fun getImageDownloadService(url: String): ImageDownloadService {
		return buildRetrofitCached(url).create(ImageDownloadService::class.java)
	}

	fun getImageDownloadDisposable(
		context: Context,
		url: String,
		newImagePath: String,
		responseBodyLiveData: MutableLiveData<File?>
	): Disposable {
		return getImageDownloadService(url).downloadImage(newImagePath)
			.subscribeOn(Schedulers.io())
			.observeOn(Schedulers.io())
			.subscribe(
				{ response ->
					val imageFile = ImageUtil.processResponse(context, response)
					responseBodyLiveData.postValue(imageFile)
				},
				{
					//Log.e(TAG, "Getting the news is failed.", it)
					responseBodyLiveData.postValue(null)
				}
			)
	}
}