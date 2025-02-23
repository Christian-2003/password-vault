package de.christian2003.accounts.model

import java.time.LocalDateTime
import java.util.UUID

class Account (

    val id: UUID = UUID.randomUUID(),

    var name: String,

    var description: String,

    val created: LocalDateTime,

    var edited: LocalDateTime,

)
