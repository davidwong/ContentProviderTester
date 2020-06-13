package au.com.dw.contentprovidertester

import android.Manifest
import android.content.Context
import android.provider.ContactsContract
import android.provider.Telephony
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import au.com.dw.contentprovidertester.query.JsonQuery
import au.com.dw.contentprovidertester.query.LogQuery
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.query.model.SecondaryQuery
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test


/**
 * Instrumented test to see the results of ContentResolver queries for Telephony content provider.
 */
class TelephonyInboxTest {
    @Rule
    @JvmField
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS)

    lateinit var context : Context

    @Before
    fun setup() {
        // Context of the app under test.
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun querySmsInboxAllColumns() {
        //
        val params = QueryParam(uri = "content://sms/inbox")

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    // todo need thread_id to test first
    @Test
    fun querySmsInboxForThread() {
        fail("choose your own thread_id before running, e.g. from SMS conversation query")
        // content://sms/inbox
        val params = QueryParam(uri = Telephony.Sms.Inbox.CONTENT_URI.toString(), projection = arrayOf(Telephony.Sms.Inbox.DATE, Telephony.Sms.Inbox.BODY), selection = "thread_id=14")

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

}