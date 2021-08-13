package au.com.dw.contentprovidertester

import android.Manifest
import android.R.id
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal
import android.telephony.PhoneNumberUtils
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import au.com.dw.contentprovidertester.query.JsonQuery
import au.com.dw.contentprovidertester.query.model.QueryParam
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Instrumented test to check how contacts display name is generated for the database.
 * The display name can be used to link the various contact data tables, e.g. phone, email, address, etc.
 *
 * This shows the the display name may differ depending on whether a contact is inserted directly
 * (using ContentResolver.insert) or using batch (ContentResolver.applyBatch).
 *
 * Having blank name (space only):
 * - both first name and surname blank
 * - one name blank and the other null
 * makes the display name null instead of using the first phone number.
 *
 * It seems only having both names null uses the phone number as display name.
 */
@RunWith(Parameterized::class)
class ContactsDisplayNameTest(val contactDetails: NamePhoneInput, val displayName: String?) {

    companion object {
        const val testFirstName = "firstName"
        const val testSurname = "lastName"
        const val SPACE = " "
        const val testCombinedName = testFirstName + SPACE + testSurname
        const val testFirstPhone = "1111111"
        const val testSecondPhone = "2222222"
        // use in array as can't just pass null as expected value since can't infer type
        const val NULL = "NULL"
        @JvmStatic
        @Parameterized.Parameters
        fun data() : Collection<Array<Any>> {
            return listOf(
                // both names, no phones
                arrayOf(NamePhoneInput(firstName = testFirstName, surname = testSurname), testCombinedName),
                // both names, phone
                arrayOf(NamePhoneInput(firstName = testFirstName, surname = testSurname, firstPhone = testFirstPhone), testCombinedName),

                // first name only, no phone
                arrayOf(NamePhoneInput(firstName = testFirstName), testFirstName),
                // first name only, phone
                arrayOf(NamePhoneInput(firstName = testFirstName, firstPhone = testFirstPhone), testFirstName),
                // first name only, surname blank, no phones
                arrayOf(NamePhoneInput(firstName = testFirstName, surname = SPACE), testFirstName),
                // first name only, surname blank, , phone
                arrayOf(NamePhoneInput(firstName = testFirstName, surname = SPACE, firstPhone = testFirstPhone), testFirstName),

                // surname only, no phones
                arrayOf(NamePhoneInput(surname = testSurname), testSurname),
                // surname only, phone
                arrayOf(NamePhoneInput(surname = testSurname, firstPhone = testFirstPhone), testSurname),
                // surname only, first name blank, no phones
                arrayOf(NamePhoneInput(firstName = SPACE, surname = testSurname), testSurname),
                // surname only, first name blank, phone
                arrayOf(NamePhoneInput(firstName = SPACE, surname = testSurname, firstPhone = testFirstPhone), testSurname),

                // no names, no phones
                arrayOf(NamePhoneInput(), NULL),
                // no names, phone
                arrayOf(NamePhoneInput(firstPhone = testFirstPhone), testFirstPhone) ,
                // no names, both phones
                arrayOf(NamePhoneInput(firstPhone = testFirstPhone, secondPhone = testSecondPhone), testFirstPhone),

                // both names blank, no phones
                arrayOf(NamePhoneInput(firstName = SPACE, surname = SPACE), NULL),
                // both names blank, phone
                arrayOf(NamePhoneInput(firstName = SPACE, surname = SPACE, firstPhone = testFirstPhone), NULL),
                // both names blank, both phones
                arrayOf(NamePhoneInput(firstName = SPACE, surname = SPACE, firstPhone = testFirstPhone, secondPhone = testSecondPhone), NULL),

                // first name only blank, no phones
                arrayOf(NamePhoneInput(firstName = SPACE), NULL),
                // first name only names blank, phone
                arrayOf(NamePhoneInput(firstName = SPACE, firstPhone = testFirstPhone), NULL),
                // first name only names blank, both phones
                arrayOf(NamePhoneInput(firstName = SPACE, firstPhone = testFirstPhone, secondPhone = testSecondPhone), NULL),

                // surname only blank, no phones
                arrayOf(NamePhoneInput(surname = SPACE), NULL),
                // surname only names blank, phone
                arrayOf(NamePhoneInput(surname = SPACE, firstPhone = testFirstPhone), NULL),
                // surname only names blank, both phones
                arrayOf(NamePhoneInput(surname = SPACE, firstPhone = testFirstPhone, secondPhone = testSecondPhone), NULL)

            )
        }
    }

    @Rule
    @JvmField
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
        )

    lateinit var context : Context

    @Before
    fun setup() {
        // Context of the app under test.
        context = InstrumentationRegistry.getInstrumentation().targetContext
        deleteContactsBatch()
    }

    @Test
    fun checkDisplayName() {
        val rawContactId = insertContact(contactDetails)
        val displayNameRetrieved = getDisplayName(rawContactId)

        if (NULL.equals(displayName))
        {
            assertNull(displayNameRetrieved)
        }
        else {
            assertEquals(displayName, displayNameRetrieved)
        }

        // tidy up
        deleteContact(rawContactId)
    }

    private fun insertContact(contactDetails: NamePhoneInput): Long
    {
        // insert an empty contact.
        val contentValues = ContentValues()
        val rawContactUri: Uri? = context.getContentResolver().insert(
            ContactsContract.RawContacts.CONTENT_URI,
            contentValues
        )
        // get the newly created contact raw id.
        val rawContactId = ContentUris.parseId(rawContactUri!!)

        // insert names
        insertName(contactDetails, rawContactId)

        // insert phone numbers
        if (contactDetails.firstPhone != null)
        {
            insertPhone(contactDetails.firstPhone!!, Phone.TYPE_HOME, rawContactId)
        }
        if (contactDetails.secondPhone != null)
        {
            insertPhone(contactDetails.secondPhone!!, Phone.TYPE_WORK, rawContactId)
        }

        // insert dummy address to allow lookup
        insertAddress("10 test street", StructuredPostal.TYPE_HOME, rawContactId)

        return rawContactId
    }

    private fun insertName(contactDetails: NamePhoneInput, rawContactId: Long)
    {
        val contentValues = ContentValues()

        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
        )
        // put contact input names
        contentValues.put(
            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
            contactDetails.surname
        )
        contentValues.put(
            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
            contactDetails.firstName
        )

        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues)
    }

    private fun insertPhone(phoneNumber: String, type: Int, rawContactId: Long)
    {
        val contentValues = ContentValues()

        // Each contact must has an id to avoid java.lang.IllegalArgumentException: raw_contact_id is required error.
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)

        // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(
            ContactsContract.Data.MIMETYPE,
            Phone.CONTENT_ITEM_TYPE
        )

        // Put phone number value and type.
        contentValues.put(Phone.NUMBER, phoneNumber)
        contentValues.put(Phone.TYPE, type)

        // Insert new contact data into phone contact list.
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues)
    }

    private fun insertAddress(address: String, type: Int, rawContactId: Long)
    {
        val contentValues = ContentValues()

        // Each contact must has an id to avoid java.lang.IllegalArgumentException: raw_contact_id is required error.
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)

        // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(
            ContactsContract.Data.MIMETYPE,
            StructuredPostal.CONTENT_ITEM_TYPE
        )

        // Put address value and type.
        contentValues.put(StructuredPostal.STREET, address)
        contentValues.put(StructuredPostal.TYPE, type)

        // Insert new contact data into address contact list.
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, contentValues)
    }

    // generic version
//    private fun getDisplayName(rawContactId: Long): String?
//    {
//        var displayName: String? = null
//        val selectionArgs = arrayOf(rawContactId.toString())
//        val cursor: Cursor? = context.contentResolver.query(
//            ContactsContract.Data.CONTENT_URI,
//            null,
//            Phone.RAW_CONTACT_ID + " = ?", selectionArgs, null
//        )
//
//        cursor?.moveToFirst()
//        displayName = cursor?.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME))
//
//        cursor?.close()
//
//        return displayName
//    }

    // version that looks up address table
    private fun getDisplayName(rawContactId: Long): String?
    {
        var displayName: String? = null
        val selectionArgs = arrayOf(rawContactId.toString())
        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            StructuredPostal.RAW_CONTACT_ID + " = ?", selectionArgs, null
        )

        cursor?.moveToFirst()
        displayName = cursor?.getString(cursor.getColumnIndex(StructuredPostal.DISPLAY_NAME))
        println("display name=" + displayName)
        println("primary name=" + cursor?.getString(cursor.getColumnIndex(StructuredPostal.DISPLAY_NAME_PRIMARY)))
        println("alt name=" + cursor?.getString(cursor.getColumnIndex(StructuredPostal.DISPLAY_NAME_ALTERNATIVE)))
        println("source name=" + cursor?.getString(cursor.getColumnIndex(StructuredPostal.DISPLAY_NAME_SOURCE)))

        cursor?.close()

        return displayName
    }

    private fun deleteContact(rawContactId: Long)
    {

        val contentResolver: ContentResolver = context.getContentResolver()

        //******************************* delete data table related data ****************************************
        // Data table content process uri.
        val dataContentUri = ContactsContract.Data.CONTENT_URI

        // Create data table where clause.
        val dataWhereClauseBuf = StringBuffer()
        dataWhereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID)
        dataWhereClauseBuf.append(" = ")
        dataWhereClauseBuf.append(rawContactId)

        // Delete all this contact related data in data table.
        contentResolver.delete(dataContentUri, dataWhereClauseBuf.toString(), null)


        //******************************** delete raw_contacts table related data ***************************************
        // raw_contacts table content process uri.
        val rawContactUri = ContactsContract.RawContacts.CONTENT_URI

        // Create raw_contacts table where clause.
        val rawContactWhereClause = StringBuffer()
        rawContactWhereClause.append(ContactsContract.RawContacts._ID)
        rawContactWhereClause.append(" = ")
        rawContactWhereClause.append(rawContactId)

        // Delete raw_contacts table related data.
        contentResolver.delete(rawContactUri, rawContactWhereClause.toString(), null)


        //******************************** delete contacts table related data ***************************************
        // contacts table content process uri.
        val contactUri = ContactsContract.Contacts.CONTENT_URI


        // Create contacts table where clause.
        val contactWhereClause = StringBuffer()
        contactWhereClause.append(ContactsContract.Contacts._ID)
        contactWhereClause.append(" = ")
        contactWhereClause.append(rawContactId)

        // Delete raw_contacts table related data.
        contentResolver.delete(contactUri, contactWhereClause.toString(), null)
    }

    @Test
    fun checkDisplayNameBatchVersion() {
        // clear all contacts first, since can't get raw contacts id using batch.
        deleteContactsBatch()

        insertContactBatch(contactDetails)
        // get display name from first record, as the contact just inserted should be the only one
        val displayNameRetrieved = getDisplayNameBatch()

        if (NULL.equals(displayName))
        {
            assertNull(displayNameRetrieved)
        }
        else {
            assertEquals(displayName, displayNameRetrieved)
        }

        // tidy up
        deleteContactsBatch()
    }

    private fun insertContactBatch(contactDetails: NamePhoneInput)
    {
        val operations = ArrayList<ContentProviderOperation>()

        // create new contact
        ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).apply {
            // local account
            withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
            withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            operations.add(build())
        }

        // insert names
        insertNameBatch(contactDetails, operations)

        // insert phone numbers
        if (contactDetails.firstPhone != null)
        {
            insertPhoneBatch(contactDetails.firstPhone!!, Phone.TYPE_HOME, operations)
        }
        if (contactDetails.secondPhone != null)
        {
            insertPhoneBatch(contactDetails.secondPhone!!, Phone.TYPE_WORK, operations)
        }

        // insert dummy address to allow lookup
        insertAddressBatch("10 test street", StructuredPostal.TYPE_HOME, operations)

        context.contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
    }

    private fun insertNameBatch(contactDetails: NamePhoneInput, operations: ArrayList<ContentProviderOperation>)
    {
        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).apply {
            withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contactDetails.firstName)
            withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contactDetails.surname)
            operations.add(build())
        }
    }

    private fun insertPhoneBatch(phoneNumber: String, type: Int, operations: ArrayList<ContentProviderOperation>)
    {
        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).apply {
            withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
            withValue(Phone.NUMBER, phoneNumber)
            withValue(Phone.TYPE, type)
            operations.add(build())
        }
    }

    private fun insertAddressBatch(address: String, type: Int, operations: ArrayList<ContentProviderOperation>)
    {
        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).apply {
            withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            withValue(ContactsContract.Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE)
            withValue(StructuredPostal.STREET, address)
            withValue(StructuredPostal.TYPE, type)
            operations.add(build())
        }
    }

    private fun getDisplayNameBatch(): String?
    {
        var displayName: String? = null
        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI, null, null, null, null)

        cursor?.moveToFirst()
        displayName = cursor?.getString(cursor.getColumnIndex(StructuredPostal.DISPLAY_NAME))
        println("display name=" + displayName)
        println("primary name=" + cursor?.getString(cursor.getColumnIndex(StructuredPostal.DISPLAY_NAME_PRIMARY)))
        println("alt name=" + cursor?.getString(cursor.getColumnIndex(StructuredPostal.DISPLAY_NAME_ALTERNATIVE)))
        println("source name=" + cursor?.getString(cursor.getColumnIndex(StructuredPostal.DISPLAY_NAME_SOURCE)))

        cursor?.close()

        return displayName
    }

    private fun deleteContactsBatch()
    {
        val contentResolver = context.contentResolver
        val operations = java.util.ArrayList<ContentProviderOperation>()
        operations.add(
            ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .build()
        )
        contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
    }

    @Test
    fun showAllDisplayNamesForAddresses()
    {
        val cursor: Cursor? = context.contentResolver.query(
            StructuredPostal.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        while (cursor!!.moveToNext()) {
            val street = cursor?.getString(cursor.getColumnIndex(StructuredPostal.STREET))
            println("street name is " + street)
            val displayName = cursor?.getString(cursor.getColumnIndex(StructuredPostal.DISPLAY_NAME))
            val displayNamePrimary = cursor?.getString(cursor.getColumnIndex(StructuredPostal.DISPLAY_NAME_PRIMARY))
            val displayNameAlt = cursor?.getString(cursor.getColumnIndex(StructuredPostal.DISPLAY_NAME_ALTERNATIVE))
            if (null == displayName)
            {
                println("display name is null")
            }
            else if ("".equals(displayName.trim())) {
                println("display name is space")
            }
            else
            {
                println("display name is " + displayName)
            }

        }
        cursor?.close()
    }

    @Test
    fun getDisplayNameFromVCard()
    {
        var tempDisplayName: String? = null

        val firstName: String? = contactDetails.firstName
        val surname: String? = contactDetails.surname

        if (firstName == null && surname == null)
        {
            // both names are null, then use first phone number if available
            if (contactDetails.firstPhone != null)
            {
                tempDisplayName = contactDetails.firstPhone
            }
        }
        else
        {
            var aggregatedName: String? = null
            if (firstName != null && surname != null)
            {
                aggregatedName = firstName + " " + surname
            }
            else if (surname == null)
            {
                aggregatedName = firstName
            }
            else if (firstName == null)
            {
                aggregatedName = surname
            }

            if (!(aggregatedName!!.trim().length == 0))
            {
                tempDisplayName = aggregatedName.trim()
            }
        }
        if (NULL.equals(displayName))
        {
            assertNull(tempDisplayName)
        }
        else {
            assertEquals(displayName, tempDisplayName)
        }
    }
}

data class NamePhoneInput(
    var firstName: String? = null,
    var surname: String? = null,
    var firstPhone: String? = null,
    var secondPhone: String? = null
)