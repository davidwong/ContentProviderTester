package au.com.dw.contentprovidertester.query

import android.content.Context
import android.util.Log
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.query.model.SecondaryQuery
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import au.com.dw.contentprovidertester.data.Result

/**
 * Content provider query that serializes the results to JSON and logs it to logCat.
 */
class JsonQuery(val prettyPrint: Boolean) {
    val tag = "ContentQuery"

    fun query(context: Context, params: QueryParam): Boolean {
        return query(context, params, emptyList())
    }

    fun query(context: Context, params: QueryParam, secondaryQueries: List<SecondaryQuery>): Boolean {
        val gson = if (prettyPrint) GsonBuilder().setPrettyPrinting().create() else Gson()
        val jsonObject = JsonObject()
        val rootElement = gson.toJsonTree(params)

        if (secondaryQueries.isNotEmpty())
        {
            val secondaryQueryJson = JsonObject()
            secondaryQueries.forEach {
                rootElement.asJsonObject.add("field lookup", gson.toJsonTree(it))
            }
        }

        val query = ContentResolverQuery()
        // for debugging
//        val query = ContentResolverQueryWithDebugLog()
        val queryResult = query.processQuery(context, params, secondaryQueries)

        if (queryResult is Result.Success) {

            rootElement.asJsonObject.addProperty("status", "Success")
            rootElement.asJsonObject.addProperty("result count", queryResult.data.results.count())
            rootElement.asJsonObject.addProperty("execution time", executionTimeDisplay(queryResult.data.executionTime))

            val resultJson = JsonObject()
            var counter = 1
            queryResult.data.results.forEach { map ->
                var row = counter++.toString()
                resultJson.add(row, gson.toJsonTree(map))
            }

            rootElement.asJsonObject.add("results", resultJson)
        }
        else
        {
            rootElement.asJsonObject.addProperty("status", "ERROR")
        }
        jsonObject.add("Content Provider query", rootElement)
        Log.i(tag, gson.toJson(jsonObject))

        return (queryResult is Result.Success)
    }
}