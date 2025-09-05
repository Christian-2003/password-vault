package de.christian2003.passwordvault.plugin.presentation.view.entries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.model.entry.Entry
import de.christian2003.passwordvault.application.repository.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EntriesViewModel(): ViewModel() {

    private lateinit var entryRepository: EntryRepository


    lateinit var allEntries: Flow<List<Entry>>

    fun init(entryRepository: EntryRepository) {
        this.entryRepository = entryRepository
        this.allEntries = entryRepository.getAllEntries()
    }


    fun deleteEntry(entry: Entry) = viewModelScope.launch(Dispatchers.IO) {
        entryRepository.deleteEntry(entry)
    }

}
