package de.christian2003.feature.search.application.services

import java.time.LocalDate
import javax.inject.Inject


/**
 * Service to evaluate the relation between two dates depending on an operator.
 */
internal class QueryDateEvaluatorService @Inject constructor() {

    /**
     * Evaluates the relational expression between the provided dates based on the specified operator.
     *
     * @param leftDate  Left date of the relation.
     * @param rightDate Right date of the relation.
     * @param operator  Relational operator.
     * @return          Whether the relational expression is true or false.
     */
    fun evaluateDate(leftDate: LocalDate, rightDate: LocalDate, operator: String): Boolean {
        return when (operator) {
            "<=" -> leftDate <= rightDate
            ">=" -> leftDate >= rightDate
            "<" -> leftDate < rightDate
            ">" -> leftDate > rightDate
            "<>" -> leftDate != rightDate
            else -> false
        }
    }

}
