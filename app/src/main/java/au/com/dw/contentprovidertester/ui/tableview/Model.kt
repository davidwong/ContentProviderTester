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

}

//class ColumnHeader(val data: String)
//
//class RowHeader(val data: String)

class ColumnHeader(@NonNull id: String, @Nullable data: Any?) : Cell(id, data)
class RowHeader(@NonNull id: String, @Nullable data: Any?) : Cell(id, data)