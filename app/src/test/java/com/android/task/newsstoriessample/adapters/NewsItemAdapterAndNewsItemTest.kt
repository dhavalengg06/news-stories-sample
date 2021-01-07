package com.android.task.newsstoriessample.adapters

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.android.task.newsstoriessample.RxSchedulersOverrideRule
import com.android.task.newsstoriessample.TestData
import com.android.task.newsstoriessample.model.NewsItem
import com.android.task.newsstoriessample.service.NewsGetService
import com.android.task.newsstoriessample.utils.ImageUtil
import com.android.task.newsstoriessample.utils.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import retrofit2.Retrofit

class NewsItemAdapterAndNewsItemTest {

	private lateinit var lifecycleOwner: LifecycleOwner
	private var mMockWebServer: MockWebServer = MockWebServer()
	private val port = 6000
	private val url = "http://localhost:$port"
	private lateinit var context: Context

	@Rule
	@JvmField
	val schedulersOverrideRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

	@Before
	fun setUp() {
		lifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
		mMockWebServer.start(port)
		context = Mockito.mock(Context::class.java)
	}

	@After
	fun tearDown() {
		mMockWebServer.close()
		mMockWebServer.shutdown()
	}

	// Mock NewsItemsAdapter to set the mock Data.
	// Test the count on the adapter.
	@Test
	fun newsItemsGetAndSetAdapterTest() {
		val adapter = NewsItemsAdapter(lifecycleOwner)
		val recyclerView = mock(RecyclerView::class.java)
		recyclerView.adapter = adapter

		val newsItems = getMockNewsItems()
		assert(newsItems?.size == 4)

		GlobalScope.launch(Dispatchers.Unconfined) {
			adapter.setData(null)
		}
		GlobalScope.launch(Dispatchers.Unconfined) {
			adapter.setData(newsItems)
			assert(adapter.itemCount == 4)
		}
	}


	// Testy NewsItem models.
	@Test
	fun newsItemsModelTest() {
		val newsItems = getMockNewsItems()
		assert(newsItems?.size == 4)

		val firstItem = getMockNewsItems()?.first()
		GlobalScope.launch(Dispatchers.Unconfined) {
			firstItem?.setSelected(false)
		}
		assert(firstItem?.isJpgImageExist() == false)

		assert(firstItem?.url != null)
		assert(firstItem?.headline != null)
		assert(firstItem?.theAbstract != null)
		assert(firstItem?.byLine != null)

		firstItem?.onClick()
		assert(true)

		val pair = firstItem?.getBaseAndImageUrl()
		assert(pair != null)
		assert(pair?.first.equals("https://www.fairfaxstatic.com.au"))
		assert(pair?.second.equals("content/dam/images/h/1/t/6/1/y/image.related.thumbnail.375x250.p56rjr.13zzqx.png/1609754551711.jpg"))

		val body = ResponseBody.create(MediaType.parse("image/jpeg"), "isThisEnoughForTest")
		val imageFile = ImageUtil.processResponse(context, body)
		assert(imageFile != null)
		GlobalScope.launch(Dispatchers.Unconfined) {
			firstItem?.setJpgImage(imageFile!!)
		}
		assert(true)
	}


	private fun getMockNewsItems(): Collection<NewsItem>? {
		mMockWebServer.enqueue(
			MockResponse().setBody(TestData.newsGet)
		)
		val retrofit: Retrofit = RetrofitBuilder.getRetrofit(url)
		val result = retrofit.create(NewsGetService::class.java).getNewsStories()
			.blockingGet()

		val newsAssets = result.getSortedNewsAssets()
		return NewsItem.getFromAssets(newsAssets) { }
	}
}