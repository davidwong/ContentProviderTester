package au.com.dw.contentprovidertester.query

import android.util.Log
import au.com.dw.contentprovidertester.query.model.QueryParam
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

/**
 * Content provider query that serializes the results to JSON and logs it to logCat.
 */
class JsonQuery(val prettyPrint: Boolean) : ContentResolverQuery() {
    val tag = "ContentQuery"

    override fun processResult(result: List<MutableMap<String, Any>>, params: QueryParam, executionTime: Long) {

        val gson = if (prettyPrint) GsonBuilder().setPrettyPrinting().create() else Gson()
        val jsonObject = JsonObject()

        val jsonElement = gson.toJsonTree(params)
        jsonElement.asJsonObject.addProperty("result count", result.count())
        val time = (executionTime /  1E6).toString() + " ms"
        jsonElement.asJsonObject.addProperty("execution time", time)

        val resultJson = JsonObject()
        var counter = 1
        result.forEach { map ->
            var row = counter++.toString()
            resultJson.add(row, gson.toJsonTree(map))
        }

        jsonElement.asJsonObject.add("results", resultJson)
        jsonObject.add("Content Provider query", jsonElement)

//        jsonObject.add("Content Provider query", jsonElement)
//
//        var counter = 1
//        result.forEach { map ->
//            var row = counter++.toString()
//            jsonObject.add(row, gson.toJsonTree(map))
//        }

        Log.i(tag, gson.toJson(jsonObject))
    }
}