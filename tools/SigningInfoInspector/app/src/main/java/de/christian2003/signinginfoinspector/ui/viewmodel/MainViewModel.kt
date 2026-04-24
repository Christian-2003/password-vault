package de.christian2003.signinginfoinspector.ui.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class MainViewModel(
    application: Application
): AndroidViewModel(application) {

    private val packageManager: PackageManager = application.packageManager

    val applicationInfos: MutableList<ApplicationInfo> = mutableStateListOf()

    init {
        viewModelScope.launch {
            loadPackages()
        }
    }


    fun getIconForPackage(applicationInfo: ApplicationInfo): Drawable? {
        val icon = try {
            packageManager.getApplicationIcon(applicationInfo)
        } catch (_: Exception) {
            null
        }
        return icon
    }


    fun getNameForPackage(applicationInfo: ApplicationInfo): String {
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


    private fun loadPackages() {
        try {
            val applicationInfos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            this@MainViewModel.applicationInfos.clear()
            applicationInfos.forEach { applicationInfo ->
                if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    this@MainViewModel.applicationInfos.add(applicationInfo)
                }
            }
            Log.d("Packages", "Loaded ${this@MainViewModel.applicationInfos.size} packages")
        }
        catch (e: Exception) {
            Log.e("Packages", "Cannot load packages: ${e.message ?: "Unknown error"}")
        }
    }

}
