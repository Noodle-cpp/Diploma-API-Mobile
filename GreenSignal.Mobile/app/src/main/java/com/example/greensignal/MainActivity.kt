package com.example.greensignal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import coil.request.Disposable
import com.example.greensignal.data.provider.SharedPrefsProvider
import com.example.greensignal.presentation.ui.dialog.PermissionDialog
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPrefsProvider.initialize(applicationContext)

        val context: Context = this
        requestNotificationPermission()

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                GreenSignalTheme {
                    navController = rememberNavController()
                    val permissions = listOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.FOREGROUND_SERVICE,
                    )

                    PermissionDialog(
                        permissions = permissions,
                        description = "Чтобы использовать приложение, пожалуйста, дайте доступ к вашему местоположению",
                        requiredPermissions = listOf(permissions.first()),
                        navController = navController,
                        onGranted = {
                            com.example.greensignal.presentation.ui.navigation.Navigation(
                                navController = navController
                            )

                            val serviceIntent = Intent(context, LocationService::class.java)
                            startService(serviceIntent)
                        },
                    )
                }
            }
        }
    }


    private fun requestNotificationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if(!hasPermission) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }
}



