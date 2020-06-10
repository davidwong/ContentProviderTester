package au.com.dw.contentprovidertester

import android.Manifest
import android.content.Context
import android.provider.Telephony
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import au.com.dw.contentprovidertester.query.JsonQuery
import au.com.dw.contentprovidertester.query.LogQuery
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.query.model.SecondaryQuery
import org.junit.Before
import org.junit.Rule
import org.junit.Test


/**
 * Instrumented test to see the results of ContentResolver queries for Telephony content provider.
 */
class TelephonyTest {
    @Rule
    @JvmField
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.READ_SMS)

    lateinit var context : Context

    @Before
    fun setup() {
        // Context of the app under test.
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testLogOutput() {
        val params = QueryParam(uri = Telephony.Threads.CONTENT_URI.toString())
        val query = LogQuery()
        query.processQuery(context, params)
    }

    @Test
    fun testJsonOutput() {
        val params = QueryParam(uri = Telephony.Threads.CONTENT_URI.toString() + "?simple=true",
            projection = arrayOf(Telephony.ThreadsColumns._ID, Telephony.ThreadsColumns.RECIPIENT_IDS))
        val query = JsonQuery(true)
        query.processQuery(context, params)
    }

    @Test
    fun testPhoneNumberLookup() {
        val params = QueryParam(uri = Telephony.Threads.CONTENT_URI.toString() + "?simple=true",
            projection = arrayOf(Telephony.ThreadsColumns._ID, Telephony.ThreadsColumns.RECIPIENT_IDS))

        val secondaryParam = QueryParam(uri = "content://mms-sms/canonical-addresses", projection = arrayOf("address"),
            selection = "_id=?")
        val secondaryQuery = SecondaryQuery(lookup = Telephony.ThreadsColumns.RECIPIENT_IDS, queryParam = secondaryParam)
        val query = JsonQuery(true)
        query.processQuery(context, params, listOf(secondaryQuery))
    }
}