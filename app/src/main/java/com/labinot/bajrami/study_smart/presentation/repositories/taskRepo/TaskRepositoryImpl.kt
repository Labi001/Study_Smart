package com.labinot.bajrami.study_smart.presentation.repositories.taskRepo

import com.labinot.bajrami.study_smart.presentation.data.TaskDao
import com.labinot.bajrami.study_smart.presentation.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
):TaskRepository {

    override suspend fun upsertTask(task: Task) {
       taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {

        taskDao.deleteTask(taskId)
    }

    override suspend fun getTaskById(taskId: Int): Task? {

        return taskDao.getTaskById(taskId)
    }

    override fun getUpcomingTasksForSubject(subjectInt: Int): Flow<List<Task>> {

        return taskDao.getTaskForSubject(subjectInt)
            .map {tasks -> tasks.filter { it.isCompleted.not() } }
            .map { tasks -> sortTasks(tasks) }
    }

    override fun getCompletedTasksForSubject(subjectInt: Int): Flow<List<Task>> {

        return taskDao.getTaskForSubject(subjectInt)
            .map {tasks -> tasks.filter { it.isCompleted} }
            .map { tasks -> sortTasks(tasks) }


    }

    override fun getAllUpcomingTask(): Flow<List<Task>> {

        return taskDao.getAllTasks()
            .map { tasks -> tasks.filter { it.isCompleted.not() } }
            .map { tasks -> sortTasks(tasks) }
    }

    private fun sortTasks(tasks: List<Task>): List<Task> {

        return tasks.sortedWith(compareBy<Task>{it.dueDate}.thenByDescending { it.priority })
    }
}