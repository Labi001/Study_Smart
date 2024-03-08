package com.labinot.bajrami.study_smart.presentation.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.labinot.bajrami.study_smart.presentation.domain.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Upsert
    suspend fun upsertTask(task: Task)

    @Query("DELETE FROM Task WHERE taskID = :taskId")
    suspend fun deleteTask(taskId:Int)

    @Query("DELETE FROM Task WHERE taskSubjectId = :subjectId")
    suspend fun deleteTaskBySubjectId(subjectId:Int)

    @Query("SELECT * FROM Task WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("SELECT * FROM Task WHERE taskSubjectId = :subjectId")
     fun getTaskForSubject(subjectId: Int): Flow<List<Task>>

    @Query("SELECT * FROM Task")
    fun getAllTasks(): Flow<List<Task>>


}