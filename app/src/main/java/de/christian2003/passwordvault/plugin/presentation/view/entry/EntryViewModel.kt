package de.christian2003.passwordvault.plugin.presentation.view.entry

import android.content.ClipData
import android.content.ClipDescription
import android.os.PersistableBundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.passwordvault.domain.entry.Detail
import de.christian2003.passwordvault.domain.entry.Entry
import de.christian2003.passwordvault.domain.entry.Tag
import de.christian2003.passwordvault.domain.repository.DetailRepository
import de.christian2003.passwordvault.domain.repository.EntryRepository
import de.christian2003.passwordvault.domain.repository.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.uuid.Uuid


class EntryViewModel(): ViewModel() {

    private lateinit var entryRepository: EntryRepository

    private lateinit var detailRepository: DetailRepository

    private var entry: Entry? = null
    private var isInitialized = false

    lateinit var tagRepository: TagRepository

    lateinit var details: Flow<List<Detail>>

    lateinit var allTags: Flow<List<Tag>>

    lateinit var entryId: Uuid

    var name: String by mutableStateOf("")

    var description: String by mutableStateOf("")

    var isNameDialogVisible: Boolean by mutableStateOf(false)

    var isDescriptionDialogVisible: Boolean by mutableStateOf(false)

    var isTagDialogVisible: Boolean by mutableStateOf(false)

    var detailToDelete: Detail? by mutableStateOf(null)

    var tags: MutableList<Tag> = mutableStateListOf()


    fun init(entryRepository: EntryRepository, detailRepository: DetailRepository, tagRepository: TagRepository, id: Uuid? = null) {
        if (isInitialized) {
            return
        }
        this.entryRepository = entryRepository
        this.detailRepository = detailRepository
        this.tagRepository = tagRepository
        this.entryId = id ?: Uuid.random()
        details = detailRepository.getAllDetailsForEntry(entryId)
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
        detailRepository.deleteDetail(detail)
    }


    fun copyToClipboard(detail: Detail, clipboard: Clipboard) = viewModelScope.launch(Dispatchers.IO) {
        val data = ClipData.newPlainText(detail.name, detail.content)
        if (detail.isObfuscated) {
            data.apply {
                description.extras = PersistableBundle().apply {
                    putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                }
            }
        }
        val entry = ClipEntry(data)
        clipboard.setClipEntry(entry)
    }

}
