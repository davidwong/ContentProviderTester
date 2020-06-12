package au.com.dw.contentprovidertester.query

fun executionTimeDisplay(time: Long): String {
    if (time > 0)
        if (time >= 1E9)
            return (time / 1E9).toString() + " s"
        else
            return (time / 1E6).toString() + " ms"
    else
        return "not recorded"
}