package au.com.dw.contentprovidertester

import android.Manifest
import android.R.attr.phoneNumber
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import au.com.dw.contentprovidertester.query.JsonQuery
import au.com.dw.contentprovidertester.query.model.QueryParam
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


/**
 * Instrumented test to see the results of ContentResolver queries for Telephony content provider.
 */
class ContactsTest {
    @Rule
    @JvmField
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.READ_CONTACTS)

    lateinit var context : Context

    @Before
    fun setup() {
        // Context of the app under test.
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun queryPhoneNumbers() {
        // content://com.android.contacts/data/phones

        val params = QueryParam(uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI.toString())

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params, emptyList()))
    }

    @Test
    fun queryCanonicalAddress() {
        // _id, address (phone)

        val params = QueryParam(uri = "content://mms-sms/canonical-addresses")

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params, emptyList()))
    }

    @Test
    fun queryContactForPhoneNumber() {
        // content://com.android.contacts/phone_lookup/xxx
        val uri: Uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode("1111111")
        )
        val params = QueryParam(uri = uri.toString(), projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.NORMALIZED_NUMBER))

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params, emptyList()))
    }

}