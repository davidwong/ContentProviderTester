package au.com.dw.contentprovidertester.data.model

import au.com.dw.contentprovidertester.query.model.QueryParam

/**
 * The results of the ContentResolver query, including the parameters used in the query.
 */
data class QueryResult ( val params: QueryParam, val results: List<Map<String, Any>>, val executionTime: Long)