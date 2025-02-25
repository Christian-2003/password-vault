package de.christian2003.accounts.model

enum class DetailType(

    val persistentId: Byte

) {

    TEXT(0),

    NUMBER(1),

    SECURITY_QUESTION(2),

    ADDRESS(3),

    DATE(4),

    EMAIL(5),

    PASSWORD(6),

    URL(7),

    PIN(8),

    UNDEFINED(-1);


    companion object {

        /**
         * Returns the detail type from it's persistent ID.
         *
         * @param persistentId  Persistent ID whose type to return.
         * @return              Type for the persistent ID specified.
         */
        fun fromPersistentId(persistentId: Byte): DetailType {
            return when (persistentId) {
                TEXT.persistentId -> TEXT
                NUMBER.persistentId -> NUMBER
                SECURITY_QUESTION.persistentId -> SECURITY_QUESTION
                ADDRESS.persistentId -> ADDRESS
                DATE.persistentId -> DATE
                EMAIL.persistentId -> EMAIL
                PASSWORD.persistentId -> PASSWORD
                URL.persistentId -> URL
                PIN.persistentId -> PIN
                else -> UNDEFINED
            }
        }

    }

}
