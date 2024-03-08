package com.labinot.bajrami.study_smart.presentation.screens.session_s

import com.labinot.bajrami.study_smart.presentation.domain.model.Session
import com.labinot.bajrami.study_smart.presentation.domain.model.Subject

sealed class SessionEvent{

    data class OnRelatedSubjectChange(val subject: Subject) : SessionEvent()

    data class SaveSession(val duration: Long) : SessionEvent()

    data class OnDeleteSessionButtonClick(val session: Session) : SessionEvent()

    data object DeleteSession : SessionEvent()

    data object NotifyToUpdateSubject : SessionEvent()

    data class UpdateSubjectIdAndRelatedSubject (

        val subjectId: Int?,
        val relatedToSubject: String?


    ): SessionEvent()

}
