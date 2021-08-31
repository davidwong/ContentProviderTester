package au.com.dw.contentprovidertester

import android.Manifest
import android.content.Context
import android.net.Uri
import android.provider.Telephony
import android.provider.Telephony.Sms.Inbox
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import au.com.dw.contentprovidertester.query.JsonFileQuery
import au.com.dw.contentprovidertester.query.JsonQuery
import au.com.dw.contentprovidertester.query.model.QueryParam
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


/**
 * Instrumented test to see the results of ContentResolver queries for Telephony content provider.
 * This is for conversations for SMS only.
 * Can't use conversation content provider due to Samsung bug, so use grouping on SMS content provider.
 */
class TelephonySmsConversationTest {
    @Rule
    @JvmField
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS)

    lateinit var context : Context

    // for testing paging
    val limit = 20
    val offset = 10

    @Before
    fun setup() {
        // Context of the app under test.
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    /**
     * Group SMS inbox messages by thread.
     * Idea from
     * https://stackoverflow.com/questions/2315203/android-distinct-and-groupby-in-contentresolver/4470227
     *
     * Note that the 'IS NOT NULL' condition is only required because of the way the ContentResolver
     * adds brackets to the database query.
     */
    @Test
    fun querySmsInboxByThread() {
        // content://sms/inbox
        // SELECT DISTINCT thread_id, _id, address, body, date FROM sms_restricted WHERE (type=1) AND (thread_id IS NOT NULL) GROUP BY (thread_id) ORDER BY date DESC
        val params = QueryParam(uri = Inbox.CONTENT_URI, projection = arrayOf("DISTINCT " + Inbox.THREAD_ID,Inbox._ID, Inbox.ADDRESS, Inbox.BODY, Inbox.DATE),
            selection = Inbox.THREAD_ID + " IS NOT NULL) GROUP BY (" + Inbox.THREAD_ID, sortOrder = Inbox.DEFAULT_SORT_ORDER)

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun querySmsInboxByThreadWithPhoneNumberLookup() {
        // content://sms/inbox
        // SELECT DISTINCT thread_id, _id, address, body, date FROM sms_restricted WHERE (type=1) AND (thread_id IS NOT NULL) GROUP BY (thread_id) ORDER BY date DESC
        val params = QueryParam(uri = Inbox.CONTENT_URI,
            projection = arrayOf("DISTINCT " + Inbox.THREAD_ID,Inbox._ID, Inbox.ADDRESS, Inbox.BODY, Inbox.DATE),
            selection = Inbox.THREAD_ID + " IS NOT NULL) GROUP BY (" + Inbox.THREAD_ID,
            sortOrder = Inbox.DEFAULT_SORT_ORDER)

        val secondaryQuery = getPhoneLookupForContactQuery()

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params, listOf(secondaryQuery)))
    }

    @Test
    fun saveSmsInboxByThread() {
        // SELECT DISTINCT thread_id, _id, address, body, date FROM sms_restricted WHERE (type=1) AND (thread_id IS NOT NULL) GROUP BY (thread_id) ORDER BY date DESC
        val params = QueryParam(uri = Uri.parse("content://sms/inbox"),
            projection = arrayOf("DISTINCT " + Inbox.THREAD_ID,Inbox._ID, Inbox.ADDRESS, Inbox.BODY, Inbox.DATE),
            selection = Inbox.THREAD_ID + " IS NOT NULL) GROUP BY (" + Inbox.THREAD_ID,
            sortOrder = Inbox.DEFAULT_SORT_ORDER)

        val queryProcessor = JsonFileQuery(true, context,"sms-inbox-thread.json")
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun querySmsConversationWithPagingAndPhoneNumberLookup() {
        val params = QueryParam(uri = Inbox.CONTENT_URI,
            projection = arrayOf("DISTINCT " + Inbox.THREAD_ID,Inbox._ID, Inbox.ADDRESS, Inbox.BODY, Inbox.DATE),
            selection = Inbox.THREAD_ID + " IS NOT NULL) GROUP BY (" + Inbox.THREAD_ID,
            sortOrder = Inbox.DEFAULT_SORT_ORDER + " LIMIT " + limit + " OFFSET " + offset)

        val secondaryQuery = getPhoneLookupForContactQuery()

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params, listOf(secondaryQuery)))
    }

    @Test
    fun querySmsConversationWithFilterAndPhoneNumberLookup() {
        // filter only works on raw address, not the lookup contact name
//        val filter = "Maree"
        val filter = "02"
        val filterArg = "%" + filter + "%"

        val params = QueryParam(uri = Inbox.CONTENT_URI,
            projection = arrayOf("DISTINCT " + Inbox.THREAD_ID,Inbox._ID, Inbox.ADDRESS, Inbox.BODY, Inbox.DATE),
            selection = Telephony.Sms.ADDRESS + " LIKE ?) AND (" + Inbox.THREAD_ID + " IS NOT NULL) GROUP BY (" + Inbox.THREAD_ID,
            selectionArgs = arrayOf(filterArg),
            sortOrder = Inbox.DEFAULT_SORT_ORDER)

        val secondaryQuery = getPhoneLookupForContactQuery()

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params, listOf(secondaryQuery)))
    }

}