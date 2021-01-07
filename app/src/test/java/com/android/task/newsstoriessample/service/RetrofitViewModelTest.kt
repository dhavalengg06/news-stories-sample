package com.android.task.newsstoriessample.service

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.task.newsstoriessample.RxSchedulersOverrideRule
import com.android.task.newsstoriessample.model.NewsAsset
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.io.File


class RetrofitViewModelTest {

	private lateinit var applicationMock: Application
	private lateinit var retrofitViewModel: RetrofitViewModel
	private lateinit var context: Context

	@Rule
	@JvmField
	val schedulersOverrideRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

	@Before
	fun setUp() {
		applicationMock = Mockito.mock(Application::class.java)
		context = Mockito.mock(Context::class.java)
		retrofitViewModel = RetrofitViewModel(applicationMock)
	}

	@After
	fun tearDown() {
	}

	 // Test that getNewsGetDisposable works appropriately.
	@Test
	fun getNewsGetDisposable() {
		val newsAssetsLiveData = MutableLiveData<List<NewsAsset>?>()
		val disposable = retrofitViewModel.getNewsGetDisposable(newsAssetsLiveData)
		assert(!disposable.isDisposed)
	}

	// Test that getImageDownloadDisposable works appropriately.
	@Test
	fun getImageDownloadDisposable() {
		val imageFileLiveData = MutableLiveData<File?>()
		val disposable = retrofitViewModel.getImageDownloadDisposable(
			context,
			"https://www.fairfaxstatic.com.au",
			"content/dam/images/h/1/q/t/1/b/image.related.thumbnail.375x250.p56rxt.13zzqx.png/1609909258438.jpg",
			imageFileLiveData
		)
		assert(!disposable.isDisposed)
	}
}