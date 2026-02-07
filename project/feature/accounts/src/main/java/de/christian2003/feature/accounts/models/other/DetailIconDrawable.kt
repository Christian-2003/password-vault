package de.christian2003.feature.accounts.models.other

import androidx.annotation.DrawableRes
import androidx.compose.material3.MaterialShapes
import androidx.graphics.shapes.RoundedPolygon
import de.christian2003.data.accounts.domain.entities.DetailIcon
import de.christian2003.feature.accounts.R


/**
 * Drawables for the detail icons.
 *
 * @param drawableRes   ID of the drawable resource.
 */
internal enum class DetailIconDrawable(
    @param:DrawableRes val drawableRes: Int
) {

    Text(R.drawable.detail_text),
    Number(R.drawable.detail_number),
    SecurityQuestion(R.drawable.detail_question),
    Address(R.drawable.detail_address),
    Date(R.drawable.detail_date),
    Email(R.drawable.detail_email),
    Password(R.drawable.detail_password),
    Url(R.drawable.detail_url),
    Pin(R.drawable.detail_pin),
    Username(R.drawable.detail_username);


    /**
     * Returns the shape to use as background for the detail icon.
     *
     * @return  Rounded polygon shape.
     */
    fun getShape(): RoundedPolygon {
        return shapes[ordinal % shapes.size]
    }


    companion object {

        /**
         * List of shapes that can be applied as background for a detail icon.
         */
        private val shapes: Array<RoundedPolygon> = arrayOf(
            MaterialShapes.Circle,
            MaterialShapes.VerySunny,
            MaterialShapes.Sunny,
            MaterialShapes.Cookie6Sided,
            MaterialShapes.Cookie12Sided,
            MaterialShapes.Clover8Leaf,
            MaterialShapes.SoftBurst,
            MaterialShapes.Flower
        )


        /**
         * Returns the drawable for the specified detail icon.
         *
         * @param icon  Detail icon whose drawable to return.
         * @return      Drawable for the specified detail icon.
         */
        fun getDrawableForDetailIcon(icon: DetailIcon): DetailIconDrawable {
            //Using "entries[icon.ordinal % entries.size]" would work safely as well. However, if
            //adding a new detail icon, the developer might forget to add the icon here as well. So
            //instead we use the "when"-statement below which shows an error when the expression is
            //not exhaustive (i.e. not every detail icon has a branch). This way, we will be reminded
            //to add an icon here.
            return when (icon) {
                DetailIcon.Text -> Text
                DetailIcon.Number -> Number
                DetailIcon.SecurityQuestion -> SecurityQuestion
                DetailIcon.Address -> Address
                DetailIcon.Date -> Date
                DetailIcon.Email -> Email
                DetailIcon.Password -> Password
                DetailIcon.Url -> Url
                DetailIcon.Pin -> Pin
                DetailIcon.Username -> Username
            }
        }

    }

}
