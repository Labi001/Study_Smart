package com.labinot.bajrami.study_smart.presentation.di

import android.content.Context
import androidx.room.Room
import com.labinot.bajrami.study_smart.presentation.data.AppDataBase
import com.labinot.bajrami.study_smart.presentation.data.ColorListConverter
import com.labinot.bajrami.study_smart.presentation.data.SessionDao
import com.labinot.bajrami.study_smart.presentation.data.SubjectDao
import com.labinot.bajrami.study_smart.presentation.data.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideSessionDao(appDataBase: AppDataBase):SessionDao
      = appDataBase.sessionDao()

    @Provides
    @Singleton
    fun provideSubjectDao(appDataBase: AppDataBase):SubjectDao
            = appDataBase.subjectDao()

    @Provides
    @Singleton
    fun provideTaskDao(appDataBase: AppDataBase):TaskDao
            = appDataBase.taskDao()

    @Provides
    @Singleton
    fun provideAppDataBase(@ApplicationContext context: Context) :AppDataBase
     = Room.databaseBuilder(

         context,
         AppDataBase::class.java,
         "app_database"

     )
        .fallbackToDestructiveMigration()
        .build()


}