package au.com.dw.contentprovidertester

import android.Manifest
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Telephony.Sms
import android.provider.Telephony.Mms
import android.provider.Telephony.MmsSms
import android.provider.Telephony.Threads
import android.provider.Telephony.ThreadsColumns
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import au.com.dw.contentprovidertester.query.JsonFileQuery
import au.com.dw.contentprovidertester.query.JsonQuery
import au.com.dw.contentprovidertester.query.LogQuery
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.query.model.SecondaryQuery
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


/**
 * Instrumented test to see the results of ContentResolver queries for Telephony content provider.
 */
class TelephonyConversationTest {
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
    fun querySmsMmsConversationAllColumns() {
        val params = QueryParam(uri = Threads.CONTENT_URI)
        val queryProcessor = LogQuery()
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun querySmsMmsConversationSimpleAllColumns() {
        val params = QueryParam(uri = Uri.parse(Threads.CONTENT_URI.toString() + "?simple=true"))
        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun querySmsMmsConversationSimple() {
        val params = QueryParam(uri = Uri.parse(Threads.CONTENT_URI.toString() + "?simple=true"),
            projection = arrayOf(ThreadsColumns._ID, ThreadsColumns.RECIPIENT_IDS))
        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    /**
     * Use the canonical addresses URI to get address from the recipient id.
     */
    @Test
    fun querySmsMmsConversationSimpleWithPhoneNumberLookup() {
        // content://mms-sms/conversations
        val params = QueryParam(uri = Uri.parse(Threads.CONTENT_URI.toString() + "?simple=true"),
            projection = arrayOf(ThreadsColumns._ID, ThreadsColumns.RECIPIENT_IDS))

        val secondaryParam = QueryParam(uri = Uri.parse("content://mms-sms/canonical-addresses"), projection = arrayOf("address"),
            selection = "_id=?")
        val secondaryQuery = SecondaryQuery(lookup = ThreadsColumns.RECIPIENT_IDS, queryParam = secondaryParam)
        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params, listOf(secondaryQuery)))
    }

    /**
     * Additional fields for sms and mms if not appending '?simple=true' to the URI.
     */
    @Test
    fun querySmsMmsConversationWithPhoneNumberLookup() {
        // content://mms-sms/conversations
        // same as MmsSms.CONTENT_CONVERSATIONS_URI?
        val params = QueryParam(uri = Threads.CONTENT_URI,
            projection = arrayOf(ThreadsColumns._ID, Sms.DATE, Sms.ADDRESS))

        val secondaryParam = QueryParam(uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI, projection = arrayOf(ContactsContract.PhoneLookup._ID,
            ContactsContract.PhoneLookup.LOOKUP_KEY,
            ContactsContract.PhoneLookup.DISPLAY_NAME))
        val secondaryQuery = SecondaryQuery(lookup = Sms.ADDRESS, queryParam = secondaryParam, appendUri = true)
        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params, listOf(secondaryQuery)))
    }

    @Test
    fun querySmsConversation() {
        // content://sms/conversations
        val params = QueryParam(uri = Sms.Conversations.CONTENT_URI,
              projection = arrayOf(Sms.Conversations.THREAD_ID, Sms.Conversations.SNIPPET))

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun querySmsConversationAllColumns() {
        // content://sms/conversations
        // msg_count, thread_id, snippet
        val params = QueryParam(uri = Sms.Conversations.CONTENT_URI)

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun queryCompleteConversation() {
        // if this URI undocumented? Cannot have null projection or will cause error
        // projection can use any combination of the column fields in android.provider.Telephony.Mms
        // and android.provider.Telephony.Sms
        val params = QueryParam(uri = Uri.parse("content://mms-sms/complete-conversations"),
            projection = arrayOf(MmsSms.TYPE_DISCRIMINATOR_COLUMN, Sms.ADDRESS,
                Sms.THREAD_ID))

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun saveConversation() {
        // content://mms-sms/conversations
        val params = QueryParam(uri = MmsSms.CONTENT_CONVERSATIONS_URI)

        val queryProcessor = JsonFileQuery(true, context,"mms-sms-conversations.json")
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun saveConversationSample() {
        // content://mms-sms/conversations
        val params = QueryParam(uri = MmsSms.CONTENT_CONVERSATIONS_URI, projection = arrayOf(
            Sms.ADDRESS,
            Sms.DATE,
            Sms.BODY,
            Sms.PERSON,
            Sms.THREAD_ID,
            Mms.DATE,
            Mms.THREAD_ID))

        val queryProcessor = JsonFileQuery(true, context,"mms-sms-conversations-sample.json")
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun saveCompleteConversation() {
        val params = QueryParam(uri = Uri.parse("content://mms-sms/complete-conversations"),
            projection = arrayOf(
                MmsSms.TYPE_DISCRIMINATOR_COLUMN,
                MmsSms._ID,
                Mms.DATE,
                Mms.DATE_SENT,
                Mms.READ,
                Mms.THREAD_ID,
                Mms.LOCKED,

                Sms.ADDRESS,
                Sms.BODY,
                Sms.SEEN,
                Sms.TYPE,
                Sms.STATUS,
                Sms.ERROR_CODE,

                Mms.SUBJECT,
                Mms.SUBJECT_CHARSET,
                Mms.SEEN,
                Mms.MESSAGE_TYPE,
                Mms.MESSAGE_BOX,
                Mms.DELIVERY_REPORT,
                Mms.READ_REPORT,
                MmsSms.PendingMessages.ERROR_TYPE,
                Mms.STATUS
            ))

        val queryProcessor = JsonFileQuery(true, context,"mms-sms-complete-conversations.json")
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun saveCompleteConversationSample() {
        val params = QueryParam(uri = Uri.parse("content://mms-sms/complete-conversations"),
            projection = arrayOf(MmsSms.TYPE_DISCRIMINATOR_COLUMN,
                Sms.ADDRESS,
                Sms.DATE,
                Sms.BODY,
                Sms.PERSON,
                Sms.THREAD_ID,
                Mms.DATE,
                Mms.THREAD_ID))

        val queryProcessor = JsonFileQuery(true, context,"mms-sms-complete-conversations-sample.json")
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun saveSmsConversationAllColumns() {
        // content://sms/conversations
        // msg_count, thread_id, snippet
        val params = QueryParam(uri = Sms.Conversations.CONTENT_URI)

        val queryProcessor = JsonFileQuery(true, context,"sms-conversations.json")
        assertTrue(queryProcessor.query(context, params))
    }

}