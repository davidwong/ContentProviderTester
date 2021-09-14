package au.com.dw.contentprovidertester.ui

import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.test.*
import au.com.dw.contentprovidertester.query.model.QuerySampleFiller
import au.com.dw.contentprovidertester.ui.query.QueryBodyContent
import au.com.dw.contentprovidertester.ui.query.QueryScreen
import de.mannodermaus.junit5.compose.createComposeExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class QueryTest {
    @RegisterExtension
    @JvmField
    val extension = createComposeExtension()

    @Test
    fun testQueryForm() {
        extension.setContent {
            val querySampleFiller = QuerySampleFiller()
            QueryScreen(rememberScaffoldState(), querySampleFiller) { c, s1, s2, s3, s4, s5 -> Unit }
//            QueryBodyContent(querySampleFiller = querySampleFiller) { c, s1, s2, s3, s4, s5 -> Unit }
        }
//        extension.onRoot().printToLog("testQueryForm")
//        extension.onRoot(useUnmergedTree = true).printToLog("testQueryForm unmerged")

        val queryButton = extension.onNodeWithText("Query")
        val uriText = extension.onNodeWithText("uri *")

        // check query button is initially disabled
        queryButton.assertIsNotEnabled()

        // add text to URI field and check query button enabled
        uriText.performTextInput("a")
        queryButton.assertIsEnabled()

        // clear URI text and check query button disabled again
        uriText.performTextClearance()
        queryButton.assertIsNotEnabled()

        // select item from URI dropdown and check query button enabled again
        val uriDropDownButton = extension.onNodeWithContentDescription("uri dropdown")
        uriDropDownButton.performClick()
        val uriDropDownMenuItem = extension.onNodeWithText("Sms.Inbox.CONTENT_URI")
        uriDropDownMenuItem.assertExists()
        uriDropDownMenuItem.performClick()
        queryButton.assertIsEnabled()

        // also check after URI dropdown menu item has been selected, that the projection dropdown
        // is populated with related menu items for that URI
        val projectionDropDownButton = extension.onNodeWithContentDescription("projection dropdown")
        val projectionText = extension.onNodeWithText("projection")

        // select first menu item
        projectionDropDownButton.performClick()
        val projectionDropDownMenuItem1 = extension.onNodeWithText("address")
        projectionDropDownMenuItem1.assertExists()
        projectionDropDownMenuItem1.performClick()
        // check projection text contains first menu item
        projectionText.assertTextContains("address")

        // select second menu item
        projectionDropDownButton.performClick()
        val projectionDropDownMenuItem2 = extension.onNodeWithText("body")
        projectionDropDownMenuItem2.assertExists()
        projectionDropDownMenuItem2.performClick()
        // check projection text contains both menu items selected
        projectionText.assertTextContains("address", substring = true)
        projectionText.assertTextContains("body", substring = true)
    }
}