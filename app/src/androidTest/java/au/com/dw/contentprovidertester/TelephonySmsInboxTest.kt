package au.com.dw.contentprovidertester

import android.Manifest
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Telephony
import android.provider.Telephony.Sms.Inbox
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import au.com.dw.contentprovidertester.query.JsonFileQuery
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
class TelephonySmsInboxTest {
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
        val params = QueryParam(uri = Uri.parse("content://sms/inbox"))

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun querySmsInbox() {
        // content://sms/inbox
        val params = QueryParam(uri = Inbox.CONTENT_URI, projection = arrayOf(Inbox._ID, Inbox.THREAD_ID, Inbox.ADDRESS, Inbox.BODY, Inbox.DATE),
                                sortOrder = Inbox.DEFAULT_SORT_ORDER)

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun querySmsInboxWithPhoneNumberLookup() {
        val params = QueryParam(uri = Inbox.CONTENT_URI, projection = arrayOf(Inbox._ID, Inbox.ADDRESS, Inbox.BODY, Inbox.DATE),
            sortOrder = Inbox.DEFAULT_SORT_ORDER)

        val secondaryQuery = getPhoneLookupForContactQuery()

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params, listOf(secondaryQuery)))
    }

    // todo need thread_id to test first
    @Test
    fun querySmsInboxForThread() {
        fail("choose your own thread_id before running, e.g. from SMS conversation query")
        // content://sms/inbox
        val params = QueryParam(uri = Inbox.CONTENT_URI, projection = arrayOf(Inbox.DATE, Inbox.BODY), selection = "thread_id=14")

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun saveSmsInbox() {
        val params = QueryParam(uri = Uri.parse("content://sms/inbox"), projection = arrayOf(Inbox._ID, Inbox.THREAD_ID, Inbox.ADDRESS, Inbox.BODY, Inbox.DATE),
            sortOrder = Inbox.DEFAULT_SORT_ORDER)

        val queryProcessor = JsonFileQuery(true, context,"sms-inbox.json")
        assertTrue(queryProcessor.query(context, params))
    }

}