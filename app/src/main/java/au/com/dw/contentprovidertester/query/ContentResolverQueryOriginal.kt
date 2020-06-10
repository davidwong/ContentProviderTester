package au.com.dw.contentprovidertester.query

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.query.model.SecondaryQuery

abstract class ContentResolverQueryOriginal {

    fun processQuery(context: Context, params: QueryParam)
    {

        val startTime = System.nanoTime()
        val cursor = getCursor(context, params)
        var queryResult = query(cursor, params)
        val finishTime = System.nanoTime()
        processResult(queryResult, params, finishTime - startTime)
    }

    fun getCursor(context: Context, params: QueryParam): Cursor?
    {
        val cursor = try {
            context.contentResolver
                .query(Uri.parse(params.uri), params.projection, params.selection, params.selectionArgs, params.sortOrder)
        } catch (e: SQLException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: SecurityException) {
            null
        }
        return cursor
    }

    fun query(cursor: Cursor?, params: QueryParam): List<MutableMap<String, Any>>
    {

        val result = mutableListOf<MutableMap<String, Any>>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val columns = if (params.projection != null) params.projection else cursor.columnNames
                val rowMap = mapRow(cursor, columns)
                result += rowMap
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return result
    }

    private fun mapRow(
        cursor: Cursor,
        columns: Array<String>
    ) : MutableMap<String, Any>
    {
        val rowMap = mutableMapOf<String, Any>()
        for (column in columns) {
            val index = cursor.getColumnIndex(column)
            val type = cursor.getType(index)

            var row = when (type) {
                Cursor.FIELD_TYPE_NULL -> Pair(column, "NULL")
                Cursor.FIELD_TYPE_INTEGER -> Pair(column, cursor.getLong(index))
                Cursor.FIELD_TYPE_FLOAT -> Pair(column, cursor.getDouble(index))
                Cursor.FIELD_TYPE_STRING -> Pair(column, cursor.getString(index))
                Cursor.FIELD_TYPE_BLOB -> Pair(column, "BLOB")
                else -> Pair(column, "UNKNOWN")
            }
            rowMap += row

        }
        return rowMap
    }

    abstract fun processResult(result: List<MutableMap<String, Any>>, params: QueryParam, executionTime: Long)
}