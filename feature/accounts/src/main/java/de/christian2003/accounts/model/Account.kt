package de.christian2003.accounts.model

import de.christian2003.accounts.database.AccountEntity
import de.christian2003.core.conversion.csv.CsvBuilder
import de.christian2003.core.conversion.csv.CsvParser
import de.christian2003.core.security.aes.AesCipher
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

class Account (

    val id: UUID = UUID.randomUUID(),

    var name: String,

    var description: String,

    val created: LocalDateTime,

    var edited: LocalDateTime

) {

    fun toEntity(): AccountEntity {
        val builder = CsvBuilder()
        builder.append(name)
        builder.append(description)
        builder.append(created.toEpochSecond(ZoneOffset.UTC))
        builder.append(edited.toEpochSecond(ZoneOffset.UTC))

        val content: ByteArray = builder.toString().toByteArray()
        val hmacSeed: ByteArray = id.toString().toByteArray()
        val cipher = AesCipher()
        val encrypted: ByteArray = cipher.encryptWithHmac(content, hmacSeed)

        return AccountEntity(
            id = id,
            content = encrypted
        )
    }


    companion object {

        fun ofEntity(entity: AccountEntity): Account {
            val content: ByteArray = entity.content
            val hmacSeed: ByteArray = entity.id.toString().toByteArray()
            val cipher = AesCipher()
            val decrypted: ByteArray = cipher.decryptWithHmac(content, hmacSeed)

            val parser = CsvParser(csv = String(decrypted))
            var name = ""
            var description = ""
            var created: LocalDateTime = LocalDateTime.now()
            var edited: LocalDateTime = LocalDateTime.now()
            var i: Int = 0
            while (parser.hasNext()) {
                val next: String? = parser.next()
                if (next != null) {
                    when (i) {
                        0 -> name = next
                        1 -> description = next
                        2 -> {
                            try {
                                created = LocalDateTime.ofEpochSecond(next.toLong(), 0, ZoneOffset.UTC)
                            }
                            catch (e: Exception) { /* Ignore */ }
                        }
                        3 -> {
                            try {
                                edited = LocalDateTime.ofEpochSecond(next.toLong(), 0, ZoneOffset.UTC)
                            }
                            catch (e: Exception) { /* Ignore */ }
                        }
                    }
                }
                i++
            }

            return Account(
                id = entity.id,
                name = name,
                description = description,
                created = created,
                edited = edited
            )
        }

    }

}
