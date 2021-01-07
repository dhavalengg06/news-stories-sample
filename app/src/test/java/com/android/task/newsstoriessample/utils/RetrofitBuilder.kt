package com.android.task.newsstoriessample.utils

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
	fun getRetrofit(url: String) : Retrofit {
		return Retrofit.Builder()
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.addConverterFactory(GsonConverterFactory.create())
			.baseUrl(url)
			.build()
	}
}