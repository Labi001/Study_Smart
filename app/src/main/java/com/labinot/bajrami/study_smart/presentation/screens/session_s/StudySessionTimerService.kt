package com.labinot.bajrami.study_smart.presentation.screens.session_s

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.labinot.bajrami.study_smart.presentation.domain.utils.Constant.ACTION_SERVICE_CANCEL
import com.labinot.bajrami.study_smart.presentation.domain.utils.Constant.ACTION_SERVICE_START
import com.labinot.bajrami.study_smart.presentation.domain.utils.Constant.ACTION_SERVICE_STOP
import com.labinot.bajrami.study_smart.presentation.domain.utils.Constant.NOTIFICATION_CHANNEL_ID
import com.labinot.bajrami.study_smart.presentation.domain.utils.Constant.NOTIFICATION_CHANNEL_NAME
import com.labinot.bajrami.study_smart.presentation.domain.utils.Constant.NOTIFICATION_ID
import com.labinot.bajrami.study_smart.presentation.domain.utils.pad
import dagger.hilt.android.AndroidEntryPoint
import kotlin.time.Duration
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration.Companion.seconds


@AndroidEntryPoint
class StudySessionTimerService: Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = StudySessionTimerBinder()

    private lateinit var timer: Timer

    var duration: Duration = Duration.ZERO

    var seconds = mutableStateOf("00")
        private set

    var minutes = mutableStateOf("00")
        private set

    var hours = mutableStateOf("00")
        private set

    var currentTimeState = mutableStateOf(TimerState.IDLE)
        private set

    var subjectId = mutableStateOf<Int?>(null)

    override fun onBind(p0: Intent?) = binder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.action.let {

            when(it){

                ACTION_SERVICE_START ->{
                    startForegroundService()
                    startTimer{ hours,minutes,seconds ->

                       updateNotification(hours, minutes, seconds)
                    }

                }

                ACTION_SERVICE_STOP ->{

                    stopTimer()

                }

                ACTION_SERVICE_CANCEL -> {

                    stopTimer()
                    cancelTimer()
                    stopForegroundService()


                }

            }

        }

        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundService() {

        createNotificationChannel()
        startForeground(NOTIFICATION_ID,notificationBuilder.build())


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {

        val channel = NotificationChannel(

            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW

        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification(hours:String,minutes:String,seconds:String){

        notificationManager.notify(

            NOTIFICATION_ID,
            notificationBuilder.setContentText("$hours:$minutes:$seconds")
                .build()

        )

    }

    private fun stopForegroundService(){

        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

    }

    private fun stopTimer(){

        if(this::timer.isInitialized) {

            timer.cancel()
        }

        currentTimeState.value = TimerState.STOP

    }

    private fun cancelTimer(){

        duration = Duration.ZERO
        updateTimeUnits()
        currentTimeState.value = TimerState.IDLE
    }

    private fun startTimer(
        onTick:(h:String,m:String,s:String) -> Unit
    ){

       currentTimeState.value = TimerState.STARTED
        timer = fixedRateTimer(initialDelay = 1000L,
                               period = 1000L){

            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours.value,minutes.value,seconds.value)
        }

    }


    private fun updateTimeUnits(){

        duration.toComponents{ hours, minutes, seconds, _ ->

            this@StudySessionTimerService.hours.value = hours.toInt().pad()
            this@StudySessionTimerService.minutes.value = minutes.pad()
            this@StudySessionTimerService.seconds.value = seconds.pad()

            
        }
    }

    inner class StudySessionTimerBinder : Binder() {

        fun getService(): StudySessionTimerService = this@StudySessionTimerService

    }

}

enum class TimerState {

    IDLE,
    STARTED,
    STOP

}