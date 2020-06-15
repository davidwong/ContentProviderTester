package au.com.dw.contentprovidertester.query

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import android.util.Log
import au.com.dw.contentprovidertester.data.Result
import au.com.dw.contentprovidertester.data.model.QueryResult
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.query.model.SecondaryQuery
import java.io.IOException

/**
 * Generic utility for querying content provider using ContentResolver.query().
 * Subclass to handle what to do with the query results.
 */
class ContentResolverQuery(var isDebug: Boolean = false) {

    val debugTag = "QueryDebug"

    /**
     * This the entry into the query process.
     */
    public fun processQuery(context: Context, params: QueryParam, secondaryQueries: List<SecondaryQuery>): Result<QueryResult>
    {
        try {
            val startTime = System.nanoTime()
            var queryResult = query(context, params, { s -> s }, secondaryQueries)
            val finishTime = System.nanoTime()

            // ignore timings in debug mode, due to debug logging adding extra time
            return Result.Success(processResult(queryResult, params, if (isDebug) 0 else finishTime - startTime))
        } catch (e: Exception)
        {
            Result.Error(e)
        }
        return Result.Error(IOException("Error in query to ContentResolver"))
    }

    /**
     * Get the cursor from the call to ContentResolver.query().
     */
    fun getCursor(context: Context, params: QueryParam): Cursor?
    {
        val cursor = try {
            context.contentResolver
                .query(params.uri, params.projection, params.selection, params.selectionArgs, params.sortOrder)
            // for testing
//            throw SecurityException("TEST")
        } catch (e: SQLException) {
            Log.e("QueryCursor", e.message)
            throw e
        } catch (e: IllegalArgumentException) {
            Log.e("QueryCursor", e.message)
            throw e
        } catch (e: SecurityException) {
            Log.e("QueryCursor", e.message)
            throw e
        }
        return cursor
    }

    /**
     * Do the actual query and get the results.
     */
    fun query(context: Context, params: QueryParam, nameKey: (columnName: String) -> String, secondaryQueries: List<SecondaryQuery>): List<MutableMap<String, Any>>
    {
        debugLog("Start query with params: " + params.toString())

        val cursor = getCursor(context, params)
        val result = mutableListOf<MutableMap<String, Any>>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                debugLog("Columns: " + cursor.columnNames.joinToString(","))

                val rowMap = mapRow(context, cursor, cursor.columnNames, nameKey, secondaryQueries)
                result += rowMap
            } while (cursor.moveToNext())
        }
        else
        {
            debugLog("No cursor")
        }

        cursor?.close()
        return result
    }

    /**
     * Get the data for a row.
     * Has an optional parameter for secondary queries if getting data for a primary query with
     * secondary lookups for some of the fields.
     */
    private fun mapRow(
        context: Context,
        cursor: Cursor,
        columns: Array<String>,
        nameKey: (columnName: String) -> String,
        secondaryQueries: List<SecondaryQuery>
    ) : MutableMap<String, Any> {
        val rowMap = mutableMapOf<String, Any>()
        for (column in columns) {
            debugLog("  Begin: map row for " + column)

            val row = mapField(cursor, column, nameKey)
            rowMap += row

            // do secondary lookups for a primary query
            if (secondaryQueries.isNotEmpty()) {
                // does the column has any secondary lookups
                val matchingLookup =
                    secondaryQueries.filter { secondary -> column.equals(secondary.lookup) }
                if (matchingLookup.isNotEmpty()) {
                    // found a candidate to do a secondary query, this would normally be done to
                    // replace or supplement the field with a lookup value from another database table

                    var newQuery: QueryParam
                    matchingLookup.forEach { secondaryQuery ->
                        // do a secondary query using the value retrieved for the lookup field, this can
                        // be done in either one of two ways
                        if (secondaryQuery.appendUri) {
                            // append the retrieved value from the first query to the Uri
                            val pathSegment = Uri.encode(row.second.toString())
                            val newUri =
                                Uri.withAppendedPath(secondaryQuery.queryParam.uri, pathSegment)
                            newQuery = secondaryQuery.queryParam.copy(uri = newUri)
                        } else {
                            // use the retrieved value from the first query as the selection arg of the secondary query
                            // e.g. if the secondary query has selection 'id=?', then use the retrieved
                            // value as the arg for 'id' to lookup
                            val newArg = row.second.toString()
                            newQuery =
                                secondaryQuery.queryParam.copy(selectionArgs = arrayOf(newArg))
                        }
                        debugLog("  Begin: secondary lookup for " + column)

                        // can pass a different nameKey func to differentiate results from a lookup
                        val secondaryResult =
                            query(context, newQuery, { s -> s + " (L)" }, emptyList())
                        secondaryResult.forEach { rowMap += it }

                        debugLog("  End: secondary lookup for " + column)
                    }
                }
                else
                {
                    debugLog("  No matching secondary lookup")
                }
            }
            debugLog("  End: map row for " + column)
        }
        return rowMap
    }

    /**
     * Get the data for a field.
     * The nameKey func is used to decorate the column name in the result, could be used to distinguish
     * results from a secondary lookup.
     */
    private fun mapField(cursor: Cursor, column: String, nameKey: (columnName: String) -> String): Pair<String, Any>
    {
        val index = cursor.getColumnIndex(column)
        val type = cursor.getType(index)

        var row = when (type) {
            Cursor.FIELD_TYPE_NULL -> Pair(nameKey(column), "NULL")
            Cursor.FIELD_TYPE_INTEGER -> Pair(nameKey(column), cursor.getLong(index))
            Cursor.FIELD_TYPE_FLOAT -> Pair(nameKey(column), cursor.getDouble(index))
            Cursor.FIELD_TYPE_STRING -> Pair(nameKey(column), cursor.getString(index))
            Cursor.FIELD_TYPE_BLOB -> Pair(nameKey(column), "BLOB")
            else -> Pair(nameKey(column), "UNKNOWN")
        }
        debugLog("    map field for column " + column + "=" + row)

        return row
    }

    fun processResult(result: List<MutableMap<String, Any>>, params: QueryParam, executionTime: Long): QueryResult
    {
        return QueryResult(params, result, executionTime)
    }

    fun debugLog(msg: String)
    {
        if (isDebug)
            Log.d(debugTag, msg)
    }
}