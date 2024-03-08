package com.labinot.bajrami.study_smart.presentation.screens.task_s

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.labinot.bajrami.study_smart.R
import com.labinot.bajrami.study_smart.presentation.components.DeleteDialog
import com.labinot.bajrami.study_smart.presentation.components.SubjectListBottomSheet
import com.labinot.bajrami.study_smart.presentation.components.TaskDatePicker
import com.labinot.bajrami.study_smart.presentation.domain.TaskCheckBox
import com.labinot.bajrami.study_smart.presentation.domain.utils.Priority
import com.labinot.bajrami.study_smart.presentation.domain.utils.SnackbarEvent
import com.labinot.bajrami.study_smart.presentation.domain.utils.changeMillisToDateString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant

data class TaskScreenNavArgs(

    val taskId: Int?,
    val subjectId: Int?

)


@Destination(navArgsDelegate = TaskScreenNavArgs::class)
@Composable
fun TaskScreenRoute(
    navigator: DestinationsNavigator
){
    val viewModel: TaskScreenViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    TaskScreen (
        state = state,
        snackbarEvent = viewModel.snackBarEventFlow,
        onEvents = viewModel::onEvent,
        onBackButtonClick = {navigator.navigateUp()})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(

    state: TaskState,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onEvents: (TaskEvents) -> Unit,
    onBackButtonClick: () -> Unit

){




    var taskTitleError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var isDeleteDialogOpen by rememberSaveable {

        mutableStateOf(false)
    }

    var isDatePickerDialogOpen by rememberSaveable {

        mutableStateOf(false)
    }

    var datePickerState = rememberDatePickerState(

        initialSelectedDateMillis = Instant.now().toEpochMilli()

    )

    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by rememberSaveable {

        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    taskTitleError = when {

        state.title.isBlank() -> "Please enter task title."
       state.title.length < 4 -> "Task title is too short"
       state.title.length > 30 -> "Task title is too long"

        else ->null

    }

    val snackBarHostState = remember {

        SnackbarHostState()
    }

    LaunchedEffect(key1 = true){

        snackbarEvent.collectLatest { event->

            when(event){

                is SnackbarEvent.ShowSnackBar -> {

                    snackBarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )

                }

                SnackbarEvent.NavigationUp -> {

                    onBackButtonClick()
                }
            }

        }

    }
    
    DeleteDialog(isOpen = isDeleteDialogOpen,
        title = "Delete Task",
        bodyText = "Are you sure,you want to delete this task?" +
        "This action cannot be undone.",
        OnDismissRequest = { isDeleteDialogOpen = false },
        OnConfirmButtonClick = {

            onEvents(TaskEvents.DeleteTask)
            isDeleteDialogOpen = false

        })
    
    
    TaskDatePicker(state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = { isDatePickerDialogOpen = false },
        onConfirmButtonClicked = {

            onEvents(TaskEvents.OnDateChange(millis = datePickerState.selectedDateMillis))
            isDatePickerDialogOpen = false
        })

     SubjectListBottomSheet(
         sheetState = sheetState,
         isOpen = isBottomSheetOpen,
         subjects = state.subjects,
         onSubjectClicked = {subject ->

                            scope.launch { sheetState.hide() }.invokeOnCompletion {

                                if(!sheetState.isVisible) isBottomSheetOpen = false
                            }

             onEvents(TaskEvents.OnRelatedSubjectSelect(subject))
                            
         },
         onDismissRequest = {
             
             isBottomSheetOpen = false
         })
    

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TaskScreenTopBar(
                title = "Task",
                isTaskExist = state.currentTaskId != null,
                isComplete = state.isTaskComplete,
                checkBoxBorderColor = state.priority.color,
                onBackClick = { onBackButtonClick.invoke() },
                onDeleteButtonClick = { isDeleteDialogOpen = true },
                onCheckBoxClick = {onEvents(TaskEvents.OnIsCompleteChange)})
        }
    ) { mPaddingValue->

        Column(modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .fillMaxSize()
            .padding(mPaddingValue)
            .padding(horizontal = 12.dp)) {

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = {onEvents(TaskEvents.OnTitleChange(it))},
                label = { Text(text = "Title")},
                singleLine = true,
                isError = taskTitleError != null && state.title.isNotBlank(),
                supportingText = { Text(text = taskTitleError.orEmpty())})

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = {onEvents(TaskEvents.OnDescriptionChange(it))},
                label = { Text(text = "Description")})

            Spacer(modifier = Modifier.height(20.dp))
            
            Text(text = "Due Date ",
                style = MaterialTheme.typography.bodySmall)
            
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                
                Text(text = state.dueDate.changeMillisToDateString(),
                    style = MaterialTheme.typography.bodyLarge)
                
                IconButton(onClick = {isDatePickerDialogOpen = true }) {
                    
                    Icon(imageVector = Icons.Default.DateRange,
                        contentDescription = "Date Icon")
                    
                }
                
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Priority",
                style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {

                Priority.entries.forEach{ priority->

                    PriorityButton(
                        modifier = Modifier.weight(1f),
                        label = priority.title,
                        backgroundColor = priority.color,
                        borderColor = if(priority == state.priority) {
                            Color.White
                        }else Color.Transparent,
                        labelColor = if(priority == state.priority) {
                            Color.White
                        }else Color.White.copy(alpha = 0.7f),
                        onClick = {
                            onEvents(TaskEvents.OnPriorityChange(priority))
                        }
                    )


                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "Related to Subject ",
                style = MaterialTheme.typography.bodySmall)

            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {

                val firstSubject = state.subjects.firstOrNull()?.name ?: ""

                Text(text = state.relatedToSubject ?: firstSubject,
                    style = MaterialTheme.typography.bodyLarge)

                IconButton(onClick = { isBottomSheetOpen = true}) {

                    Icon(imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.arrow_down_icon)
                    )

                }

            }

            Button(onClick = { onEvents(TaskEvents.SaveTask) },
                enabled = taskTitleError == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)){

                Text(text = "Save")

            }




        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenTopBar(
    title:String,
    isTaskExist:Boolean,
    isComplete:Boolean,
    checkBoxBorderColor:Color,
    onBackClick:() ->Unit,
    onDeleteButtonClick:() ->Unit,
    onCheckBoxClick:() ->Unit,
){

    TopAppBar(title = {

        Text(text = title,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.headlineSmall)
                      },
        navigationIcon = {

            IconButton(onClick = { onBackClick.invoke()}) {

                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.icon_back) )

            }
        },
        actions = {

            if(isTaskExist){

                TaskCheckBox(isCompleted = isComplete,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClicked = onCheckBoxClick)

                IconButton(onClick = { onDeleteButtonClick.invoke()}) {

                    Icon(imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.delete_icon) )

                }


            }




        })

}

@Composable
private fun PriorityButton(
    modifier: Modifier = Modifier,
    label:String,
    backgroundColor: Color,
    borderColor: Color,
    labelColor:Color,
    onClick:() -> Unit

){

    Box(modifier = modifier
        .background(backgroundColor)
        .clickable { onClick.invoke() }
        .padding(5.dp)
        .border(1.dp, borderColor, RoundedCornerShape(5.dp))
        .padding(5.dp),
        contentAlignment = Alignment.Center){

        Text(text = label,
            color = labelColor)

    }



}