package com.labinot.bajrami.study_smart.presentation.screens.session_s

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.labinot.bajrami.study_smart.MainActivity
import com.labinot.bajrami.study_smart.presentation.domain.utils.Constant.CLICK_REQUEST_CODE

object ServiceHelp {

    fun clickPendingIntent(context: Context):PendingIntent{

        val deepLinkIntent = Intent(

            Intent.ACTION_VIEW,
            "study_smart://presentation/screens/session_s/SessionScreen".toUri(),
            context,
            MainActivity::class.java

        )

        return TaskStackBuilder.create(context).run {

            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(

                CLICK_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE


            )

        }

    }

    fun triggerForegroundService(context: Context, action:String) {

        Intent(context, StudySessionTimerService::class.java).apply {

            this.action = action
            context.startService(this)
        }

    }

}