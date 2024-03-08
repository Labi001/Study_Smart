package com.labinot.bajrami.study_smart.presentation.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.labinot.bajrami.study_smart.presentation.domain.model.Session
import com.labinot.bajrami.study_smart.presentation.domain.model.Subject
import com.labinot.bajrami.study_smart.presentation.domain.model.Task

@Database(
    entities = [Subject::class, Session::class, Task::class],
    version = 4,
    exportSchema = false)
@TypeConverters(ColorListConverter::class)
abstract class AppDataBase: RoomDatabase() {

    abstract fun sessionDao():SessionDao

    abstract fun subjectDao():SubjectDao

    abstract fun taskDao():TaskDao
}