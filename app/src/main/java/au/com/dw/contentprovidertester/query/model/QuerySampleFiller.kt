package au.com.dw.contentprovidertester.query.model

import android.net.Uri
import android.provider.ContactsContract
import android.provider.Telephony

class QuerySampleFiller {


    // lists of potential column names for projections and sort order

    val smsColumns = listOf(
        Telephony.Sms.Inbox._ID,
        Telephony.Sms.Inbox.THREAD_ID,
        Telephony.Sms.Inbox.ADDRESS,
        Telephony.Sms.Inbox.BODY,
        Telephony.Sms.Inbox.DATE,
        Telephony.Sms.Inbox.DATE_SENT,
        Telephony.Sms.Inbox.READ
    )

    val mmsColumns = listOf(
        Telephony.Mms.Inbox._ID,
        Telephony.Mms.Inbox.THREAD_ID,
        Telephony.Mms.Inbox.DATE,
        Telephony.Mms.Inbox.DATE_SENT,
        Telephony.Mms.Inbox.READ
    )

    val threadColumns = listOf(
        Telephony.ThreadsColumns._ID,
        Telephony.ThreadsColumns.RECIPIENT_IDS,
        Telephony.ThreadsColumns.DATE,
        Telephony.MmsSms.TYPE_DISCRIMINATOR_COLUMN
    )

    val smsConversationColumns = listOf(
        Telephony.Sms.Conversations.THREAD_ID,
        Telephony.Sms.Conversations.SNIPPET
    )

    // not used yet
    val sortOrder = listOf(
        Telephony.Sms.Inbox.DEFAULT_SORT_ORDER,
        "LIMIT",
        "OFFSET"
    )

    val phoneColumns = listOf(
        ContactsContract.PhoneLookup._ID,
        ContactsContract.PhoneLookup.LOOKUP_KEY,
        ContactsContract.PhoneLookup.DISPLAY_NAME
    )

    val contactAddressNameColumns = listOf(
        ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME_ALTERNATIVE,
        ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME_PRIMARY,
    )

    val addressColumnsMap = mapOf(
        "FORMATTED_ADDRESS" to ContactsContract.CommonDataKinds.StructuredPostal.DATA,
        "TYPE" to ContactsContract.CommonDataKinds.StructuredPostal.DATA2,
        "STREET" to ContactsContract.CommonDataKinds.StructuredPostal.DATA4,
        "POBOX" to ContactsContract.CommonDataKinds.StructuredPostal.DATA5,
        "NEIGHBORHOOD" to ContactsContract.CommonDataKinds.StructuredPostal.DATA6,
        "CITY" to ContactsContract.CommonDataKinds.StructuredPostal.DATA7,
        "REGION" to ContactsContract.CommonDataKinds.StructuredPostal.DATA8,
        "POSTCODE" to ContactsContract.CommonDataKinds.StructuredPostal.DATA9,
        "COUNTRY" to ContactsContract.CommonDataKinds.StructuredPostal.DATA10
    )

    /**
     * Sample content provider URI's.
     *
     * Map key - labels to display in query URI list
     * Map value =
     *  first - query URI (use toString() to use contents
     *  second - labels to display in projections list
     *  third - content to add to projects
     *
     * todo how to add DISTINCT to start of projection
     */
    val uris = mapOf<String, Pair<Uri, Map<String, String>>>(
        "Sms.Inbox.CONTENT_URI" to Pair(Telephony.Sms.Inbox.CONTENT_URI, listToMap(smsColumns)),
        "Sms.CONTENT_URI" to Pair(Telephony.Sms.CONTENT_URI, listToMap(smsColumns)),
        "Sms.Conversations.CONTENT_URI" to Pair(Telephony.Sms.Conversations.CONTENT_URI, listToMap(smsConversationColumns)),
        "Mms.Inbox.CONTENT_URI" to Pair(Telephony.Mms.Inbox.CONTENT_URI, listToMap(mmsColumns)),
        "Mms.CONTENT_URI" to Pair(Telephony.Mms.CONTENT_URI, listToMap(mmsColumns)),
        "MmsSms.CONTENT_CONVERSATIONS_URI" to Pair(Telephony.MmsSms.CONTENT_CONVERSATIONS_URI, listToMap(threadColumns + smsColumns + mmsColumns)),
        "Threads.CONTENT_URI" to Pair(Telephony.Threads.CONTENT_URI, listToMap(threadColumns + smsColumns)), // same as MmsSms.CONTENT_CONVERSATIONS_URI?
        "Threads.CONTENT_URI (simple)" to Pair(Uri.parse(Telephony.Threads.CONTENT_URI.toString() + "?simple=true"), listToMap(threadColumns)),
        "MmsSms Complete Conversation (undocumented)" to Pair(Uri.parse("content://mms-sms/complete-conversations"), listToMap(threadColumns + smsColumns + mmsColumns)),
        "ContactsContract.Data.CONTENT_URI" to Pair(ContactsContract.Data.CONTENT_URI, listToMap(contactAddressNameColumns)),
        "ContactsContract.CommonDataKinds.Phone.CONTENT_URI" to Pair(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, listToMap(phoneColumns)),
        "ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI" to Pair(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, addressColumnsMap)
    )

    fun uriLabels(): List<String> = uris.keys.toList()

    /**
     * Create a map from a list where the keys and values are the same.
     */
    fun listToMap(theList : List<String>): Map<String, String>
    {
        val theMap = mutableMapOf<String, String>()
        
        theList.forEach { 
            theMap.put(it, it)
        }
        return theMap.toMap()
    }
}