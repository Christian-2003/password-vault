package de.passwordvault.view.settings.activity_quality_gates

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.passwordvault.model.analysis.QualityGate
import de.passwordvault.model.analysis.QualityGateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class QualityGatesViewModel: ViewModel() {

    private lateinit var qualityGateManager: QualityGateManager

    var defaultQualityGates: List<QualityGate> by mutableStateOf(emptyList())

    var customQualityGates: List<QualityGate> by mutableStateOf(emptyList())

    var isLoaded: Boolean by mutableStateOf(false)


    fun init(qualityGateManager: QualityGateManager) {
        this.qualityGateManager = qualityGateManager

        val defaultQualityGatesMutable: MutableList<QualityGate> = mutableListOf()
        val customQualityGatesMutable: MutableList<QualityGate> = mutableListOf()

        this.qualityGateManager.data.forEach { qualityGate ->
            if (qualityGate.isEditable) {
                customQualityGatesMutable.add(qualityGate)
            }
            else {
                defaultQualityGatesMutable.add(qualityGate)
            }
        }
        defaultQualityGates = defaultQualityGatesMutable.toList()
        customQualityGates = customQualityGatesMutable.toList()
        isLoaded = true
    }


    fun save() = viewModelScope.launch(Dispatchers.IO) {
        qualityGateManager.saveAllQualityGates()
    }

}
