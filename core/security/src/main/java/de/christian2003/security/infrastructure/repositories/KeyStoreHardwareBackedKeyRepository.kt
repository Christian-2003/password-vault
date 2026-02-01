package de.christian2003.security.infrastructure.repositories

import android.security.keystore.KeyGenParameterSpec
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Implementation of the repository to access hardware-backed keys, which uses the Android KeyStore
 * system to handle hardware-backed keys.
 */
@Singleton
internal class KeyStoreHardwareBackedKeyRepository @Inject constructor(): HardwareBackedKeyRepository {

    /**
     * Android key store to use for accessing hardware-backed keys.
     */
    private var keyStore: KeyStore? = null


    /**
     * Gets the hardware-backed key with the specified alias. If no key with the specified alias
     * exists, null is returned.
     *
     * @param alias Alias of the key to get.
     * @return      Secret key with the specified alias or null.
     */
    override fun getKey(alias: String): SecretKey? {
        ensureKeyStoreLoaded()
        if (keyStore!!.containsAlias(alias)) {
            val entry: KeyStore.SecretKeyEntry = keyStore!!.getEntry(alias, null) as KeyStore.SecretKeyEntry
            return entry.secretKey
        }
        return null
    }

    /**
     * Generates a new hardware-backed key with the specified alias and returns it. The key will be
     * retrievable using getKey(String) for subsequent uses.
     *
     * @param alias                 Alias for the new key.
     * @param algorithm             Algorithm for the generated key (e.g. "AES).
     * @param keyGenParameterSpec   Spec for the key generation.
     * @return                      New secret key with the specified alias.
     */
    override fun generateNewKey(
        alias: String,
        algorithm: String,
        keyGenParameterSpec: KeyGenParameterSpec
    ): SecretKey {
        ensureKeyStoreLoaded()
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance(algorithm, "AndroidKeyStore")
        keyGenerator.init(keyGenParameterSpec)
        val secretKey: SecretKey = keyGenerator.generateKey()
        return secretKey
    }

    /**
     * Tests whether a hardware-backed key with the specified alias exists.
     *
     * @alias   Alias of the key to check.
     * @return  Whether a key with the specified alias exists.
     */
    override fun containsKey(alias: String): Boolean {
        ensureKeyStoreLoaded()
        return keyStore!!.containsAlias(alias)
    }

    /**
     * Deletes the hardware-backed key with the specified alias and returns true if the key was
     * deleted successfully. If the key cannot be removed (e.g. because a key with the provided alias
     * does not exists), false is returned.
     *
     * @param alias Alias of the key to delete.
     * @return      Whether the key was deleted successfully.
     */
    override fun deleteKey(alias: String): Boolean {
        ensureKeyStoreLoaded()
        if (keyStore!!.containsAlias(alias)) {
            try {
                keyStore!!.deleteEntry(alias)
                return true
            }
            catch (_: Exception) { }
        }
        return false
    }


    /**
     * Ensures that the key store is loaded. After this method finishes and returns to the caller,
     * the 'keyStore' attribute will be initialized and loaded.
     */
    private fun ensureKeyStoreLoaded() {
        //Lint does not detect that 'keyStore' can be null, because 'keyStore' is lateinit var.
        //If 'keyStore' is not initializes, this condition will be true and result in a new key store
        //being created.
        if (keyStore == null) {
            keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
            }
        }
    }

}
