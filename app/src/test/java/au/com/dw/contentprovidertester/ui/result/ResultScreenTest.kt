package au.com.dw.contentprovidertester.ui.result

import au.com.dw.contentprovidertester.ui.tableview.Cell
import au.com.dw.contentprovidertester.ui.tableview.ColumnHeader
import au.com.dw.contentprovidertester.ui.tableview.RowHeader
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

internal class ResultScreenTest {

    // common data to share between test data and expected data

    val col1 = "id"
    val col2 = "name"
    val col3 = "field"

    val id1 = "123"
    val id2 = "45"
    val id3 = "67"

    val name1 = "Ted"
    val name2 = "Ed"
    val name3 = "fred"

    val data1 : Long = 111
    val data2 : Long = 222
    val data3 : Long = 333

    // test data

    val result1 = mapOf<String, Any>( col1 to id1, col2 to name1, col3 to data1)
    val result2 = mapOf<String, Any>( col1 to id2, col2 to name2, col3 to data2)
    val result3 = mapOf<String, Any>( col1 to id3, col2 to name3, col3 to data3)

    // expected data

    val columnHeaders = listOf<ColumnHeader>(
        ColumnHeader("0", col1),
        ColumnHeader("1", col2),
        ColumnHeader("2", col3),
    )

    val rowHeaders = listOf<RowHeader>(
        RowHeader("0", "1"),
        RowHeader("1", "2"),
        RowHeader("2", "3")
    )

    val cells = mutableListOf(
        mutableListOf(
        Cell("0-0", id1),
        Cell("1-0", name1),
        Cell("2-0", data1)),

        mutableListOf(
        Cell("0-1", id2),
        Cell("1-1", name2),
        Cell("2-1", data2)),

        mutableListOf(
        Cell("0-2", id3),
        Cell("1-2", name3),
        Cell("2-2", data3))
    )

    @Test
    fun collateTableData() {
        val queryResults = listOf(result1, result2, result3)
        val holder = collateTableData(queryResults)

        assertEquals(columnHeaders, holder.columnHeaders)
        assertEquals(rowHeaders, holder.rowHeaders)
        assertEquals(cells, holder.cells)
    }
}