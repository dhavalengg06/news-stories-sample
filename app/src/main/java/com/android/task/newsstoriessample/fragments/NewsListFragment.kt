package com.android.task.newsstoriessample.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.android.task.newsstoriessample.NewsSampleActivity
import com.android.task.newsstoriessample.R
import com.android.task.newsstoriessample.adapters.NewsItemsAdapter
import com.android.task.newsstoriessample.model.NewsAsset
import com.android.task.newsstoriessample.model.NewsItem
import com.android.task.newsstoriessample.service.RetrofitViewModel
import com.android.task.newsstoriessample.utils.DividerUtil
import io.reactivex.disposables.CompositeDisposable
import java.io.File

/**
 * A fragment have recycle view to hold news lists.
 */
class NewsListFragment : Fragment() {

	private val TAG = javaClass.simpleName

	private val compositeDisposable = CompositeDisposable()
	private lateinit var retrofitViewModel: RetrofitViewModel
	private val newsAssetsLiveData = MutableLiveData<List<NewsAsset>?>()

	// Keeping News Items locally so that in des not need to create object and download image again.
	private var newsItems: List<NewsItem>? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Log.d(TAG, "onCreate:${this.hashCode()}")
		retrofitViewModel = ViewModelProvider(this).get(RetrofitViewModel::class.java)

		newsItems = null
		compositeDisposable.add(retrofitViewModel.getNewsGetDisposable(newsAssetsLiveData))
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		Log.d(TAG, "onCreateView:${this.hashCode()}")
		val rootView = inflater.inflate(R.layout.fragment_news_list, container, false)

		val isTwoPane = isTwoPane(arguments)
		val recyclerView = rootView.findViewById<RecyclerView>(R.id.news_list)
		setupRecyclerView(recyclerView, isTwoPane)
		return rootView
	}

	override fun onDestroy() {
		super.onDestroy()
		Log.d(TAG, "onDestroy:${this.hashCode()}")
		compositeDisposable.dispose()
	}

	private fun isTwoPane(arguments: Bundle?): Boolean {
		return if (arguments != null && arguments.containsKey(ARG_IS_TWO_PANE)) {
			arguments.getBoolean(ARG_IS_TWO_PANE, false)
		} else {
			false
		}
	}

	private fun setupRecyclerView(recyclerView: RecyclerView, twoPane: Boolean) {
		val adapter = NewsItemsAdapter(this)
		recyclerView.addItemDecoration(DividerUtil.getDividerDecorationLine(activity as AppCompatActivity))
		recyclerView.itemAnimator = DefaultItemAnimator()
		recyclerView.adapter = adapter

		newsAssetsLiveData.observe(viewLifecycleOwner, { newsAssets ->
			if (newsItems == null) {
				newsItems = NewsItem.getFromAssets(newsAssets) { newsItem ->
					newsItems?.forEach { it.setSelected(false) }
					onNewsClick(activity as NewsSampleActivity, newsItem, twoPane)
				}
			}
			adapter.setData(newsItems)
			adapter.notifyDataSetChanged()
			downloadImagesForNewsItems(newsItems)
			val firstNewsItem = newsItems?.first()
			if (twoPane && firstNewsItem != null) {
				onNewsClick(activity as NewsSampleActivity, firstNewsItem, twoPane)
			}
		})
	}

	/**
	 * Smallest asset image get downloaded when we received news list.
	 * It should only happen once.
	 */
	private fun downloadImagesForNewsItems(newsItems: List<NewsItem>?) {

		newsItems?.let {
			newsItems.forEach { newsItem ->
				if (!newsItem.isJpgImageExist()) {
					val pair = newsItem.getBaseAndImageUrl()
					if (pair != null) {
						val imageFileLiveData = MutableLiveData<File?>()
						compositeDisposable.add(
							retrofitViewModel.getImageDownloadDisposable(
								context!!,
								pair.first,
								pair.second,
								imageFileLiveData
							)
						)
						imageFileLiveData.observe(viewLifecycleOwner, { imageFile ->
							if (imageFile != null) {
								newsItem.setJpgImage(imageFile)
							} else {
								Log.d(TAG, "Image download is failed, do error handling.")
							}
						})
					}
				}
			}
		}
	}

	private fun onNewsClick(activity: AppCompatActivity, newsItem: NewsItem, twoPane: Boolean) {
		Log.d(TAG, "onNewsClick:${this.hashCode()} newsItem:$newsItem twoPane:$twoPane")

		newsItem.setSelected(true)
		val containerViewId = if (twoPane) {
			R.id.news_web_view_container
		} else {
			R.id.news_main_container
		}
		val fragment = NewsWebViewFragment().apply {
			arguments = Bundle().apply {
				putString(NewsWebViewFragment.ARG_NEWS_URL, newsItem.url)
			}
		}
		activity.supportFragmentManager
			.beginTransaction()
			.replace(containerViewId, fragment, "NewsWebViewFragment")
			.addToBackStack("NewsWebViewFragment")
			.commit()
	}

	companion object {
		const val ARG_IS_TWO_PANE = "is_two_pane"
	}
}