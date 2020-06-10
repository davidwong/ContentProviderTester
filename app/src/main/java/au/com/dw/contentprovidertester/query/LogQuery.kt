package au.com.dw.contentprovidertester.query

import android.util.Log
import au.com.dw.contentprovidertester.query.model.QueryParam

/**
 * Content provider query that logs the results to logCat.
 */
class LogQuery : ContentResolverQuery() {
    val tag = "ContentQuery"

    override fun processResult(result: List<MutableMap<String, Any>>, params: QueryParam, executionTime: Long) {

        Log.i(tag, "Content Provider query")

        // params
        Log.i(tag, "uri = " + params.uri)
        Log.i(tag, "projection = " + params.projection?.joinToString(","))
        Log.i(tag, "selection = " + params.selection)
        Log.i(tag, "selectionArgs = " + params.selectionArgs?.joinToString(","))
        Log.i(tag, "sortOrder = " + params.sortOrder)

        Log.i(tag, "result count = " + result.count())
        val time = (executionTime /  1E6).toString() + " ms"
        Log.i(tag, "execution time = " + time)

        var counter = 1
        val indent = "  "
        result.forEach { map ->
            Log.i(tag, "Row " + counter++)
            map.forEach { k, v ->  Log.i(tag, indent + k + " = " + v)}
        }
    }
}