package de.christian2003.accounts.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import de.christian2003.accounts.model.Detail
import de.christian2003.accounts.model.DetailType
import de.christian2003.core.conversion.csv.CsvParser
import de.christian2003.core.security.aes.AesCipher
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID


/**
 * Class models a detail that is stored within the database.
 *
 * @author  Christian-2003
 * @since   3.8.0
 */
@Entity(
    tableName = "details",
    foreignKeys = [ForeignKey(
        entity = AccountEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("account"),
        onDelete = ForeignKey.CASCADE
    )]
)
class DetailEntity(

    /**
     * UUID of the detail.
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: UUID = UUID.randomUUID(),

    /**
     * UUID of the account to which the detail belongs.
     */
    @ColumnInfo(name = "account")
    val account: UUID,

    /**
     * Byte array stores the encrypted detail content.
     */
    @ColumnInfo(name = "encrypted")
    val encrypted: ByteArray

) {

    /**
     * Stores the decrypted detail.
     */
    @Ignore
    private var decryptedDetail: Detail? = null


    /**
     * Returns the decrypted detail.
     *
     * @return  Decrypted detail.
     */
    fun toDetail(): Detail {
        if (decryptedDetail == null) {
            //Decrypt content:
            val cipher = AesCipher()
            val decrypted: ByteArray = cipher.decryptWithHmac(encrypted, account.toString().toByteArray())
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
