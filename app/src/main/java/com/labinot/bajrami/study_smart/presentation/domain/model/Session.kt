package com.labinot.bajrami.study_smart.presentation.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Session(

    @PrimaryKey(autoGenerate = true)
    val sessionSubjectId:Int? = null,

    val relatedToSubject:String,
    val date:Long,
    val duration:Long,
    val sessionId:Int? = null,


)
