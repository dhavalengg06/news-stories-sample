package com.android.task.newsstoriessample.binders

import android.graphics.Bitmap
import android.widget.ImageView
import org.junit.Test
import org.mockito.Mockito

class ImageViewBindersTest {

	// Test setting bitmap to image view including null.
	@Test
	fun setSrcBitMapTest() {
		val imageView = Mockito.mock(ImageView::class.java)
		ImageViewBinders.setSrcBitMap(imageView, null)

		val bitMap = Mockito.mock(Bitmap::class.java)
		ImageViewBinders.setSrcBitMap(imageView, bitMap)
		// Just to check it is not crashing.
		assert(true)
	}
}