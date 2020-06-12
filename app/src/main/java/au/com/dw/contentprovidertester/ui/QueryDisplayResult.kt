package au.com.dw.contentprovidertester.ui

import au.com.dw.contentprovidertester.data.model.QueryResult

/**
 * The query results to display to the user or error message.
 */
data class QueryDisplayResult(
        val success: QueryResult? = null,
        val error: Int? = null
)