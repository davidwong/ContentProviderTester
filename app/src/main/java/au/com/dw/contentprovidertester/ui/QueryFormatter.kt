package au.com.dw.contentprovidertester.ui

import java.net.URLDecoder
import java.net.URLEncoder

fun escapeUriString(uri: String): String = URLEncoder.encode(uri, "UTF-8");

fun unEscapeUriString(escaped: String): String = URLDecoder.decode(escaped, "UTF-8");

/**
 * Clean up optional params for content provider query
 */
fun checkQueryString(value: String?): String? {
    if (null != value && value.isNotBlank())
        return value
    else
        return null
}

/**
 * Clean up optional array params for content provider query
 */
fun checkQueryStringArray(arrayString: String?): Array<String>? {
    if (null != arrayString && arrayString.isNotBlank())
        return arrayString.split(",").toTypedArray()
    else
        return null
}
