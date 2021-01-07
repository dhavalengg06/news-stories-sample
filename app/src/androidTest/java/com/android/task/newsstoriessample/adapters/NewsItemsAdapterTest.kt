package com.android.task.newsstoriessample.adapters

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.UiThreadTestRule
import com.android.task.newsstoriessample.NewsSampleActivity
import com.android.task.newsstoriessample.R
import com.android.task.newsstoriessample.TestDataAndroid
import com.android.task.newsstoriessample.model.NewsItem
import com.android.task.newsstoriessample.service.NewsGetService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(AndroidJUnit4::class)
class NewsItemsAdapterTest {

	private var adapter: NewsItemsAdapter? = null
	private var recyclerView: RecyclerView? = null
	private var mMockWebServer: MockWebServer = MockWebServer()
	private val port = 6000
	private val url = "http://localhost:$port"

	@Rule
	@JvmField
	var activityTestRule: ActivityTestRule<NewsSampleActivity> = ActivityTestRule(NewsSampleActivity::class.java, true, false)

	@Rule
	@JvmField
	var uiThreadTestRule: UiThreadTestRule = UiThreadTestRule()

	@Before
	@Throws(Throwable::class)
	fun setup() {
		mMockWebServer.start(port)
		val activity: NewsSampleActivity = activityTestRule.launchActivity(null)
		uiThreadTestRule.runOnUiThread {
			adapter = NewsItemsAdapter(activity)
			recyclerView = RecyclerView(activity)
			recyclerView?.id = R.id.news_main_container
			activity.setContentView(recyclerView)
			recyclerView?.layoutManager = LinearLayoutManager(activity)
			recyclerView?.adapter = adapter
		}
	}

	@After
	fun tearDown() {
		mMockWebServer.close()
		mMockWebServer.shutdown()
	}

	// Test the NewsItemsAdapter is created successfully and used in recycle view.
	// This including setting data which been retrieved from mock data, and verifying count on adapter/recycle view.
	@Test
	fun testAdapterDataSet() {
		mMockWebServer.enqueue(MockResponse().setBody(TestDataAndroid.newsGet))
		val retrofit: Retrofit = Retrofit.Builder()
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.addConverterFactory(GsonConverterFactory.create())
			.baseUrl(url)
			.build()

		val result = retrofit.create(NewsGetService::class.java).getNewsStories()
			.blockingGet()

		val newsAssets = result.getSortedNewsAssets()
		assert(newsAssets?.size == 4)
		val newsItems = NewsItem.getFromAssets(newsAssets) { }
		uiThreadTestRule.runOnUiThread {
			adapter?.setData(newsItems)
		}
		Thread.sleep(400)
		val childCount = recyclerView?.childCount
		assert(childCount == 4)
	}
}