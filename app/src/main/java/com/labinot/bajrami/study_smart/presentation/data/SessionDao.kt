package com.labinot.bajrami.study_smart.presentation.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.labinot.bajrami.study_smart.presentation.domain.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

   // @Insert(onConflict = OnConflictStrategy.IGNORE)
    @Upsert
    suspend fun insertSession(session: Session)


    @Delete
    suspend fun deleteSession(session: Session)

    @Query("SELECT * FROM Session")
    fun getAllSession(): Flow<List<Session>>


    @Query("SELECT * FROM Session WHERE sessionSubjectId = :subjectId")
    fun getRecentSessionsForSubject(subjectId: Int): Flow<List<Session>>


    @Query("SELECT SUM(duration) FROM Session")
    fun getTotalSessionDuration(): Flow<Long>

    @Query("SELECT SUM(duration) FROM Session WHERE sessionSubjectId = :subjectId")
    fun getTotalSessionDurationBySubject(subjectId: Int): Flow<Long>

    @Query("DELETE FROM Session WHERE sessionSubjectId = :subjectId")
    fun deleteSessionBySubjectId(subjectId: Int)


}