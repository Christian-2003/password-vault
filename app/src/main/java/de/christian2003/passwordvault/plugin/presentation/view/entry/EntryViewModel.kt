package de.christian2003.passwordvault.plugin.presentation.view.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.entry.Detail
import de.christian2003.passwordvault.domain.entry.Entry
import de.christian2003.passwordvault.domain.entry.Tag
import de.christian2003.passwordvault.domain.repository.DetailRepository
import de.christian2003.passwordvault.domain.repository.EntryRepository
import de.christian2003.passwordvault.domain.repository.TagRepository
import de.christian2003.passwordvault.domain.security.ClipboardService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.uuid.Uuid


class EntryViewModel(): ViewModel() {

    private lateinit var entryRepository: EntryRepository

    private lateinit var detailRepository: DetailRepository

    private lateinit var clipboardService: ClipboardService

    private var entry: Entry? = null

    private var isInitialized = false

    lateinit var tagRepository: TagRepository

    lateinit var allTags: Flow<List<Tag>>

    lateinit var entryId: Uuid

    var name: String by mutableStateOf("")

    var description: String by mutableStateOf("")

    var isNameDialogVisible: Boolean by mutableStateOf(false)

    var isDescriptionDialogVisible: Boolean by mutableStateOf(false)

    var isTagDialogVisible: Boolean by mutableStateOf(false)

    var isDetailDialogVisible: Boolean by mutableStateOf(false)

    var detailToEdit: Detail? by mutableStateOf(null)

    var detailToDelete: Detail? by mutableStateOf(null)

    val tags: MutableList<Tag> = mutableStateListOf()

    val details: MutableList<Detail> = mutableStateListOf()

    var viewModelStoreId: Int = 0


    fun init(
        entryRepository: EntryRepository,
        detailRepository: DetailRepository,
        tagRepository: TagRepository,
        clipboardService: ClipboardService,
        id: Uuid? = null
    ) {
        if (isInitialized) {
            return
        }
        this.entryRepository = entryRepository
        this.detailRepository = detailRepository
        this.tagRepository = tagRepository
        this.clipboardService = clipboardService
        this.entryId = id ?: Uuid.random()

        allTags = tagRepository.getAllTags()
        isInitialized = true
        viewModelScope.launch(Dispatchers.IO) {
            entry = entryRepository.getEntryById(entryId)
            if (entry == null) {
                name = ""
                description = ""
                tags.clear()
            }
            else {
                name = entry!!.name
                description = entry!!.description
                tags.clear()
                tags.addAll(entry!!.tags)
            }
            details.clear()
            val detailsForEntry: Flow<List<Detail>> = detailRepository.getAllDetailsForEntry(entryId)
            detailsForEntry.first().forEach { detail ->
                details.add(detail)
            }
        }
    }


    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (name.isNotEmpty() && description.isNotEmpty()) {
            if (entry == null) {
                //Create new entry:
                entry = Entry(
                    id = entryId,
                    name = name,
                    description = description,
                    tags = tags
                )
                entryRepository.createEntry(entry!!)
            }
            else {
                //Edit existing entry:
                entry!!.name = name
                entry!!.description = description
                entry!!.edited = LocalDateTime.now()
                entry!!.tags = tags
                entryRepository.updateEntry(entry!!)
            }
        }
    }


    fun deleteDetail(detail: Detail) = viewModelScope.launch(Dispatchers.IO) {
        details.remove(detail)
    }


    fun copyToClipboard(detail: Detail) = viewModelScope.launch(Dispatchers.IO) {
        clipboardService.copyToClipboard(
            label = detail.name,
            data =detail.content,
            isSensitive = detail.isObfuscated
        )
    }


    fun dismissTagDialog(selectedTags: List<Tag>? = null) {
        isTagDialogVisible = false
        viewModelStoreId++
        if (selectedTags != null) {
            //Save new selected tags:
            tags.clear()
            tags.addAll(selectedTags)
        }
    }


    fun dismissDetailDialog(detail: Detail? = null) {
        if (detail != null) {
            //Save detail:
            if (isDetailDialogVisible) {
                //Dialog to create new detail:
                isDetailDialogVisible = false
                viewModelStoreId++
                details.add(detail)
            }
            else if (detailToEdit != null) {
                //Dialog to edit detail:
                detailToEdit = null
                viewModelStoreId++
                val index = details.indexOf(detail)
                if (index >= 0 && index < details.size) {
                    details.removeAt(index)
                    details.add(index, detail)
                }
            }
        }
        else {
            //Dismiss dialog without saving:
            isDetailDialogVisible = false
            detailToEdit = null
            viewModelStoreId++
        }
    }

}
