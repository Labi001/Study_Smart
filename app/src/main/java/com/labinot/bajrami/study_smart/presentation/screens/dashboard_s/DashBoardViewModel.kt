package com.labinot.bajrami.study_smart.presentation.screens.dashboard_s

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labinot.bajrami.study_smart.presentation.domain.model.Session
import com.labinot.bajrami.study_smart.presentation.domain.model.Subject
import com.labinot.bajrami.study_smart.presentation.domain.model.Task
import com.labinot.bajrami.study_smart.presentation.domain.utils.SnackbarEvent
import com.labinot.bajrami.study_smart.presentation.domain.utils.toHours
import com.labinot.bajrami.study_smart.presentation.repositories.sessionRepo.SessionRepository
import com.labinot.bajrami.study_smart.presentation.repositories.subjectRepo.SubjectRepository
import com.labinot.bajrami.study_smart.presentation.repositories.taskRepo.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor (
    private val repository:SubjectRepository,
    private val sessionRepository:SessionRepository,
    private val taskRepository: TaskRepository)
    :ViewModel(){


        private val _state = MutableStateFlow(DashboardState())
        val state = combine(

            _state,
            repository.getTotalSubjectCount(),
            repository.getTotalGoalHours(),
            repository.getAllSubject(),
            sessionRepository.getTotalSessionDuration()


        ){ _state, subjectCount,goalHours, subjects, totalSessionDuration ->
            _state.copy(

                totalSubjectCount = subjectCount,
                totalGoalStudyHours = goalHours,
                subjects = subjects,
                totalStudyHours = totalSessionDuration.toHours()

            )

        }.stateIn(

            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = DashboardState()
        )

    val task: StateFlow<List<Task>> = taskRepository.getAllUpcomingTask()
        .stateIn(

            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentSession: StateFlow<List<Session>> = sessionRepository.getRecentFiveSession()
        .stateIn(

            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackBarEventFlow = _snackbarEventFlow.asSharedFlow()

    fun onEvent(event: DashboardEvent) {

        when(event){
            DashboardEvent.DeleteSession -> TODO()
            is DashboardEvent.OnDeleteSessionButtonClick -> {

                _state.update {

                    it.copy(session = event.session)
                }

            }


            is DashboardEvent.OnGoalStudyHoursChange -> {

                _state.update {

                    it.copy(goalStudyHours = event.hours)
                }

            }


            is DashboardEvent.OnSubjectCardColorChange -> {

                _state.update {

                    it.copy(subjectCardColors = event.colors)
                }

            }

            is DashboardEvent.OnSubjectNameChange -> {

                _state.update {

                    it.copy(subjectName = event.name)
                }
            }

            is DashboardEvent.OnTaskIsCompleteChange -> {

                updateTask(event.task)
            }
            DashboardEvent.SaveSubject -> saveSubject()
            DashboardEvent.DeleteSession -> deleteSession()
            is DashboardEvent.OnDeleteSessionButtonClick -> TODO()
            is DashboardEvent.OnGoalStudyHoursChange -> TODO()
            is DashboardEvent.OnSubjectCardColorChange -> TODO()
            is DashboardEvent.OnSubjectNameChange -> TODO()
            is DashboardEvent.OnTaskIsCompleteChange -> TODO()
            DashboardEvent.SaveSubject -> TODO()
        }

    }

    private fun updateTask(task: Task) {

        viewModelScope.launch {

            try {

             taskRepository.upsertTask(

                 task = task.copy(isCompleted = !task.isCompleted)

             )

                _snackbarEventFlow.emit(

                    SnackbarEvent.ShowSnackBar(
                        "Saved in Completed task"
                    )
                )

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

    private fun saveSubject() {

        viewModelScope.launch {


            try {

                repository.upsertSubject(

                    subject = Subject(

                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() },
                    )
                )

                _state.update {

                    it.copy(
                        subjectName = "",
                        goalStudyHours = "",
                        subjectCardColors = Subject.subjectCardColor.random()
                    )

                }

                _snackbarEventFlow.emit(

                    SnackbarEvent.ShowSnackBar(
                        "Subject saved successfully"
                    )
                )

            }catch (e:Exception){

                _snackbarEventFlow.emit(

                    SnackbarEvent.ShowSnackBar(
                        "Couldn't save subject. ${e.message}",
                        SnackbarDuration.Long
                    )
                )

            }

        }

    }




}