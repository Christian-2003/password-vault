package de.christian2003.passwordvault.domain.model.detail

import androidx.annotation.DrawableRes
import de.christian2003.passwordvault.R


/**
 * Stores all icons that are available for details.
 *
 * @param drawableResourceId    Resource ID of the drawable.
 */
enum class DetailIcon(
    @DrawableRes val drawableResourceId: Int
) {

    TEXT(R.drawable.detail_text),
    NUMBER(R.drawable.detail_number),
    SECURITY_QUESTION(R.drawable.detail_question),
    ADDRESS(R.drawable.detail_address),
    DATE(R.drawable.detail_date),
    EMAIL(R.drawable.detail_email),
    PASSWORD(R.drawable.detail_password),
    URL(R.drawable.detail_url),
    PIN(R.drawable.detail_pin);

}
