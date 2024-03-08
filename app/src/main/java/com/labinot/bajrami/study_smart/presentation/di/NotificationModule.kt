package com.labinot.bajrami.study_smart.presentation.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.labinot.bajrami.study_smart.R
import com.labinot.bajrami.study_smart.presentation.domain.utils.Constant
import com.labinot.bajrami.study_smart.presentation.domain.utils.Constant.NOTIFICATION_CHANNEL_ID
import com.labinot.bajrami.study_smart.presentation.screens.session_s.ServiceHelp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @ServiceScoped
    @Provides
    fun provideNotificationModule(

        @ApplicationContext context: Context

    ) :NotificationCompat.Builder {

        return NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Study Session")
            .setContentText("00:00:00")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(ServiceHelp.clickPendingIntent(context))
    }


    @ServiceScoped
    @Provides
    fun provideNotificationManager (

             @ApplicationContext context: Context
    ): NotificationManager {

        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

}