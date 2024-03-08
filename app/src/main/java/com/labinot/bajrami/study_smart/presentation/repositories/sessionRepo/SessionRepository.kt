package com.labinot.bajrami.study_smart.presentation.repositories.sessionRepo

import com.labinot.bajrami.study_smart.presentation.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun insertSession(session: Session)

    suspend fun deleteSession(session: Session)

    fun getAllSessions(): Flow<List<Session>>

    fun getRecentFiveSession():Flow<List<Session>>
    fun getRecentTenSessionForSubject(subjectId:Int):Flow<List<Session>>

    fun getTotalSessionDuration():Flow<Long>

    fun getTotalSessionDurationBySubject(subjectId:Int):Flow<Long>

}