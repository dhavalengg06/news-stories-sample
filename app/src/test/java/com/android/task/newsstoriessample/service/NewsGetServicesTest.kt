package com.android.task.newsstoriessample.service

import com.android.task.newsstoriessample.TestData
import com.android.task.newsstoriessample.model.NewsAsset
import com.android.task.newsstoriessample.model.NewsItem
import com.android.task.newsstoriessample.utils.RetrofitBuilder
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(MockitoJUnitRunner::class)
class NewsGetServicesTest {

	lateinit var newsGetService: NewsGetService
	private var mMockWebServer: MockWebServer = MockWebServer()
	private val port = 6000
	private val url = "http://localhost:$port"

	@Before
	fun setUp() {
		MockitoAnnotations.initMocks(this)

		val retrofit: Retrofit = Retrofit.Builder()
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.addConverterFactory(GsonConverterFactory.create())
			.baseUrl("https://bruce-v2-mob.fairfaxmedia.com.au")
			.build()

		newsGetService = retrofit.create(NewsGetService::class.java)
		mMockWebServer.start(port)
	}

	@After
	fun tearDown() {
		mMockWebServer.close()
		mMockWebServer.shutdown()
	}

	// Test the getNewsStories of the NewsGetService so that its return list of newsAssets as expected.
	@Test
	fun testGetNewsStoriesSorted() {

		val newsAssets = getSortedNewsAssets()
		assert(newsAssets?.size == 4)
		assert((newsAssets?.first()?.timeStamp ?: 0) >= (newsAssets?.last()?.timeStamp ?: 0))

		// We get right count of relatedImages for news asset.
		assert(newsAssets?.first()?.relatedImages?.size == 6)

		val newsItems = NewsItem.getFromAssets(newsAssets) { }
		assert(newsItems?.size == 4)

		assert((newsItems?.first()?.getSmallestRelatedImage()?.width ?: 0) <= (newsAssets?.first()?.relatedImages?.first()?.width ?: 0))
	}

	// Test that we get right count of relatedImages for news asset.
	@Test
	fun testNewsAssetRelatedImagesCount() {

		val newsAssets = getSortedNewsAssets()
		assert(newsAssets?.first()?.relatedImages?.size == 6)

		val newsItems = NewsItem.getFromAssets(newsAssets) { }
		assert(newsItems?.size == 4)

		assert((newsItems?.first()?.getSmallestRelatedImage()?.width ?: 0) <= (newsAssets?.first()?.relatedImages?.first()?.width ?: 0))
	}


	// Test that we can convert NewsAsset to NewsItem and getSmallestRelatedImage works appropriately.
	@Test
	fun testNewsAssetToNewsItemAnsCheckGetSmallestRelatedImageFunction() {
		val newsItems = NewsItem.getFromAssets(getSortedNewsAssets()) { }
		assert(newsItems?.size == 4)
		assert((newsItems?.first()?.getSmallestRelatedImage()?.width ?: 0) <= (newsItems?.get(1)?.relatedImages?.first()?.width ?: 0))
	}

	private fun getSortedNewsAssets(): List<NewsAsset>? {

		mMockWebServer.enqueue(
			MockResponse().setBody(TestData.newsGet)
		)
		val retrofit: Retrofit = RetrofitBuilder.getRetrofit(url)
		val result = retrofit.create(NewsGetService::class.java).getNewsStories()
			.blockingGet()

		return result.getSortedNewsAssets()
	}
}