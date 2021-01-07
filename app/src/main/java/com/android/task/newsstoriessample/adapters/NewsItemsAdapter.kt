package com.android.task.newsstoriessample.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.android.task.newsstoriessample.BR
import com.android.task.newsstoriessample.R
import com.android.task.newsstoriessample.model.NewsItem

open class NewsItemsAdapter(
	private val viewLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<NewsItemsAdapter.NewItemViewHolder>() {

	private val itemsList = ArrayList<NewsItem>()

	override fun getItemCount() = itemsList.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewItemViewHolder {
		val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.news_item_view, parent, false)
		binding.lifecycleOwner = viewLifecycleOwner
		return NewItemViewHolder(binding, binding.root)
	}

	override fun onBindViewHolder(holder: NewItemViewHolder, position: Int) {
		holder.let {
			holder.viewDataBinding?.setVariable(BR.model, itemsList[position])
			holder.viewDataBinding?.executePendingBindings()
		}
	}

	fun setData(data: Collection<NewsItem>?) {
		itemsList.clear()
		data?.let {
			data.forEach { itemsList.add(it) }
		}
	}

	class NewItemViewHolder(val viewDataBinding: ViewDataBinding?, view: View) : RecyclerView.ViewHolder(view)
}