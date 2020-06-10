package au.com.dw.contentprovidertester.query.model

/**
 * Parameters to pass to ContentResolver.query(). Leave projection as null to get all columns
 * in the results.
 */
data class QueryParam (
    val uri: String,
    val projection: Array<String>? = null,
    val selection: String? = null,
    val selectionArgs: Array<String>? = null,
    val sortOrder: String? = null
)
