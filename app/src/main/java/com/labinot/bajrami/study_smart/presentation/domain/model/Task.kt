package com.labinot.bajrami.study_smart.presentation.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(

    val tittle:String,
    val description:String,
    val dueDate:Long,
    val priority:Int,
    val relatedToSubject:String,
    val isCompleted:Boolean,
    val taskSubjectId:Int,

    @PrimaryKey(autoGenerate = true)
    val taskId:Int? = null,


)
