package com.labinot.bajrami.study_smart.presentation.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.labinot.bajrami.study_smart.presentation.domain.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query("SELECT * FROM Subject")
    fun getAllSubjects(): Flow<List<Subject>>

    @Upsert
    suspend fun upsertSubject(subject: Subject)

    @Query("SELECT COUNT(*) FROM SUBJECT")
    fun getTotalSubjectCount(): Flow<Int>

    @Query("SELECT SUM(goalHours) FROM SUBJECT")
    fun getTotalGoalHours(): Flow<Float>

    @Query("SELECT * FROM Subject WHERE subjectId = :subjectId")
   suspend fun getSubjectById(subjectId: Int): Subject?

    @Query("DELETE FROM Subject WHERE subjectId = :subjectId")
    suspend fun deleteSubject(subjectId: Int)

}