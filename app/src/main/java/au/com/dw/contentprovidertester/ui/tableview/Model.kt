package au.com.dw.contentprovidertester.ui.tableview

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.evrencoskun.tableview.sort.ISortableModel

open class Cell(@NonNull var mId: String, @Nullable var data: Any?) : ISortableModel {

    override fun getId(): String {
        return mId
    }

    override fun getContent(): Any? {
        return data
    }

    override fun toString(): String {
        return "Cell(mId='$mId', data=$data)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cell

        if (mId != other.mId) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mId.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

}

class ColumnHeader(@NonNull id: String, @Nullable data: Any?) : Cell(id, data) {

}

class RowHeader(@NonNull id: String, @Nullable data: Any?) : Cell(id, data)

/**
 * Holder for data to pass on to the adapter of a TableView.
 */
data class TableListsHolder (
    val columnHeaders : List<ColumnHeader>,
    val rowHeaders : List<RowHeader>,
    val cells : MutableList<MutableList<Cell>>
    )