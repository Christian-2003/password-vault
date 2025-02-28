package de.christian2003.accounts.model

import de.christian2003.accounts.database.DetailEntity
import de.christian2003.core.conversion.csv.CsvBuilder
import de.christian2003.core.security.aes.AesCipher
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID


class Detail(

    val id: UUID = UUID.randomUUID(),

    val account: UUID,

    var name: String,

    var content: String,

    val created: LocalDateTime,

    var changed: LocalDateTime,

    var type: DetailType = DetailType.TEXT,

    var obfuscated: Boolean = false,

    var visible: Boolean = true

) {

    fun toDetailEntity(): DetailEntity {
        val builder = CsvBuilder()
        builder.append(name)
        builder.append(content)
        builder.append(created.toEpochSecond(ZoneOffset.UTC))
        builder.append(changed.toEpochSecond(ZoneOffset.UTC))
        builder.append(type.persistentId)
        builder.append(obfuscated)
        builder.append(visible)

        val bytes: ByteArray = builder.toString().toByteArray()

        val cipher = AesCipher()
        val encrypted: ByteArray = cipher.encryptWithHmac(bytes, account.toString().toByteArray())

        return DetailEntity(
            id = id,
            account = account,
            encrypted = encrypted
        )
    }

}
