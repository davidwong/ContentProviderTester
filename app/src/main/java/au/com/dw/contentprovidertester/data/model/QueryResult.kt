package au.com.dw.contentprovidertester.data.model

import au.com.dw.contentprovidertester.query.model.QueryParam

data class QueryResult ( val params: QueryParam, val results: List<Map<String, Any>>, val executionTime: Long)