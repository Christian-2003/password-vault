package de.christian2003.feature.autofill.infrastructure.services

import android.service.autofill.FillContext
import javax.inject.Inject

internal class GetPackageNameService @Inject constructor() {

    fun getPackageName(fillContexts: List<FillContext>): String? {
        fillContexts.forEach { fillContext ->
            fillContext.structure.activityComponent
        }
        return null
    }

}
