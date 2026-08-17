package de.passwordvault.model.storage.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import de.passwordvault.R
import de.passwordvault.model.entry.EntryExtended
import de.passwordvault.model.entry.EntryManager
import de.passwordvault.view.utils.Utils
import kotlin.jvm.Throws
import androidx.core.graphics.createBitmap
import java.io.ByteArrayOutputStream


class ExportToPdf(
    private val context: Context
) {

    private val exportFileTemplate: String = Utils.readRawResource(R.raw.pdf_export_file)
    private val accountContainerTemplate: String = Utils.readRawResource(R.raw.pdf_export_entry_container)
    private val accountDetailTemplate: String = Utils.readRawResource(R.raw.pdf_export_entry_detail)
    private val accountTagTemplate: String = Utils.readRawResource(R.raw.pdf_export_entry_tag)
    private val accountLogoAbbreviationTemplate: String = Utils.readRawResource(R.raw.pdf_export_entry_logo_abbreviation)

    private val accountLogoDrawableTemplate: String = Utils.readRawResource(R.raw.pdf_export_entry_logo_drawable)


    @Throws(ExportException::class)
    fun export() {
        val html: String = generateHtml()

        convertHtmlToPdf(
            context = context,
            html = html
        )
    }


    /**
     * Generates the HTML for the entries.
     * The HTML returned can be converted into a PDF file.
     *
     * @return HTML for the entries.
     */
    private fun generateHtml(): String {
        val entriesBuilder = StringBuilder()

        //HTML for all entries:
        EntryManager.getInstance().data.forEach { entryAbbreviated ->
            val entryExtended: EntryExtended? = EntryManager.getInstance().get(entryAbbreviated.uuid, false)
            if (entryExtended != null) {
                val entryHtml: String = generateEntryHtml(entryExtended)
                entriesBuilder.append(entryHtml)
            }
        }

        //Final HTML:
        val finalHtml: String = exportFileTemplate.replace("{entries}", entriesBuilder.toString())

        return finalHtml
    }


    /**
     * Generates the HTML for a single entry.
     *
     * @param entry Entry for which to generate the HTML.
     * @return      HTML generated for the provided entry.
     */
    private fun generateEntryHtml(entry: EntryExtended): String {
        val tagsBuilder = StringBuilder()
        val detailsBuilder = StringBuilder()

        val entryName: String = entry.name ?: ""
        val entryDescription: String = entry.description ?: ""

        //HTML for logo:
        val logoDrawable: Drawable? = entry.logo //If no logo is available, null is returned
        val logoHtml: String = if (logoDrawable == null) {
            val abbreviated: Char = entryName.firstOrNull() ?: '?'
            accountLogoAbbreviationTemplate.replace("{abbreviation}", abbreviated.toString())
        } else {
            val dataUri: String = drawableToDataUri(logoDrawable)
            accountLogoDrawableTemplate.replace("{image}", dataUri)
        }

        //HTML for tags:
        entry.tags.forEach { tag ->
            if (tag != null) {
                val tagName: String = tag.name ?: ""
                if (tagName.isNotBlank()) {
                    val tagHtml: String = accountTagTemplate.replace("{tag}", tagName)
                    tagsBuilder.append(tagHtml)
                }
            }
        }

        //HTML for details:
        entry.details.forEach { detail ->
            if (detail != null) {
                val detailName: String = detail.name ?: ""
                val detailContent: String = detail.content ?: ""
                if (detailName.isNotBlank() && detailContent.isNotBlank()) {
                    val detailHtml = accountDetailTemplate
                        .replace("{name}", detailName)
                        .replace("{content}", detail.content)
                    detailsBuilder.append(detailHtml)
                }
            }
        }

        //Final entry:
        val entryHtml: String = accountContainerTemplate
            .replace("{logo}", logoHtml)
            .replace("{title}", entryName)
            .replace("{description}", entryDescription)
            .replace("{tags}", tagsBuilder.toString())
            .replace("{details}", detailsBuilder.toString())

        return entryHtml
    }


    private fun convertHtmlToPdf(
        context: Context,
        html: String
    ) {
        val webView = WebView(context).apply {
            settings.javaScriptEnabled = false
            settings.domStorageEnabled = false

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            webViewClient = object: WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    if (view != null) {
                        val printManager: PrintManager =
                            context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                        val printAdapter: PrintDocumentAdapter =
                            view.createPrintDocumentAdapter("document")

                        val attributes: PrintAttributes = PrintAttributes.Builder()
                            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                            .build()

                        printManager.print(
                            "Password Vault Export",
                            printAdapter,
                            attributes
                        )
                    }
                }
            }
        }

        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }


    private fun drawableToDataUri(drawable: Drawable): String {
        try {
            val width: Int = drawable.intrinsicWidth.coerceAtLeast(1)
            val height: Int = drawable.intrinsicHeight.coerceAtLeast(1)

            val bitmap: Bitmap = createBitmap(width, height)

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)

            val base64: String = Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)

            bitmap.recycle()

            return "data:image/png;base64,$base64"
        }
        catch (e: Exception) {
            Log.w("PDF", e.message ?: "Cannot convert Drawable to Data URI")
        }

        return ""
    }

}
