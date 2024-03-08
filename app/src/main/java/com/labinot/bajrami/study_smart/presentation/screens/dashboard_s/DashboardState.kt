package com.labinot.bajrami.study_smart.presentation.screens.dashboard_s

import androidx.compose.ui.graphics.Color
import com.labinot.bajrami.study_smart.presentation.domain.model.Session
import com.labinot.bajrami.study_smart.presentation.domain.model.Subject

data class DashboardState(

    val totalSubjectCount: Int = 0,
    val totalStudyHours: Float = 0f,
    val totalGoalStudyHours: Float = 0f,
    val subjects: List<Subject> = emptyList(),
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val subjectCardColors:List<Color> = Subject.subjectCardColor.random(),
    val session: Session? = null

)
