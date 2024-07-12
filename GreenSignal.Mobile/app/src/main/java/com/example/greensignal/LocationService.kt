package com.example.greensignal

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.greensignal.domain.model.request.UpdateInspectorLocation
import com.example.greensignal.domain.repository.InspectorRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService: Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var prefs: SharedPreferences

    private var locationCallback: LocationCallback? = null

    @Inject
    lateinit var inspectorRepository: InspectorRepository

    private companion object {
        const val LOCATION_INTERVAL = 600 * 1000L // 600 seconds / 10 minutes
        const val LOCATION_FASTEST_INTERVAL = 300 * 1000L // 300 seconds / 5 minutes
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        requestLocationUpdates(this)
        return START_STICKY
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun requestLocationUpdates(context: Context)  {
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {


                val locationRequest = LocationRequest.create().apply {
                    interval = LOCATION_INTERVAL
                    fastestInterval = LOCATION_FASTEST_INTERVAL
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                locationCallback = object : LocationCallback() {
                    override fun onLocationAvailability(p0: LocationAvailability) {
                        super.onLocationAvailability(p0)
                    }

                    override fun onLocationResult(p0: LocationResult) {
                        super.onLocationResult(p0)

                        p0?.let { result ->
                            val location = result.lastLocation

                            Log.v(
                                "LOCATION",
                                location!!.latitude.toString() + "; " + location.longitude.toString()
                            )

                            Log.v("LOCATION", "token: " + prefs.getString("inspector-jwt-token", "null")!!)

                            // Обработка нового местоположения
                            val token = prefs.getString("inspector-jwt-token", null)

                            if (token != null) {
                                GlobalScope.launch  {
                                    Log.v("LOCATION", "ВЫЗВАЛСЯ LAUNCH")
                                    inspectorRepository.updateInspectorLocation(
                                        token,
                                        UpdateInspectorLocation(
                                            location.latitude,
                                            location.longitude
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback!!,
                    null //Looper.getMainLooper()
                )
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    private fun removeLocationUpdates(){
        locationCallback?.let {
            fusedLocationClient?.removeLocationUpdates(it)
        }
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intet: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.v("LOCATION", "service done")
        super.onDestroy()
        removeLocationUpdates()
    }

}