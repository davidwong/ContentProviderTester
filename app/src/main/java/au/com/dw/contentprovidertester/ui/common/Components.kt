package au.com.dw.contentprovidertester.ui.common

import android.text.TextUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import au.com.dw.contentprovidertester.R

@Composable
fun PlainField(fieldValue: String, onFieldChange: (String) -> Unit, fieldLabel: String) {
    TextField(
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.textHorizPadding), vertical = dimensionResource(id = R.dimen.textVertPadding)),
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
                  dropDownIconDescription: String,
                  dropDownItems: Map<String, Any>,
                  onDropDownSelected: (String, Map<String, Any>) -> Unit
                    ) {
    Box(
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.textHorizPadding), vertical = dimensionResource(id = R.dimen.textVertPadding))
        ) {
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
                Icon(icon, dropDownIconDescription, Modifier.clickable { expanded = !expanded })
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
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
 * Dropdown for text fields which require validation. The actual validation is done in the
 * TextFieldState passed, which contains both the field and the validation code.
 */
@Composable
fun DropDownValidatingField(validationState: TextFieldState,
                            fieldLabel: String,
                            imeAction: ImeAction = ImeAction.Next,
                            onImeAction: () -> Unit = {},
                            dropDownIconDescription: String,
                            dropDownItems: Map<String, Any>,
                            onDropDownSelected: (String, Map<String, Any>) -> Unit
) {
    Box(
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.textHorizPadding), vertical = dimensionResource(id = R.dimen.textVertPadding))
        ) {
        var expanded by remember { mutableStateOf(false) }
        val icon = if (expanded)
            Icons.Filled.Search
        else
            Icons.Filled.ArrowDropDown

        OutlinedTextField(
            value = validationState.text,
            onValueChange = { validationState.text = it },
            modifier = Modifier
//                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    validationState.onFocusChange(focusState.isFocused)
                    if (!focusState.isFocused) {
                        validationState.enableShowErrors()
                    }
                },
            label = { Text(fieldLabel) },
            // can't use trailing icon for error, as already used for dropdown icon
            trailingIcon = {
                Icon(icon, dropDownIconDescription, Modifier.clickable { expanded = !expanded })
            },
            isError = validationState.showErrors(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onDone = {
                    onImeAction()
                }
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
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
    // needs to be outside box scope to show below text input
    validationState.getError()?.let { errorMsg ->
        TextFieldError(textError = errorMsg)
    }
}

/**
 * To be removed when [TextField]s support error
 */
@Composable
fun TextFieldError(textError: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = textError,
            modifier = Modifier.fillMaxWidth(),
            style = LocalTextStyle.current.copy(color = MaterialTheme.colors.error)
        )
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

@Composable
fun ProgressIndicator()
{
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize() ){
        CircularProgressIndicator()
    }
}