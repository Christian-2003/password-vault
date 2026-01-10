package de.christian2003.passwordvault.plugin.infrastructure.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.inject.Inject

class KeystoreManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun setup(password: CharArray) {

    }

}
