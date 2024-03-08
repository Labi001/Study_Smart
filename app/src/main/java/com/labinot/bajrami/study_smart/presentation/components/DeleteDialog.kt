package com.labinot.bajrami.study_smart.presentation.components


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue




@Composable
fun DeleteDialog(
    isOpen: Boolean,
    title: String,
    bodyText: String,
    OnDismissRequest: () -> Unit,
    OnConfirmButtonClick: () -> Unit
) {

    var subjectNameError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var goalHoursError by rememberSaveable {
        mutableStateOf<String?>(null)
    }



    if (isOpen) {

        AlertDialog(onDismissRequest = { OnDismissRequest.invoke() },
            title = { Text(text = title) },
            text = { Text(text = bodyText) },
            confirmButton = {

                TextButton(
                    onClick = { OnConfirmButtonClick.invoke() },
                    enabled = subjectNameError == null && goalHoursError == null
                ) {

                    Text(text = "Delete")

                }

            },
            dismissButton = {

                TextButton(onClick = { OnDismissRequest.invoke() }) {

                    Text(text = "Cancel")

                }

            })

    }


}