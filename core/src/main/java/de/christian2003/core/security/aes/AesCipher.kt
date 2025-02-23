package de.christian2003.core.security.aes

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * Class implements a cipher that uses AES GCM with an HMAC secret key. The HMAC secret key is
 * derived from a seed unique to each encrypted item, like an ID.
 *
 * @author  Christian-2003
 * @since   3.8.0
 */
class AesCipher {

    /**
     * Alias with which the HMAC secret key is stored within the Android key store.
     */
    private val HMAC_KEY_ALIAS = "HMACMasterKey"


    /**
     * Method encrypts the content with HMAC. The HMAC secret key used is generated through the seed
     * specified.
     *
     * @param content       Content to encrypt.
     * @param hmacSeed      Seed used to generate the HMAC secret key.
     * @return              Encrypted content.
     * @throws Exception    Cannot encrypt content.
     */
    fun encryptWithHmac(content: ByteArray, hmacSeed: ByteArray): ByteArray {
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val hmac: ByteArray = deriveHmac(hmacSeed)
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val secretKeySpec = SecretKeySpec(hmac, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec)
        val encrypted: ByteArray = cipher.doFinal(content)

        return iv + encrypted
    }


    /**
     * Method decrypts the content with HMAC. The HMAC secret key used is generated through the seed
     * specified.
     *
     * @param content       Content to decrypt.
     * @param hmacSeed      Seed used to generate the HMAC secret key.
     * @return              Decrypted content.
     * @throws Exception    Cannot decrypt data.
     */
    fun decryptWithHmac(content: ByteArray, hmacSeed: ByteArray): ByteArray {
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val hmac: ByteArray = deriveHmac(hmacSeed)
        val iv: ByteArray = content.take(12).toByteArray()
        val encrypted: ByteArray = content.drop(12).toByteArray()

        val secretKeySpec = SecretKeySpec(hmac, "AES")
        val gcmSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec)
        val decrypted: ByteArray = cipher.doFinal(encrypted)

        return decrypted
    }


    /**
     * Method derives the HMAC from the specified bytes (seed).
     *
     * @param bytes         Seed used to generate HMAC.
     * @return              Generated HMAC.
     * @throws Exception    Cannot generate HMAC.
     */
    private fun deriveHmac(bytes: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA512")
        mac.init(getOrCreateHmacKey())
        return mac.doFinal(bytes)
    }


    /**
     * Gets a secret key for HMAC operations. This is the master key used with all HMAC used.
     *
     * @return              Secret key to use with HMAC.
     * @throws Exception    Cannot get or create secret key.
     */
    private fun getOrCreateHmacKey(): SecretKey {
        val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        if (keyStore.containsAlias(HMAC_KEY_ALIAS)) {
            //Retrieve HMAC master key:
            val entry: KeyStore.SecretKeyEntry = keyStore.getEntry(HMAC_KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            return entry.secretKey
        }
        else {
            //Create new HMAC master key:
            val keyGenerator: KeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA512, "AndroidKeyStore")
            val keySpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                HMAC_KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            ).build()
            keyGenerator.init(keySpec)
            return keyGenerator.generateKey()
        }
    }

}
