package com.android.task.newsstoriessample.utils

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.task.newsstoriessample.R

object DividerUtil {

	fun getDividerDecorationLine(activity: AppCompatActivity?): RecyclerView.ItemDecoration {
		val lineDrawable = ShapeDrawable(RectShape())
		activity?.let {
			lineDrawable.intrinsicHeight = 1
			@Suppress("DEPRECATION")
			lineDrawable.paint.color = it.resources.getColor(R.color.light_grey)
		}
		return DividerDecoration(lineDrawable)
	}

	class DividerDecoration(var divider: Drawable) : RecyclerView.ItemDecoration() {
		override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
			val left = parent.paddingLeft
			val right = parent.width - parent.paddingRight
			for (i in 0..(parent.childCount - 2)) {
				val child = parent.getChildAt(i)
				val params = child.layoutParams as RecyclerView.LayoutParams
				val top = child.bottom + params.bottomMargin
				val bottom = top + divider.intrinsicHeight
				divider.setBounds(left, top, right, bottom)
				divider.draw(c)
			}
		}
	}
}