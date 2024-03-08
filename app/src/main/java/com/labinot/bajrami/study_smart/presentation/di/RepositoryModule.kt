package com.labinot.bajrami.study_smart.presentation.di

import com.labinot.bajrami.study_smart.presentation.repositories.sessionRepo.SessionRepository
import com.labinot.bajrami.study_smart.presentation.repositories.sessionRepo.SessionRepositoryImpl
import com.labinot.bajrami.study_smart.presentation.repositories.subjectRepo.SubjectRepository
import com.labinot.bajrami.study_smart.presentation.repositories.subjectRepo.SubjectRepositoryImpl
import com.labinot.bajrami.study_smart.presentation.repositories.taskRepo.TaskRepository
import com.labinot.bajrami.study_smart.presentation.repositories.taskRepo.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Singleton
    @Binds
    abstract fun bindSubjectRepository(
        impl: SubjectRepositoryImpl
    ):SubjectRepository

    @Singleton
    @Binds
    abstract fun bindSessionRepository(
        impl:SessionRepositoryImpl
    ):SessionRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ):TaskRepository


}