package de.christian2003.feature.search.domain.entities


/**
 * Models a collection of tokens.
 *
 * @param tokens    List of tokens.
 */
internal class QueryTokenCollection(
    private val tokens: List<QueryToken>
) {

    /**
     * Current position within the list of tokens.
     */
    private var position: Int = 0

    /**
     * Returns the size of the collection.
     */
    val size: Int
        get() = tokens.size

    /**
     * Returns the number of remaining tokens.
     */
    val remaining: Int
        get() = tokens.size - position


    /**
     * Peeks the next token. If no more tokens are available, null is returned.
     *
     * @return  Next token or null.
     */
    fun peek(): QueryToken? {
        return if (position < tokens.size) {
            tokens[position]
        } else {
            null
        }
    }


    /**
     * Consumes the next token.
     *
     * @return                          Next token.
     * @throws IllegalArgumentException No more tokens to consume.
     */
    fun consume(): QueryToken {
        if (position >= tokens.size) {
            throw IllegalArgumentException("No more tokens")
        }
        return tokens[position++]
    }


    /**
     * Test whether the provided type matches the next token.
     *
     * @param type  Type to match.
     * @return      Whether the provided type matches the next token.
     */
    fun match(type: QueryTokenType): Boolean {
        val token: QueryToken? = peek()
        return token != null && token.type == type
    }


    /**
     * Consumes the next token if it's type matches the provided type. Otherwise, an exception is
     * thrown.
     *
     * @param type                      Expected type.
     * @return                          Consumed next token.
     * @throws IllegalArgumentException The expected type does not match.
     */
    fun expect(type: QueryTokenType): QueryToken {
        val token = peek()
        if (token == null || token.type != type) {
            throw IllegalArgumentException("Expected $type but encountered ${token?.type ?: "no token"}")
        }
        return consume()
    }


    /**
     * Returns whether the collection is empty.
     *
     * @return  Whether the collection is empty.
     */
    fun isEmpty(): Boolean {
        return tokens.isEmpty()
    }


    /**
     * Returns whether the collection is not empty.
     *
     * @return  Whether the collection is not empty.
     */
    fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

}
