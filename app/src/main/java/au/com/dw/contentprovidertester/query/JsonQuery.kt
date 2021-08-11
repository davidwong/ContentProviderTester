package au.com.dw.contentprovidertester.query

import android.content.Context
import android.net.Uri
import android.util.Log
import au.com.dw.contentprovidertester.data.Result
import au.com.dw.contentprovidertester.data.model.QueryResult
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.query.model.SecondaryQuery
import au.com.dw.contentprovidertester.ui.QueryDisplayResult
import com.google.gson.*


/**
 * Content provider query processor that serializes the results to JSON and logs it to logCat.
 */
open class JsonQuery(var prettyPrint: Boolean) {
    val tag = "ContentQuery"

    fun query(context: Context, params: QueryParam): Boolean {
        return query(context, params, emptyList())
    }

    fun query(context: Context, params: QueryParam, secondaryQueries: List<SecondaryQuery>): Boolean {

        val gson = if (prettyPrint) GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(UriExclusionStrategy()).create() else GsonBuilder().addSerializationExclusionStrategy(UriExclusionStrategy()).create()
        val jsonObject = JsonObject()
        // uri excluded as not working
        val rootElement = gson.toJsonTree(params)
        // add uri back as string
        rootElement.asJsonObject.addProperty("uri", params.uri.toString())

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

        if (queryResult is QueryDisplayResult.Success<*>) {

            val resultData = queryResult.data as QueryResult

            rootElement.asJsonObject.addProperty("status", "Success")
            rootElement.asJsonObject.addProperty("result count", resultData.results.count())
            rootElement.asJsonObject.addProperty("execution time", executionTimeDisplay(resultData.executionTime))

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
        val saved = output(gson.toJson(jsonObject))

        return (queryResult is QueryDisplayResult.Success<*> && saved)
    }

    protected open fun output(json: String): Boolean {
        Log.i(tag, json)
        return true
    }
}

// filter out uri from QueryParam in JSON, as not working for toJsonTree()
class UriExclusionStrategy: ExclusionStrategy {
    override fun shouldSkipField(field: FieldAttributes): Boolean {
        if (field.declaredType == Uri::class.java && field.name == "uri") {
            return true
        }
        return false
    }

    override fun shouldSkipClass(clazz: Class<*>?): Boolean {
        return false
    }
}