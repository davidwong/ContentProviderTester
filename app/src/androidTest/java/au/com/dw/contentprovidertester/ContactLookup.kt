package au.com.dw.contentprovidertester

import android.provider.ContactsContract
import android.provider.Telephony
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.query.model.SecondaryQuery

/**
 * Secondary query for looking up a contact name for a phone number.
 */
fun getPhoneLookupForContactQuery(): SecondaryQuery {
    val secondaryParam = QueryParam(
        uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI, projection = arrayOf(
            ContactsContract.PhoneLookup._ID,
            ContactsContract.PhoneLookup.LOOKUP_KEY,
            ContactsContract.PhoneLookup.DISPLAY_NAME
        )
    )
    val secondaryQuery = SecondaryQuery(
        lookup = Telephony.Sms.ADDRESS,
        queryParam = secondaryParam,
        appendUri = true
    )
    return secondaryQuery
}