package com.labinot.bajrami.study_smart.presentation.screens.subject

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.labinot.bajrami.study_smart.R
import com.labinot.bajrami.study_smart.presentation.components.AddSubjectDialog
import com.labinot.bajrami.study_smart.presentation.components.CountCard
import com.labinot.bajrami.study_smart.presentation.components.DeleteDialog
import com.labinot.bajrami.study_smart.presentation.components.studySessionList
import com.labinot.bajrami.study_smart.presentation.components.taskList
import com.labinot.bajrami.study_smart.presentation.domain.utils.SnackbarEvent
import com.labinot.bajrami.study_smart.presentation.screens.task_s.TaskScreenNavArgs
import com.labinot.bajrami.study_smart.presentation.screens.destinations.TaskScreenRouteDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

data class SubjectScreenNavArgs(

    val subjectId: Int

)

@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@Composable
fun SubjectScreenRoute(

    navigator: DestinationsNavigator
){

    val viewModel: SubjectViewModel = hiltViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()

    SubjectScreen(
        states = state,
        onEvent = viewModel::onEvent,
        snackbarEvent = viewModel.snackBarEventFlow,
        onBackButtonClick = { navigator.navigateUp() },
        onAddTaskButtonClick = {

            val navArgs = TaskScreenNavArgs(taskId = null,subjectId = state.currentSubjectId)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArgs))

        },
        onTaskCardClick = {taskId ->

            taskId?.let {

                val navArgs = TaskScreenNavArgs(taskId = taskId,subjectId = null)
                navigator.navigate(TaskScreenRouteDestination(navArgs = navArgs))

            }

        })
}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreen(
     states: SubjectStates,
     onEvent:(SubjectEvent) -> Unit,
     snackbarEvent: SharedFlow<SnackbarEvent>,
    onBackButtonClick:() -> Unit,
    onAddTaskButtonClick:() -> Unit,
    onTaskCardClick:(Int?) -> Unit,

){

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val isFABExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    var isEditSubjectDialogOpen by rememberSaveable {

        mutableStateOf(false)
    }

    var isDeleteSessionDialogOpen by rememberSaveable {

        mutableStateOf(false)
    }

    var isDeleteSubjectDialogOpen by rememberSaveable {

        mutableStateOf(false)
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


    LaunchedEffect(key1 = states.studiedHours , key2 = states.goalStudyHours){


        onEvent(SubjectEvent.UpdateProgress)

    }



    AddSubjectDialog(isOpen = isEditSubjectDialogOpen,
        subjectName = states.subjectName,
        goalHours = states.goalStudyHours,
        onSubjectNameChange = {onEvent(SubjectEvent.OnSubjectNameChange(it))},
        onGoalHoursChange = {onEvent(SubjectEvent.OnGoalStudyHoursChange(it))},
        selectedColors = states.subjectCardColors,
        onColorChange = {onEvent(SubjectEvent.OnSubjectCardColorChange(it))},
        OnDismissRequest = { isEditSubjectDialogOpen = false },
        OnConfirmButtonClick = {
            onEvent(SubjectEvent.UpdateSubject)
            isEditSubjectDialogOpen = false})

    DeleteDialog(isOpen = isDeleteSessionDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure,you want to delete this session? Your studied hours will be reduced " +
                "by this session time. This action can not be undone.",
        OnDismissRequest = { isDeleteSessionDialogOpen = false },
        OnConfirmButtonClick = {
            onEvent(SubjectEvent.DeleteSubject)
            isDeleteSessionDialogOpen = false
        })


    DeleteDialog(isOpen = isDeleteSubjectDialogOpen,
        title = "Delete Subject?",
        bodyText = "Are you sure,you want to delete this subject? All related " +
                "task and study sessions will be permanently removed.This action can not be undone.",
        OnDismissRequest = { isDeleteSubjectDialogOpen= false },
        OnConfirmButtonClick = {
            onEvent(SubjectEvent.DeleteSubject)
            isDeleteSubjectDialogOpen = false

                onBackButtonClick.invoke()

        })

    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackBarHostState)},
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {

            SubjectScreenTopAppBar(title = states.subjectName,
                onBackClick = { onBackButtonClick.invoke() },
                onDeleteClick = { isDeleteSubjectDialogOpen = true },
                onEditClick = {isEditSubjectDialogOpen = true},
                scrollBehavior = scrollBehavior)
        },

        content = { mPadding->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(mPadding),
                state = listState){

                item {

                    SubjectOverViewSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        studiedHours = states.studiedHours.toString(),
                        goalHours = states.goalStudyHours,
                        progress = states.progress
                    )
                }

                taskList(sectionTitle = "UPCOMING TASK",
                    emptyListText = "You don't have any upcoming tasks. \n" +
                            "Click the + button in subject screen to add new task.",
                    tasks = states.upcomingTasks,
                    onCheckBoxClick = {onEvent(SubjectEvent.OnTaskIsCompleteChange(it))},
                    onTaskCardClick = onTaskCardClick
                )

                item {

                    Spacer(modifier = Modifier.height(10.dp))
                }

                taskList(sectionTitle = "COMPLETED TASK",
                    emptyListText = "You don't have any completed tasks. \n" +
                            "Click the check box on completion of task.",
                    tasks = states.completedTasks,
                    onCheckBoxClick = {onEvent(SubjectEvent.OnTaskIsCompleteChange(it))},
                    onTaskCardClick = onTaskCardClick
                )

                item {

                    Spacer(modifier = Modifier.height(10.dp))
                }

                studySessionList(
                    sectionTitle = "RECENT STUDY SESSIONS",
                    emptyListText = "You don't have any recent study session. \n" +
                            "Start a study session to begin recording your process.",
                    sessions = states.recentSessions,
                    onDelIconClick = {
                       onEvent(SubjectEvent.OnDeleteSessionClick(it))
                      isDeleteSessionDialogOpen = true
                    }
                )

            }

        },
        floatingActionButton = {
            
           ExtendedFloatingActionButton(
               onClick = { onAddTaskButtonClick.invoke() },
               icon = {

                   Icon(imageVector = Icons.Default.Add,
                       contentDescription = stringResource(R.string.add_icon) )
               },
               text = { Text(text = "Add Text")},
               expanded = isFABExpanded)
            
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreenTopAppBar(
    title:String,
    onBackClick:() -> Unit,
    onDeleteClick:() -> Unit,
    onEditClick:() -> Unit,
    scrollBehavior: TopAppBarScrollBehavior


){

    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {

                           Text(text = title,
                               maxLines = 1,
                               overflow = TextOverflow.Ellipsis,
                               style = MaterialTheme.typography.headlineSmall)
    },
        navigationIcon = {

            IconButton(onClick = { onBackClick.invoke() }) {

                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.icon_back)
                )

            }

        },

        actions = {

            IconButton(onClick = { onDeleteClick.invoke() }) {

                Icon(imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_icon)
                )

            }

            IconButton(onClick = { onEditClick.invoke() }) {

                Icon(imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_icon)
                )

            }


        })
    
}

@Composable
private fun SubjectOverViewSection(

    modifier: Modifier,
    studiedHours:String,
    goalHours:String,
    progress:Float

){

    val percentageProgress = remember(progress) {

        (progress * 100).toInt().coerceIn(0,100)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(modifier = Modifier.weight(1f),
            headlineText = "Goal Study Hours",
            count = goalHours)

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(modifier = Modifier.weight(1f),
            headlineText = "Studied Hours",
            count = studiedHours)

        Spacer(modifier = Modifier.width(10.dp))


        Box(modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.Center){

            CircularProgressIndicator(

                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant

            )

            CircularProgressIndicator(

                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round

            )
            
            Text(text = "$percentageProgress%")

        }

    }


}