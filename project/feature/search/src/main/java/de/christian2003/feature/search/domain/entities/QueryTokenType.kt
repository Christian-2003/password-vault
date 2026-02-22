package de.christian2003.feature.search.domain.entities


/**
 * Types for query tokens.
 * To explain each query token type, the following search query is used:
 * (name:Bank OR name:"Bank account") AND created:<=2025-11-12
 *
 * @property Literal            Literal token       e.g. 'Bank', 'Bank account', '2025-11-12', 'name' or 'created'.
 * @property Colon              Colon               e.g. ':'.
 * @property OperatorRelation   Relational operator e.g. '<='.
 * @property OperatorBool       Boolean operator    e.g. 'OR' or 'AND'.
 * @property ParenthesesOpen    Opened parentheses  e.g. '('.
 * @property ParenthesesClose   Closed parentheses  e.g. ')'.
 */
internal enum class QueryTokenType {

    Literal,
    Colon,
    OperatorRelation,
    OperatorBool,
    ParenthesesOpen,
    ParenthesesClose

}
