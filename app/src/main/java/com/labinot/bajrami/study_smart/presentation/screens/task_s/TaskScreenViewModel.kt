package com.labinot.bajrami.study_smart.presentation.screens.task_s

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labinot.bajrami.study_smart.presentation.domain.model.Task
import com.labinot.bajrami.study_smart.presentation.domain.utils.Priority
import com.labinot.bajrami.study_smart.presentation.domain.utils.SnackbarEvent
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
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle)
    :ViewModel(){

        private val _state = MutableStateFlow(TaskState())

    private val navArgs: TaskScreenNavArgs = savedStateHandle.navArgs()

    val state = combine(

        _state,
        subjectRepository.getAllSubject()

    ){state,subjects ->

        state.copy(subjects = subjects)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TaskState()
    )


    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackBarEventFlow = _snackbarEventFlow.asSharedFlow()


    init {

        fetchTask()
        fetchSubject()
    }

    fun onEvent(events: TaskEvents){

        when(events){

            TaskEvents.DeleteTask -> deleteTask()
            is TaskEvents.OnDateChange -> {

                _state.update {

                    it.copy(dueDate = events.millis)
                }

            }
            TaskEvents.OnIsCompleteChange -> {

                _state.update {

                    it.copy(isTaskComplete = !_state.value.isTaskComplete)
                }

            }
            is TaskEvents.OnPriorityChange -> {

                _state.update {

                    it.copy(priority = events.priority )

                }

            }

            is TaskEvents.OnRelatedSubjectSelect -> {

                _state.update {

                    it.copy(relatedToSubject = events.subject.name,
                        subjectId = events.subject.subjectId)
                }

            }


            is TaskEvents.OnTitleChange -> {

                _state.update {

                    it.copy(title = events.title)
                }

            }
            is TaskEvents.OnDescriptionChange -> {

                _state.update {

                    it.copy(description = events.description)
                }

            }
            TaskEvents.SaveTask -> saveTask()
        }

    }

    private fun deleteTask() {

        viewModelScope.launch {

            try {

                val currentSubjectId = state.value.currentTaskId

                if(currentSubjectId != null){

                    withContext(Dispatchers.IO){

                        repository.deleteTask(taskId = currentSubjectId)
                    }


                    _snackbarEventFlow.emit(

                        SnackbarEvent.ShowSnackBar(message = "Task Deleted Successfully")
                    )

                    _snackbarEventFlow.emit(SnackbarEvent.NavigationUp)

                }else{

                    _snackbarEventFlow.emit(

                        SnackbarEvent.ShowSnackBar(message = "No Task to Delete")
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

    private fun saveTask() {

        viewModelScope.launch {

            val state = _state.value

            if(state.subjectId == null || state.relatedToSubject == null){

                _snackbarEventFlow.emit(

                    SnackbarEvent.ShowSnackBar(
                        "Please select subject related to task",
                        SnackbarDuration.Long
                    )
                )

                return@launch

            }

            try {

                repository.upsertTask(

                    task = Task(

                        tittle = state.title,
                        description = state.description,
                        dueDate = state.dueDate ?: Instant.now().toEpochMilli(),
                        relatedToSubject = state.relatedToSubject,
                        priority = state.priority.value,
                        isCompleted = state.isTaskComplete,
                        taskSubjectId = state.subjectId,
                        taskId = state.currentTaskId,

                        )

                )

                _snackbarEventFlow.emit(

                    SnackbarEvent.ShowSnackBar(
                        "Task saved Successfully",
                        SnackbarDuration.Long
                    )
                )

                _snackbarEventFlow.emit(SnackbarEvent.NavigationUp)

            }catch (e:Exception){

                _snackbarEventFlow.emit(

                    SnackbarEvent.ShowSnackBar(
                        "Couldn't save task. ${e.message}",
                        SnackbarDuration.Long
                    )
                )

            }



        }



    }

    private fun fetchTask(){

        viewModelScope.launch {

            navArgs.taskId?.let {id ->

                repository.getTaskById(id)?.let {task ->

                    _state.update {

                        it.copy(

                            title = task.tittle,
                            description = task.description,
                            dueDate = task.dueDate,
                            isTaskComplete = task.isCompleted,
                            relatedToSubject = task.relatedToSubject,
                            priority = Priority.fromInt(task.priority),
                            subjectId = task.taskSubjectId,
                            currentTaskId = task.taskId

                        )
                    }

                }


            }

        }


    }

    private fun fetchSubject(){

        viewModelScope.launch {

            navArgs.subjectId?.let { id->

                subjectRepository.getSubjectById(id)?.let { subject->

                    _state.update {

                        it.copy(

                            subjectId = subject.subjectId,
                            relatedToSubject = subject.name

                        )
                    }

                }

            }


        }

    }


}