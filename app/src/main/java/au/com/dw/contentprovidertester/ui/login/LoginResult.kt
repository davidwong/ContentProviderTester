package au.com.dw.contentprovidertester.ui.login

import au.com.dw.contentprovidertester.data.model.QueryResult

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
        val success: QueryResult? = null,
        val error: Int? = null
)