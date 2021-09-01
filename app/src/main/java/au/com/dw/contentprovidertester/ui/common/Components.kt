package au.com.dw.contentprovidertester.ui.common

import android.text.TextUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


@Composable
fun PlainField(fieldValue: String, onFieldChange: (String) -> Unit, fieldLabel: String) {
    TextField(
        value = fieldValue,
        onValueChange = onFieldChange,
        label = { Text(fieldLabel) }
    )
}

// for convenience similate a drop down list (not currently available in the compose library) e.g. for commonly
// used query URI's

/**
 * The first 3 parameters are for the text field (same as for PlainText) and the drop down parameters
 * are for the dropdown list with a map of the labels to display and the values to use when an item
 * from the list is selected.
 */
@Composable
fun DropDownField(fieldValue: String,
                  onFieldChange: (String) -> Unit,
                  fieldLabel: String,
                  dropDownItems: Map<String, Any>,
                  onDropDownSelected: (String, Map<String, Any>) -> Unit) {
    Box() {
        var expanded by remember { mutableStateOf(false) }
        val icon = if (expanded)
            Icons.Filled.Search
        else
            Icons.Filled.ArrowDropDown

        OutlinedTextField(
            value = fieldValue,
            onValueChange = onFieldChange,
//            modifier = Modifier.fillMaxWidth(),
            label = { Text(fieldLabel) },
            trailingIcon = {
                Icon(icon, "contentDescription", Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            // can't use map directly here as will get error message about not being inside
            // a composable, so use a separate lookup instead
            dropDownItems.keys.forEach { label ->
                DropdownMenuItem(onClick = {
                    onDropDownSelected(label, dropDownItems)
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}

/**
 * For use in fields where we want to add an item to the field, instead of replacing it.
 */
fun addQueryColumn(projection : String, newValue : String): String {
    if (TextUtils.isEmpty(projection))
    {
        return newValue
    }
    else{
        return projection + ", " + newValue
    }
}