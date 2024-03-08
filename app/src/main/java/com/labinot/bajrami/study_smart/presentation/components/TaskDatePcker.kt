package com.labinot.bajrami.study_smart.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(

    state: DatePickerState,
    isOpen:Boolean,
    confirmButtonText:String = "OK",
    dismissButtonText:String = "Cancel",
    onDismissRequest:() -> Unit,
    onConfirmButtonClicked:() -> Unit,
){

    if(isOpen){

        DatePickerDialog(onDismissRequest = { onDismissRequest.invoke() },
            confirmButton = {

                TextButton(onClick = { onConfirmButtonClicked.invoke() }) {

                    Text(text = confirmButtonText)
                }
            },
            dismissButton = {

                TextButton(onClick = { onDismissRequest.invoke() }) {

                    Text(text = dismissButtonText)
                }


            },
            content = {

                DatePicker(state = state,
                    dateValidator = { timesTamp ->

                        val selectedDate = Instant
                            .ofEpochMilli(timesTamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        val currentDate = LocalDate.now(ZoneId.systemDefault())
                        selectedDate>=currentDate

                    })

            })



    }


    
}