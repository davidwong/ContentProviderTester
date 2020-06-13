package au.com.dw.contentprovidertester.query

import android.content.Context
import android.util.Log
import au.com.dw.contentprovidertester.data.Result
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.query.model.SecondaryQuery

/**
 * Content provider query processor that logs the results as individual logs to logCat.
 * Generally JsonQuery gives you a clearer view of the data, but sometimes the data will
 * exceed the maximum limit for a log statement. LogQuery can be used in this case.
 */
class LogQuery {
    val tag = "ContentQuery"

    fun query(context: Context, params: QueryParam): Boolean {
        return query(context, params, emptyList())
    }

    fun query(context: Context, params: QueryParam, secondaryQueries: List<SecondaryQuery>): Boolean {
        Log.i(tag, "Content Provider query")

        // params
        Log.i(tag, "uri = " + params.uri)
        Log.i(tag, "projection = " + params.projection?.joinToString(","))
        Log.i(tag, "selection = " + params.selection)
        Log.i(tag, "selectionArgs = " + params.selectionArgs?.joinToString(","))
        Log.i(tag, "sortOrder = " + params.sortOrder)

        // todo log secondary lookup query params

        val query = ContentResolverQuery()
        val queryResult = query.processQuery(context, params, secondaryQueries)
        if (queryResult is Result.Success) {

            Log.i(tag, "status = Success")
            Log.i(tag, "result count = " + queryResult.data.results.count())
            Log.i(tag, "execution time = " + executionTimeDisplay(queryResult.data.executionTime))

            var counter = 1
            val indent = "  "
            queryResult.data.results.forEach { map ->
                Log.i(tag, "Row " + counter++)
                map.forEach { k, v -> Log.i(tag, indent + k + " = " + v) }
            }
        }
        else
        {
            Log.e(tag, "status = ERROR")
        }
        return (queryResult is Result.Success)
    }
}