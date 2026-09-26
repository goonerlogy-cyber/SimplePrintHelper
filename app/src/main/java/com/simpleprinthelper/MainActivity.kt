package com.simpleprinthelper

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.print.PrintHelper
import com.simpleprinthelper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Image picker
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { printImage(it) }
    }

    // PDF picker
    private val pickPdf = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { printPdf(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPrintImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnPrintPdf.setOnClickListener {
            pickPdf.launch("application/pdf")
        }

        // Handle share intent from other apps
        handleIncomingShare(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingShare(intent)
    }

    private fun handleIncomingShare(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND) {
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            val type = intent.type
            when {
                type?.startsWith("image/") == true && uri != null -> printImage(uri)
                type == "application/pdf" && uri != null -> printPdf(uri)
            }
        }
    }

    private fun printImage(uri: Uri) {
        try {
            val printHelper = PrintHelper(this).apply {
                scaleMode = PrintHelper.SCALE_MODE_FIT
                colorMode = PrintHelper.COLOR_MODE_COLOR
            }

            // Load bitmap (for simplicity; for very large images consider sampling)
            contentResolver.openInputStream(uri)?.use { input ->
                val bitmap = BitmapFactory.decodeStream(input)
                if (bitmap != null) {
                    printHelper.printBitmap("SimplePrintHelper - Image", bitmap)
                } else {
                    Toast.makeText(this, "Could not load image", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error printing image: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun printPdf(uri: Uri) {
        try {
            val printManager = getSystemService(PRINT_SERVICE) as PrintManager
            val jobName = "SimplePrintHelper - PDF"

            val adapter = PdfDocumentAdapter(this, uri)
            printManager.print(jobName, adapter, PrintAttributes.Builder().build())
        } catch (e: Exception) {
            Toast.makeText(this, "Error printing PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
