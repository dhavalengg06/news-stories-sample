package com.android.task.newsstoriessample.utils

import android.content.Context
import android.util.Log
import okhttp3.ResponseBody
import java.io.*

object ImageUtil {

	private val TAG = javaClass.simpleName

	private fun getShareDir(context: Context): File {
		val myDir = File(context.filesDir, "share")
		myDir.mkdir()
		return myDir
	}

	fun processResponse(context: Context, body: ResponseBody): File? {

		val inputStream: InputStream?
		val outputStream: OutputStream?

		try {
			val file = File(getShareDir(context), System.currentTimeMillis().toString() + "_Image.jpg")
			val fileReader = ByteArray(4096)
			val fileSize = body.contentLength()
			var fileSizeDownloaded: Long = 0

			inputStream = body.byteStream()
			outputStream = FileOutputStream(file)

			while (true) {
				val read = inputStream!!.read(fileReader)
				if (read == -1) {
					Log.d(TAG, "file download stream is empty")
					break
				}

				outputStream.write(fileReader, 0, read)
				fileSizeDownloaded += read.toLong()
				Log.d(TAG, "file download: $fileSizeDownloaded of $fileSize")
			}

			if (fileSizeDownloaded == 0L) {
				return null
			}
			outputStream.flush()
			outputStream.close()

			inputStream.close()

			return file

		} catch (e: IOException) {
			Log.e(TAG, "processResponse", e)
			return null
		}
	}
}