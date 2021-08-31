package au.com.dw.contentprovidertester

import android.Manifest
import android.content.Context
import android.provider.Telephony.Sms
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import au.com.dw.contentprovidertester.query.JsonFileQuery
import au.com.dw.contentprovidertester.query.model.QueryParam
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


/**
 * Instrumented test to see the results of ContentResolver queries for Telephony content provider.
 */
class TelephonyPagingFilteringTest {
    @Rule
    @JvmField
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.READ_SMS)

    lateinit var context : Context

    val limit = 10

    @Before
    fun setup() {
        // Context of the app under test.
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    /**
     * Get a copy of all the SMS data as a control to verify against pages.
     */
    @Test
    fun saveSmsAsControl() {
        val params = QueryParam(uri = Sms.CONTENT_URI, projection = arrayOf(Sms.ADDRESS, Sms.BODY, Sms.DATE),
            sortOrder = Sms.DEFAULT_SORT_ORDER)

        val queryProcessor = JsonFileQuery(true, context,"sms-control.json")
        assertTrue(queryProcessor.query(context, params))
    }

    @Test
    fun saveSmsPage() {
        // change page number to get different page of results
        val page = 1
        val offset = (page - 1) * limit
        val params = QueryParam(uri = Sms.CONTENT_URI, projection = arrayOf(Sms.ADDRESS, Sms.BODY, Sms.DATE),
            sortOrder = Sms.DEFAULT_SORT_ORDER + " LIMIT " + limit + " OFFSET " + offset)

        val queryProcessor = JsonFileQuery(true, context,"sms-page" + page + ".json")
        assertTrue(queryProcessor.query(context, params))
    }

    /**
     * Get a filtered copy of SMS messages.
     */
    @Test
    fun saveSmsFiltered() {
        val filter = "02"
        // contains the filter string
        val arg = "%" + filter + "%"
        val selection = Sms.ADDRESS + " LIKE ?"

        val params = QueryParam(uri = Sms.CONTENT_URI, projection = arrayOf(Sms.ADDRESS, Sms.BODY, Sms.DATE),
            selection = selection, selectionArgs = arrayOf(arg),sortOrder = Sms.DEFAULT_SORT_ORDER)

        val queryProcessor = JsonFileQuery(true, context,"sms-filter.json")
        assertTrue(queryProcessor.query(context, params))
    }

}