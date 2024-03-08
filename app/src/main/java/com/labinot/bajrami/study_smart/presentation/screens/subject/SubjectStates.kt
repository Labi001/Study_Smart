package com.labinot.bajrami.study_smart.presentation.screens.subject

import androidx.compose.ui.graphics.Color
import com.labinot.bajrami.study_smart.presentation.domain.model.Session
import com.labinot.bajrami.study_smart.presentation.domain.model.Subject
import com.labinot.bajrami.study_smart.presentation.domain.model.Task

data class SubjectStates(

    val currentSubjectId: Int? = null,
    val subjectName:String = "",
    val goalStudyHours:String = "",
    val subjectCardColors: List<Color> = Subject.subjectCardColor.random(),
    val studiedHours:Float = 0f,
    val progress:Float = 0f,
    val recentSessions: List<Session> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val session: Session? = null,

)
