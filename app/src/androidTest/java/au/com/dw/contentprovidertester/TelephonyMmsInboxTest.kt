package au.com.dw.contentprovidertester

import android.Manifest
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Telephony
import android.provider.Telephony.Mms.Inbox
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
class TelephonyMmsInboxTest {
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
    fun queryMmsInboxAllColumns() {
        val params = QueryParam(uri = Uri.parse("content://mms/inbox"))

        val queryProcessor = JsonQuery(true)
        assertTrue(queryProcessor.query(context, params))
    }

}