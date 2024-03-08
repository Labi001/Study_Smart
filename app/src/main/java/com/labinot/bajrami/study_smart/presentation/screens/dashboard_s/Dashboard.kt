package com.labinot.bajrami.study_smart.presentation.screens.dashboard_s

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.labinot.bajrami.study_smart.R
import com.labinot.bajrami.study_smart.presentation.components.AddSubjectDialog
import com.labinot.bajrami.study_smart.presentation.components.CountCard
import com.labinot.bajrami.study_smart.presentation.components.DeleteDialog
import com.labinot.bajrami.study_smart.presentation.components.SubjectCard
import com.labinot.bajrami.study_smart.presentation.components.studySessionList
import com.labinot.bajrami.study_smart.presentation.components.taskList
import com.labinot.bajrami.study_smart.presentation.domain.model.Session
import com.labinot.bajrami.study_smart.presentation.domain.model.Subject
import com.labinot.bajrami.study_smart.presentation.domain.model.Task
import com.labinot.bajrami.study_smart.presentation.domain.utils.SnackbarEvent
import com.labinot.bajrami.study_smart.presentation.screens.subject.SubjectScreenNavArgs
import com.labinot.bajrami.study_smart.presentation.screens.task_s.TaskScreenNavArgs
import com.labinot.bajrami.study_smart.presentation.screens.destinations.SessionScreenRouteDestination
import com.labinot.bajrami.study_smart.presentation.screens.destinations.SubjectScreenRouteDestination
import com.labinot.bajrami.study_smart.presentation.screens.destinations.TaskScreenRouteDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph(start = true)
@Destination()
@Composable
fun DashboardScreenRoute(

    navigator:DestinationsNavigator
){

    val viewModel:DashBoardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks by viewModel.task.collectAsStateWithLifecycle()
    val recentSession by viewModel.recentSession.collectAsStateWithLifecycle()

   DashboardScreen(
       state = state,
       tasks = tasks,
       recentSessions = recentSession,
       onEvent = viewModel::onEvent,
       snackbarEvent = viewModel.snackBarEventFlow,
       onSubjectCardClick = {subjectId ->

                                        subjectId?.let {

                                            val navArgs = SubjectScreenNavArgs(subjectId = subjectId)
                                            navigator.navigate(SubjectScreenRouteDestination(navArgs = navArgs))
                                        }

   },
       onTaskCardClick = {taskId ->

                taskId?.let {

                    val navArgs = TaskScreenNavArgs(taskId = taskId,subjectId = null)
                    navigator.navigate(TaskScreenRouteDestination(navArgs = navArgs))

                }


       },
       onStartSessionButtonClick = {

           navigator.navigate(SessionScreenRouteDestination())
       })
}

@Composable
private fun DashboardScreen(
    state: DashboardState,
    tasks: List<Task>,
    recentSessions:List<Session>,
    onEvent:(DashboardEvent) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onSubjectCardClick: (Int?) -> Unit,
    onTaskCardClick: (Int?) -> Unit,
    onStartSessionButtonClick: () -> Unit,

){

    var isAddSubjectDialogOpen by rememberSaveable {

        mutableStateOf(false)
    }

    var isDeleteDialogOpen by rememberSaveable {

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


                }
            }

        }

    }

//    var subjectName by rememberSaveable { mutableStateOf("") }
//    var goalHours by rememberSaveable { mutableStateOf("") }
//    var selectedColor by remember { mutableStateOf(Subject.subjectCardColor.random()) }


    
    AddSubjectDialog(isOpen = isAddSubjectDialogOpen,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange = {onEvent(DashboardEvent.OnSubjectNameChange(it))},
        onGoalHoursChange = {onEvent(DashboardEvent.OnGoalStudyHoursChange(it))},
        selectedColors = state.subjectCardColors,
        onColorChange = {onEvent(DashboardEvent.OnSubjectCardColorChange(it))},
        OnDismissRequest = { isAddSubjectDialogOpen = false },
        OnConfirmButtonClick = {
            onEvent(DashboardEvent.SaveSubject)
            isAddSubjectDialogOpen = false})


    DeleteDialog(isOpen = isDeleteDialogOpen,
        title = "Delete Session?",
        bodyText = "Are you sure,you want to delete this session? Your studied hours will be reduced " +
        "by this session time. This action can not be undone.",
        OnDismissRequest = { isDeleteDialogOpen = false },
        OnConfirmButtonClick = {
            onEvent(DashboardEvent.DeleteSession)
            isDeleteDialogOpen = false})

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState)},
        topBar = {
        
       DashboardScreenTopAppBar()
        
    }) { mPadding ->

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(mPadding)){

            item {

                CountCardsSection(modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    studiedHours = state.totalStudyHours.toString(),
                    goalHours = state.totalGoalStudyHours.toString())

            }
            
            item { 
                
                SubjectCardSection(modifier = Modifier.fillMaxWidth(),
                    subjectList = state.subjects,
                    onAddIconClicked = {

                        isAddSubjectDialogOpen = true
                    },
                    onSubjectCardClick = onSubjectCardClick)
            }

            item {

                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 20.dp),
                    onClick = { onStartSessionButtonClick.invoke() }) {
                    
                    Text(text = "Start Study Session")

                }
                
            }

            taskList(sectionTitle = "UPCOMING TASK",
                emptyListText = "You don't have any upcoming tasks. \n" +
                "Click the + button in subject screen to add new task.",
                tasks = tasks,
                onCheckBoxClick = {onEvent(DashboardEvent.OnTaskIsCompleteChange(it))},
                onTaskCardClick = onTaskCardClick
            )
            
            item { 
                
                Spacer(modifier = Modifier.height(10.dp))
            }

            studySessionList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study session. \n" +
                        "Start a study session to begin recording your process.",
                sessions = recentSessions,
                onDelIconClick = {
                    onEvent(DashboardEvent.OnDeleteSessionButtonClick(it))
                    isDeleteDialogOpen = true
                }
            )




        }

    }
    
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenTopAppBar(){

    CenterAlignedTopAppBar(
        title = { 
            
            Text(text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium)
            
        })
    
}

@Composable
private fun CountCardsSection(

    modifier: Modifier,
    subjectCount:Int,
    studiedHours:String,
    goalHours:String,

){

    Row(modifier = modifier){

        CountCard(
            modifier = Modifier.weight(1f),
            headlineText = "Subject Count",
            count = "$subjectCount")

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(
            modifier = Modifier.weight(1f),
            headlineText = "Study Hours",
            count = studiedHours)

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(
            modifier = Modifier.weight(1f),
            headlineText = "Goal Study Hours",
            count = goalHours)



    }

}

@Composable
private fun SubjectCardSection(
    modifier: Modifier,
    subjectList:List<Subject>,
    emptyListText:String = "You don't have any subject. \n Click the + button to add new Subject",
    onAddIconClicked:() -> Unit,
    onSubjectCardClick: (Int?) -> Unit
){

    Column(modifier = modifier) {

        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {

            Text(text = "SUBJECT",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp))

            IconButton(onClick = { onAddIconClicked.invoke() }) {

                Icon(imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_subject)
                )

            }


        }

        if(subjectList.isEmpty()){

            Image(modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.books),
                contentDescription = stringResource(R.string.empty_books)
            )

            Text(modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center)
            
        }else {

            LazyRow(

                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
            ){

                items(subjectList){ subject->

                    SubjectCard(
                        subjectName = subject.name,
                        gradientColors = subject.colors.map {
                                                            Color(it)
                        },
                        onClick = {

                            onSubjectCardClick.invoke(subject.subjectId)
                        }
                        )
                }

            }

        }

    }


}