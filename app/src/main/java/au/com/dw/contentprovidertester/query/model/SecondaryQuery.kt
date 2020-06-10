package au.com.dw.contentprovidertester.query.model

/**
 * Parameters to pass to ContentResolver.query() for a secondary query for a column from the
 * first query.
 * e.g.
 * For a telephony conversation query there is a field recipient id. We want to do a secondary
 * query to lookup the address (phone number) for the recipient:
 * lookup = recipient id
 * queryParam = query parameters for the secondary query
 */
data class SecondaryQuery(
    val lookup: String,
    val queryParam: QueryParam
)