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
        GrantPermissionRule.grant(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS)

    lateinit var context : Context

    @Before
    fun setup() {
        // Context of the app under test.
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun querySmsMmsConversationAllColumns() {
        val params = QueryParam(uri = Telephony.Threads.CONTENT_URI.toString())
        val queryProcessor = LogQuery()
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun querySmsMmsConversationSimple() {
        val params = QueryParam(uri = Telephony.Threads.CONTENT_URI.toString() + "?simple=true",
            projection = arrayOf(Telephony.ThreadsColumns._ID, Telephony.ThreadsColumns.RECIPIENT_IDS))
        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun querySmsMmsConversationWithCanonicalPhoneNumberLookup() {
        // content://mms-sms/conversations
        val params = QueryParam(uri = Telephony.Threads.CONTENT_URI.toString() + "?simple=true",
            projection = arrayOf(Telephony.ThreadsColumns._ID, Telephony.ThreadsColumns.RECIPIENT_IDS))

        val secondaryParam = QueryParam(uri = "content://mms-sms/canonical-addresses", projection = arrayOf("address"),
            selection = "_id=?")
        val secondaryQuery = SecondaryQuery(lookup = Telephony.ThreadsColumns.RECIPIENT_IDS, queryParam = secondaryParam)
        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params, listOf(secondaryQuery)))
    }

    @Test
    fun querySmsMmsConversationWithContactsPhoneNumberLookup() {
        // content://mms-sms/conversations
        val params = QueryParam(uri = Telephony.Threads.CONTENT_URI.toString() + "?simple=true",
            projection = arrayOf(Telephony.ThreadsColumns._ID, Telephony.ThreadsColumns.RECIPIENT_IDS))

        val secondaryParam = QueryParam(uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI.toString(),
            selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"=?")
        val secondaryQuery = SecondaryQuery(lookup = Telephony.ThreadsColumns.RECIPIENT_IDS, queryParam = secondaryParam)
        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params, listOf(secondaryQuery)))
    }

    @Test
    fun querySmsConversation() {
        // content://sms/conversations
        val params = QueryParam(uri = Telephony.Sms.Conversations.CONTENT_URI.toString(),
              projection = arrayOf(Telephony.Sms.Conversations.THREAD_ID, Telephony.Sms.Conversations.SNIPPET))

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun querySmsConversationAllColumns() {
        // content://sms/conversations
        // msg_count, thread_id, snippet
        val params = QueryParam(uri = Telephony.Sms.Conversations.CONTENT_URI.toString())

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }
}