package com.labinot.bajrami.study_smart.presentation.screens.task_s

import com.labinot.bajrami.study_smart.presentation.domain.model.Subject
import com.labinot.bajrami.study_smart.presentation.domain.utils.Priority

data class TaskState(

    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val isTaskComplete: Boolean = false,
    val priority: Priority = Priority.LOW,
    val relatedToSubject:String? = null,
    val subjects:List<Subject> = emptyList(),
    val subjectId: Int? = null,
    val currentTaskId:Int? = null

)
