package com.android.task.newsstoriessample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.android.task.newsstoriessample.fragments.NewsListFragment
import com.android.task.newsstoriessample.service.RetrofitViewModel

/**
 * This activity has different presentations for handset and tablet-size devices.
 * On handsets, the activity presents a list of news, which when touched,
 * lead to a [new fragment representing news details in web view.
 * On tablets, the activity presents the list of news and
 * news details side-by-side using two vertical panes.
 */
open class NewsSampleActivity : AppCompatActivity() {

	private val TAG = javaClass.simpleName
	private lateinit var retrofitViewModel: RetrofitViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Log.d(TAG, "onCreate:${this.hashCode()}")

		retrofitViewModel = ViewModelProvider(this).get(RetrofitViewModel::class.java)

		setContentView(R.layout.news_sample_activity)

		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)
		toolbar.title = title

		/**
		 * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
		 */
		var twoPane = false
		if (findViewById<NestedScrollView>(R.id.news_web_view_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-w900dp).
			// If this view is present, then the
			// activity should be in two-pane mode.
			twoPane = true
		}

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don"t need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			val fragment = NewsListFragment().apply {
				arguments = Bundle().apply {
					putBoolean(NewsListFragment.ARG_IS_TWO_PANE, twoPane)
				}
			}

			supportFragmentManager.beginTransaction()
				.add(R.id.news_main_container, fragment)
				.commit()
		}
	}
}