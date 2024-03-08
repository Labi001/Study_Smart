package com.labinot.bajrami.study_smart

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.labinot.bajrami.study_smart.presentation.screens.NavGraphs
import com.labinot.bajrami.study_smart.presentation.screens.destinations.SessionScreenRouteDestination
import com.labinot.bajrami.study_smart.presentation.screens.session_s.StudySessionTimerService
import com.labinot.bajrami.study_smart.ui.theme.Study_SmartTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isBound by mutableStateOf(false)
    private lateinit var timmerService: StudySessionTimerService

    private val connection = object :ServiceConnection {

        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {

            val binder = service as StudySessionTimerService.StudySessionTimerBinder
            timmerService = binder.getService()
            isBound = true

        }

        override fun onServiceDisconnected(p0: ComponentName?) {

            isBound = false

        }


    }

    override fun onStart() {
        super.onStart()
        Intent(this,StudySessionTimerService::class.java).also { intent ->

            bindService(intent,connection,Context.BIND_AUTO_CREATE)


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window,false)
        installSplashScreen()
        setContent {

            if(isBound){

                Study_SmartTheme {
                    // A surface container using the 'background' color from the theme

                    DestinationsNavHost(navGraph = NavGraphs.root,
                        dependenciesContainerBuilder = {

                            dependency(SessionScreenRouteDestination) {

                                timmerService

                            }
                        })

                }

            }

        }
        requestPermission()
    }

    private fun requestPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )

        }

    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }

}


