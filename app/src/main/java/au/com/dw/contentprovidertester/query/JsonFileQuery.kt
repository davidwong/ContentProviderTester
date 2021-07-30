package au.com.dw.contentprovidertester.query

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Content provider query processor that serializes the results to JSON and logs it to logCat.
 */
class JsonFileQuery(prettyPrint: Boolean, var context: Context, var fileName: String) : JsonQuery(prettyPrint) {

    override fun output(json: String): Boolean {
        // check external storage is available to write to
        val extStorageState: String = Environment.getExternalStorageState()
        if (!Environment.MEDIA_MOUNTED.equals(extStorageState))
        {
            Log.e(tag, "External storage not available")
            return false
        }
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState))
        {
            Log.e(tag, "External storage mounted as read only")
            return false
        }

        try {
            val file = File(context.getExternalFilesDir(null), fileName)
            val fos = FileOutputStream(file)
            fos.write(json.toByteArray())
            fos.close()
            Log.i(tag, "Content provider query JSON written to " + file.absoluteFile)
            return true
        } catch (e: IOException) {
            e.message?.let { Log.e(tag, it) }
        }
        return false
    }


}