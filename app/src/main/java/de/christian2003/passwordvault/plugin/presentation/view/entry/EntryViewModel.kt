package de.christian2003.passwordvault.plugin.presentation.view.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.entry.Entry
import de.christian2003.passwordvault.domain.repository.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.uuid.Uuid


class EntryViewModel(): ViewModel() {

    private lateinit var entryRepository: EntryRepository

    private var entry: Entry? = null

    private var isInitialized = false


    var name: String by mutableStateOf("")

    var description: String by mutableStateOf("")

    var isNameDialogVisible: Boolean by mutableStateOf(false)

    var isDescriptionDialogVisible: Boolean by mutableStateOf(false)


    fun init(entryRepository: EntryRepository, id: Uuid? = null) {
        if (isInitialized) {
            return
        }
        this.entryRepository = entryRepository
        isInitialized = true
        viewModelScope.launch(Dispatchers.IO) {
            entry = if (id != null) { entryRepository.getEntryById(id) } else { null }
            if (entry == null) {
                name = ""
                description = ""
            }
            else {
                name = entry!!.name
                description = entry!!.description
            }
        }
    }


    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (name.isNotEmpty() && description.isNotEmpty()) {
            if (entry == null) {
                //Create new entry:
                entry = Entry(
                    name = name,
                    description = description
                )
                entryRepository.createEntry(entry!!)
            }
            else {
                //Edit existing entry:
                entry!!.name = name
                entry!!.description = description
                entry!!.edited = LocalDateTime.now()
                entryRepository.updateEntry(entry!!)
            }
        }
    }

}
