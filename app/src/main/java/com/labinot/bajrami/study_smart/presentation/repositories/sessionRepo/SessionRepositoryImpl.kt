package com.labinot.bajrami.study_smart.presentation.repositories.sessionRepo

import com.labinot.bajrami.study_smart.presentation.data.SessionDao
import com.labinot.bajrami.study_smart.presentation.domain.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(

    private val sessionDao: SessionDao
):SessionRepository {

    override suspend fun insertSession(session: Session) {
       sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
       sessionDao.deleteSession(session)
    }

    override fun getAllSessions(): Flow<List<Session>> {

        return sessionDao.getAllSession().map { session -> session.sortedByDescending { it.date }


        }
    }

    override fun getRecentFiveSession(): Flow<List<Session>> {
        return sessionDao.getAllSession()
            .map { session -> session.sortedByDescending { it.date }}
            .take(count = 5)
    }

    override fun getRecentTenSessionForSubject(subjectId: Int): Flow<List<Session>> {

        return sessionDao.getRecentSessionsForSubject(subjectId).take(count = 10)
    }

    override fun getTotalSessionDuration(): Flow<Long> {

        return sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSessionDurationBySubject(subjectId: Int): Flow<Long> {

        return sessionDao.getTotalSessionDurationBySubject(subjectId)

    }
}