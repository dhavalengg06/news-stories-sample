package com.android.task.newsstoriessample.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.android.task.newsstoriessample.R
import java.net.URISyntaxException

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a news container
 * in two-pane mode (on tablets) or a main container.
 * on handsets.
 */
class NewsWebViewFragment : Fragment() {

	private val TAG = javaClass.simpleName
	private lateinit var progressBar: ProgressBar

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val rootView = inflater.inflate(R.layout.fragment_news_web_view, container, false)
		val webView = rootView.findViewById<WebView>(R.id.news_webView)
		progressBar = rootView.findViewById(R.id.news_webView_progress)
		progressBar.visibility = View.VISIBLE
		initWebView(webView)
		arguments?.getString(ARG_NEWS_URL)?.let {
			Log.d(TAG, "onCreateView url:$it")
			webView.loadUrl(it)
		}
		return rootView
	}

	private fun initWebView(webView: WebView) {
		webView.webChromeClient = WebChromeClient()
		webView.clearCache(true)
		webView.isHorizontalScrollBarEnabled = false

		webView.webViewClient = NewsWebViewClient()
		webView.clearCache(true)
		webView.clearHistory()
		webView.isHorizontalScrollBarEnabled = false
	}

	private inner class NewsWebViewClient : WebViewClient() {

		override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
			Log.d(TAG, "shouldOverrideUrlLoading:$url")
			if (url.startsWith("intent://")) {
				view.stopLoading()
				return try {
					val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
					if (intent != null) {
						val info = view.context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
						if (info != null) {
							view.context.startActivity(intent)
						}
					}
					false
				} catch (e: URISyntaxException) {
					Log.e(TAG, "Can't resolve intent:// for url:$url", e)
					false
				}
			} else {
				view.loadUrl(url)
				return true
			}
		}

		override fun onPageFinished(view: WebView, url: String) {
			super.onPageFinished(view, url)
			Log.d(TAG, "onPageFinished:$url")
			progressBar.visibility = View.GONE
		}

		override fun onReceivedError(view: WebView, request: WebResourceRequest?, error: WebResourceError?) {
			super.onReceivedError(view, request, error)
			activity?.invalidateOptionsMenu()
			Log.d(TAG, "onReceivedError request:$request error:$error")
		}
	}

	companion object {
		const val ARG_NEWS_URL = "item_id"
	}
}