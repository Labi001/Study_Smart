package com.labinot.bajrami.study_smart.presentation.repositories.subjectRepo

import com.labinot.bajrami.study_smart.presentation.data.SessionDao
import com.labinot.bajrami.study_smart.presentation.data.SubjectDao
import com.labinot.bajrami.study_smart.presentation.data.TaskDao
import com.labinot.bajrami.study_smart.presentation.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
   private val  subjectDao: SubjectDao,
   private val  taskDao: TaskDao,
   private val  sessionDao: SessionDao
): SubjectRepository {

    override suspend fun upsertSubject(subject: Subject) {

        subjectDao.upsertSubject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int> {

        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {

        return subjectDao.getTotalGoalHours()
    }

    override suspend fun deleteSubject(subjectId: Int) {

        subjectDao.deleteSubject(subjectId)
       sessionDao.deleteSessionBySubjectId(subjectId)
        taskDao.deleteTaskBySubjectId(subjectId)

    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {


        return subjectDao.getSubjectById(subjectId)
    }

    override fun getAllSubject(): Flow<List<Subject>> {

        return subjectDao.getAllSubjects()
    }
}