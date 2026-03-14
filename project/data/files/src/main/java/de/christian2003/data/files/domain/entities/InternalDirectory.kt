package de.christian2003.data.files.domain.entities


/**
 * Value object models a directory.
 *
 * @param internalPath  Internal path of the directory (example: "/finances/my bank/accounts").
 *                      An internal path always begins with "/".
 */
data class InternalDirectory(
    val internalPath: String
) {

    /**
     * Returns the internal name of the directory. For example, the internal path
     * "/finances/my bank/accounts" has the internal name "accounts"
     */
    val internalName: String
        get() {
            val separatorIndex: Int = internalPath.lastIndexOf('/')
            return if (separatorIndex >= 0 && separatorIndex < internalPath.length - 1) {
                internalPath.substring(separatorIndex + 1)
            }
            else {
                internalPath
            }
        }

}
