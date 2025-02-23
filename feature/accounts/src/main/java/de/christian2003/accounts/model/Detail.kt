package de.christian2003.accounts.model

import java.time.LocalDateTime
import java.util.UUID


class Detail(

    val id: UUID = UUID.randomUUID(),

    val account: UUID,

    var name: String,

    var content: String,

    val created: LocalDateTime,

    var edited: LocalDateTime,

    var type: DetailType = DetailType.TEXT,

    var obfuscated: Boolean = false,

    var visible: Boolean = true

) {



}
