package de.christian2003.feature.files.models.other

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import de.christian2003.feature.files.R


/**
 * List of all file types.
 */
internal enum class FileType(
    @param:DrawableRes val drawableRes: Int
) {

    //Derived from mime types:
    Image(R.drawable.ic_file_image),
    Audio(R.drawable.ic_file_audio),
    Video(R.drawable.ic_file_video),
    Text(de.christian2003.core.ui.R.drawable.ic_text),
    Font(R.drawable.ic_file_font),
    Message(de.christian2003.core.ui.R.drawable.ic_email),
    Model(R.drawable.ic_file_model),

    //Office
    Word(R.drawable.ic_file_word),
    Excel(R.drawable.ic_file_excel),
    PowerPoint(R.drawable.ic_file_powerpoint),

    //Specialized types:
    Archive(R.drawable.ic_file_archive),
    Code(de.christian2003.core.ui.R.drawable.ic_dev),
    Markdown(R.drawable.ic_file_markdown),
    Apk(de.christian2003.core.ui.R.drawable.ic_android),
    Pdf(R.drawable.ic_file_pdf),

    //Other:
    Generic(R.drawable.ic_file_generic);


    @Composable
    fun getOnSurfaceColor(): Color {
        return when (this) {
            Image -> colorResource(R.color.file_image_onSurface)
            Audio -> colorResource(R.color.file_audio_onSurface)
            Video -> colorResource(R.color.file_video_onSurface)
            Text -> colorResource(R.color.file_text_onSurface)
            Font -> colorResource(R.color.file_font_onSurface)
            Message -> colorResource(R.color.file_message_onSurface)
            Model -> colorResource(R.color.file_model_onSurface)

            Word -> colorResource(R.color.file_word_onSurface)
            Excel -> colorResource(R.color.file_excel_onSurface)
            PowerPoint -> colorResource(R.color.file_powerpoint_onSurface)

            Archive -> colorResource(R.color.file_archive_onSurface)
            Code -> colorResource(R.color.file_code_onSurface)
            Markdown -> colorResource(R.color.file_markdown_onSurface)
            Apk -> colorResource(R.color.file_apk_onSurface)
            Pdf -> colorResource(R.color.file_pdf_onSurface)

            Generic -> colorResource(R.color.file_generic_onSurface)
        }
    }


    @Composable
    fun getSurfaceColor(): Color {
        return when (this) {
            Image -> colorResource(R.color.file_image_surface)
            Audio -> colorResource(R.color.file_audio_surface)
            Video -> colorResource(R.color.file_video_surface)
            Text -> colorResource(R.color.file_text_surface)
            Font -> colorResource(R.color.file_font_surface)
            Message -> colorResource(R.color.file_message_surface)
            Model -> colorResource(R.color.file_model_surface)

            Word -> colorResource(R.color.file_word_surface)
            Excel -> colorResource(R.color.file_excel_surface)
            PowerPoint -> colorResource(R.color.file_powerpoint_surface)

            Archive -> colorResource(R.color.file_archive_surface)
            Code -> colorResource(R.color.file_code_surface)
            Markdown -> colorResource(R.color.file_markdown_surface)
            Apk -> colorResource(R.color.file_apk_surface)
            Pdf -> colorResource(R.color.file_pdf_surface)

            Generic -> colorResource(R.color.file_generic_surface)
        }
    }

}
