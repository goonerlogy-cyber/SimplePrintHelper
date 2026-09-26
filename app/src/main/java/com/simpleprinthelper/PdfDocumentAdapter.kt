package com.simpleprinthelper

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Simple adapter that streams an existing PDF file to the print framework.
 * Works with any printer that appears in the system print dialog
 * (including many USB-OTG printers).
 */
class PdfDocumentAdapter(
    private val context: Context,
    private val pdfUri: Uri
) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        val info = PrintDocumentInfo.Builder("document.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build()

        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        var input: InputStream? = null
        var output: FileOutputStream? = null

        try {
            input = context.contentResolver.openInputStream(pdfUri)
            output = FileOutputStream(destination?.fileDescriptor)

            if (input == null || output == null) {
                callback.onWriteFailed("Could not open streams")
                return
            }

            val buffer = ByteArray(16 * 1024)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } >= 0) {
                if (cancellationSignal?.isCanceled == true) {
                    callback.onWriteCancelled()
                    return
                }
                output.write(buffer, 0, bytesRead)
            }

            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback.onWriteFailed(e.message)
        } finally {
            try {
                input?.close()
                output?.close()
            } catch (_: Exception) {}
        }
    }
}
