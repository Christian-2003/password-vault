package de.christian2003.accounts.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import de.christian2003.accounts.model.Detail
import de.christian2003.accounts.model.DetailType
import de.christian2003.core.conversion.csv.CsvParser
import de.christian2003.core.security.aes.AesCipher
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID


@Entity("details")
class DetailEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "account")
    val account: UUID,

    @ColumnInfo(name = "encrypted")
    val encrypted: ByteArray

) {

    @Ignore
    private var decryptedDetail: Detail? = null


    fun toDetail(hmacSeed: ByteArray): Detail {
        if (decryptedDetail == null) {
            //Decrypt content:
            val cipher = AesCipher()
            val decrypted: ByteArray = cipher.decryptWithHmac(encrypted, hmacSeed)
            val csv = String(decrypted)

            //Parse content:
            val parser = CsvParser(csv)
            var name = ""
            var content = ""
            var created: LocalDateTime = LocalDateTime.now()
            var changed: LocalDateTime = LocalDateTime.now()
            var type: DetailType = DetailType.UNDEFINED
            var obfuscated = false
            var visible = true

            var i = 0
            while (parser.hasNext()) {
                val column = parser.next()
                if (column != null) {
                    when (i) {
                        0 -> {
                            name = column
                        }
                        1 -> {
                            content = column
                        }
                        2 -> {
                            try {
                                created = LocalDateTime.ofEpochSecond(column.toLong(), 0, ZoneOffset.UTC)
                            }
                            catch (e: Exception) { /* Ignore */ }
                        }
                        3 -> {
                            try {
                                changed = LocalDateTime.ofEpochSecond(column.toLong(), 0, ZoneOffset.UTC)
                            }
                            catch (e: Exception) { /* Ignore */ }
                        }
                        4 -> {
                            try {
                                type = DetailType.fromPersistentId(column.toByte())
                            }
                            catch (e: Exception) { /* Ignore */ }
                        }
                        5 -> {
                            try {
                                obfuscated = column.toBoolean()
                            }
                            catch (e: Exception) { /* Ignore */ }
                        }
                        6 -> {
                            try {
                                visible = column.toBoolean()
                            }
                            catch (e: Exception) { /* Ignore */ }
                        }
                    }
                }
                i++
            }

            //Create new detail:
            decryptedDetail = Detail(
                id = id,
                account = account,
                name = name,
                content = content,
                created = created,
                changed = changed,
                type = type,
                obfuscated = obfuscated,
                visible = visible
            )
        }
        return decryptedDetail!!
    }

}
