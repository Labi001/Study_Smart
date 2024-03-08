package com.labinot.bajrami.study_smart.presentation.screens.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labinot.bajrami.study_smart.presentation.domain.model.Subject
import com.labinot.bajrami.study_smart.presentation.domain.model.Task
import com.labinot.bajrami.study_smart.presentation.domain.utils.SnackbarEvent
import com.labinot.bajrami.study_smart.presentation.domain.utils.toHours
import com.labinot.bajrami.study_smart.presentation.repositories.sessionRepo.SessionRepository
import com.labinot.bajrami.study_smart.presentation.repositories.subjectRepo.SubjectRepository
import com.labinot.bajrami.study_smart.presentation.repositories.taskRepo.TaskRepository
import com.labinot.bajrami.study_smart.presentation.screens.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val repository: SubjectRepository,
    private val taskRepository:TaskRepository,
    private val sessionRepository:SessionRepository,
    savedStateHandle: SavedStateHandle)
    :ViewModel(){

        private val navArgs: SubjectScreenNavArgs = savedStateHandle.navArgs()



        private val _state = MutableStateFlow(SubjectStates())
       val state = combine(

           _state,
           taskRepository.getUpcomingTasksForSubject(navArgs.subjectId),
           taskRepository.getCompletedTasksForSubject(navArgs.subjectId),
           sessionRepository.getRecentTenSessionForSubject(navArgs.subjectId),
           sessionRepository.getTotalSessionDurationBySubject(navArgs.subjectId)

       ){ state, upcomingTasks,completeTasks,recentSessions,tottalSessionsDuration ->

           state.copy(

               upcomingTasks = upcomingTasks,
               completedTasks = completeTasks,
               recentSessions = recentSessions,
               studiedHours = tottalSessionsDuration.toHours()

           )


       }.stateIn(

           scope = viewModelScope,
           started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
           initialValue = SubjectStates()

       )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackBarEventFlow = _snackbarEventFlow.asSharedFlow()

    init {
        fetchSubject()
    }

    fun onEvent(event: SubjectEvent){

        when(event){

            SubjectEvent.DeleteSession -> deleteSession()
            SubjectEvent.DeleteSubject ->  deleteSubject()

            is SubjectEvent.OnDeleteSessionClick -> {

                _state.update {

                    it.copy(session = event.session)
                }

            }


            is SubjectEvent.OnGoalStudyHoursChange -> {

                _state.update {

                    it.copy(goalStudyHours = event.hours)
                }

            }


            is SubjectEvent.OnSubjectCardColorChange -> {

                _state.update {

                    it.copy(subjectCardColors = event.color)
                }


            }
            is SubjectEvent.OnSubjectNameChange -> {

                _state.update {

                    it.copy(subjectName = event.name)
                }

            }
            is SubjectEvent.OnTaskIsCompleteChange -> {

                updateTask(event.task)
            }
            SubjectEvent.UpdateSubject -> updateSubject()
            SubjectEvent.UpdateProgress -> {

                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?:1f

                _state.update {

                    it.copy(

                        progress = (state.value.studiedHours / goalStudyHours).coerceIn(0f,1f)
                    )

                }

            }
        }


    }

    private fun deleteSession(){

        viewModelScope.launch {

            try {

                state.value.session?.let {

                    sessionRepository.deleteSession(it)
                    _snackbarEventFlow.emit(

                        SnackbarEvent.ShowSnackBar(
                            "Session deleted successfully",

                            )
                    )

                }


            }catch (e:Exception) {

                _snackbarEventFlow.emit(

                    SnackbarEvent.ShowSnackBar(
                        "Couldn't delete session. ${e.message}",
                        SnackbarDuration.Long
                    )
                )

            }

        }

    }

    private fun updateSubject() {

        viewModelScope.launch {

            try {

                repository.upsertSubject(

                    subject = Subject(

                        subjectId = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }

                    )

                )

                _snackbarEventFlow.emit(

                    SnackbarEvent.ShowSnackBar(message = "Subject Updated Successfully")
                )

            } catch (e: Exception){

                _snackbarEventFlow.emit(

                    SnackbarEvent.ShowSnackBar(message = "Couldn't update subject. ${e.message}",
                        SnackbarDuration.Long)

                )

            }

        }






    }

    private fun fetchSubject(){

        viewModelScope.launch {

            repository
                .getSubjectById(navArgs.subjectId)?.let { subject->

                    _state.update {

                        it.copy(

                            subjectName = subject.name,
                            goalStudyHours = subject.goalHours.toString(),
                            subjectCardColors = subject.colors.map { Color(it)},
                            currentSubjectId = subject.subjectId

                        )
                    }



                }

        }

    }


    private fun deleteSubject(){

        viewModelScope.launch {


            try {

                val currentSubjectId = state.value.currentSubjectId

                if(currentSubjectId != null){

                    withContext(Dispatchers.IO){

                        repository.deleteSubject(subjectId = currentSubjectId)
                    }


                    _snackbarEventFlow.emit(

                        SnackbarEvent.ShowSnackBar(message = "Subject Deleted Successfully")
                    )

                    _snackbarEventFlow.emit(SnackbarEvent.NavigationUp)

                }else{

                    _snackbarEventFlow.emit(

                        SnackbarEvent.ShowSnackBar(message = "No Subject to Delete")
                    )

                }


            } catch (e:Exception){

                _snackbarEventFlow.emit(

                    SnackbarEvent.ShowSnackBar(message = "Couldn't delete subject. ${e.message}",
                        duration = SnackbarDuration.Long)
                )

            }



        }

    }

    private fun updateTask(task: Task) {

        viewModelScope.launch {

            try {

                taskRepository.upsertTask(

                    task = task.copy(isCompleted = !task.isCompleted)

                )

                if(task.isCompleted) {

                    _snackbarEventFlow.emit(

                        SnackbarEvent.ShowSnackBar(
                            "Saved in upcoming task"
                        )
                    )

                }else {


                    SnackbarEvent.ShowSnackBar(
                        "Saved in Completed task"
                    )

                }



            }catch (e:Exception){

                _snackbarEventFlow.emit(

                    SnackbarEvent.ShowSnackBar(
                        "Couldn't update task. ${e.message}",
                        SnackbarDuration.Long
                    )
                )

            }


        }


    }

}