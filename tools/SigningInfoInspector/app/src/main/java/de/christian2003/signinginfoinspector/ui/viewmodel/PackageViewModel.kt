package de.christian2003.signinginfoinspector.ui.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.SigningInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Base64


class PackageViewModel(
    application: Application
): AndroidViewModel(application) {

    private val packageManager: PackageManager = application.packageManager

    private lateinit var applicationInfo: ApplicationInfo

    lateinit var name: String

    val signatures: MutableList<X509Certificate> = mutableListOf()

    val certHistory: MutableList<X509Certificate> = mutableListOf()

    var x509ToDisplay: X509Certificate? by mutableStateOf(null)

    fun init(packageName: String) {
        applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        name = getNameForPackage(applicationInfo)

        val certFactory: CertificateFactory = CertificateFactory.getInstance("X.509")

        val signingInfo = getSigningInfo(applicationInfo)
        signingInfo?.apkContentsSigners?.forEach { signature ->
            val cert: Certificate = certFactory.generateCertificate(ByteArrayInputStream(signature.toByteArray()))
            val x509: X509Certificate = cert as X509Certificate
            signatures.add(x509)
        }
        signingInfo?.signingCertificateHistory?.forEach { signature ->
            val cert: Certificate = certFactory.generateCertificate(ByteArrayInputStream(signature.toByteArray()))
            val x509: X509Certificate = cert as X509Certificate
            certHistory.add(x509)
        }
    }

    fun hashCertificate(x509: X509Certificate): String {
        val digester: MessageDigest = MessageDigest.getInstance("SHA-512")
        val hash: ByteArray = digester.digest(x509.encoded)
        return Base64.getEncoder().encodeToString(hash)
    }


    private fun getNameForPackage(applicationInfo: ApplicationInfo): String {
        val name = try {
            if (applicationInfo.nonLocalizedLabel != null) {
                applicationInfo.nonLocalizedLabel.toString()
            } else {
                applicationInfo.loadLabel(packageManager).toString()
            }
        } catch (_: Exception) {
            applicationInfo.packageName
        }
        return name
    }

    private fun getSigningInfo(applicationInfo: ApplicationInfo): SigningInfo? {
        val packageInfo: PackageInfo = packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
        return packageInfo.signingInfo
    }

}
