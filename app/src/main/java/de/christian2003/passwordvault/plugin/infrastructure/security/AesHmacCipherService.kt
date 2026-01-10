package de.christian2003.passwordvault.plugin.infrastructure.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import de.christian2003.passwordvault.application.security.MasterKeyService
import de.christian2003.security.domain.services.HmacCipherService
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey


/**
 * Implements the cipher service for the AES algorithm.
 */
class AesHmacCipherService: HmacCipherService {

    /**
     * Alias with which to store the HMAC master key in the key store.
     */
    private val hmacKeyAlias = "HMACMasterKey"


    /**
     * Encrypts the content passed using the specified seed for an HMAC.
     *
     * @param content       Plain text to encrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to encrypt the
     *                      plain text.
     * @return              Cipher text.
     * @throws Exception    Cannot encrypt content.
     */
    override fun encrypt(content: ByteArray, hmacSeed: ByteArray): ByteArray {
        val masterKeyService: MasterKeyService = AesMasterKeyService(deriveHmac(hmacSeed))
        val aesHelper = AesHelper(masterKeyService)

        val ciphertext: ByteArray = aesHelper.encrypt(content)
        masterKeyService.clearMasterKey()

        return ciphertext
    }


    /**
     * Decrypts the passed cypher text using the specified seed for an HMAC.
     *
     * @param content       Cipher text to decrypt.
     * @param hmacSeed      Seed for the HMAC to generate. The HMAC is used as key to decrypt the
     *                      cipher text.
     * @return              Plain text.
     * @throws Exception    Cannot decrypt content.
     */
    override fun decrypt(content: ByteArray, hmacSeed: ByteArray): ByteArray {
        val masterKeyService: MasterKeyService = AesMasterKeyService(deriveHmac(hmacSeed))
        val aesHelper = AesHelper(masterKeyService)

        val plaintext: ByteArray = aesHelper.decrypt(content)
        masterKeyService.clearMasterKey()

        return plaintext
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
        val fullHmacOutput = mac.doFinal(bytes)

        //Use only the first 32 bytes of the key, since AES does not support 64 byte keys:
        return fullHmacOutput.copyOf(32)
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
        if (keyStore.containsAlias(hmacKeyAlias)) {
            //Get existing HMAC master key:
            val entry: KeyStore.SecretKeyEntry = keyStore.getEntry(hmacKeyAlias, null) as KeyStore.SecretKeyEntry
            return entry.secretKey
        }
        else {
            //Create new HMAC master key:
            val keyGenerator: KeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA512, "AndroidKeyStore")
            val keySpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                hmacKeyAlias,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            ).build()
            keyGenerator.init(keySpec)
            return keyGenerator.generateKey()
        }
    }

}
