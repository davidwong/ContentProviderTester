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

    fun query(context: Context, params: QueryParam) {
        query(context, params, emptyList())
    }

    fun query(context: Context, params: QueryParam, secondaryQueries: List<SecondaryQuery>) {
        val gson = if (prettyPrint) GsonBuilder().setPrettyPrinting().create() else Gson()
        val jsonObject = JsonObject()
        val jsonElement = gson.toJsonTree(params)

        val query = ContentResolverQuery()
        val queryResult = query.processQuery(context, params, secondaryQueries)

        if (queryResult is Result.Success) {

            jsonElement.asJsonObject.addProperty("status", "Success")
            jsonElement.asJsonObject.addProperty("result count", queryResult.data.results.count())
            val time = (queryResult.data.executionTime / 1E6).toString() + " ms"
            jsonElement.asJsonObject.addProperty("execution time", time)

            val resultJson = JsonObject()
            var counter = 1
            queryResult.data.results.forEach { map ->
                var row = counter++.toString()
                resultJson.add(row, gson.toJsonTree(map))
            }

            jsonElement.asJsonObject.add("results", resultJson)


//        jsonObject.add("Content Provider query", jsonElement)
//
//        var counter = 1
//        result.forEach { map ->
//            var row = counter++.toString()
//            jsonObject.add(row, gson.toJsonTree(map))
//        }


        }
        else
        {
            jsonElement.asJsonObject.addProperty("status", "ERROR")
        }
        jsonObject.add("Content Provider query", jsonElement)
        Log.i(tag, gson.toJson(jsonObject))
    }
}