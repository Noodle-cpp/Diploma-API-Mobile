package com.example.greensignal.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.lang.ref.WeakReference

object LocationManager {

    private lateinit var activity: WeakReference<ComponentActivity>
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest

    private var onUpdateLocation: WeakReference<(latitude: Double, longtitude: Double) -> Unit>? = null

    private val locationCallback = object: LocationCallback() {

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)

            if(!locationAvailability.isLocationAvailable) {

                activity.get()?.let {
                    goSettingScreen(it)
                    removeCallback(it)
                }
            }
        }


        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            onUpdateLocation?.get()?.invoke(
                locationResult.lastLocation?.latitude ?: 0.0,
                locationResult.lastLocation?.longitude ?: 0.0,
                )
        }
    }

    object Builder {

        fun build(): Builder {
            return this
        }

        fun create(activity: ComponentActivity): com.example.greensignal.util.LocationManager {

            com.example.greensignal.util.LocationManager.activity = WeakReference(activity)
            locationRequest = com.google.android.gms.location.LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {

                setMinUpdateIntervalMillis(1000)
                setMaxUpdateAgeMillis(0)
                setMinUpdateDistanceMeters(1.0f)
                setGranularity(Granularity.GRANULARITY_FINE)
                setWaitForAccurateLocation(true)

            }.build()

            return LocationManager
        }
    }

    fun goSettingScreen(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1001
        )
//        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        activity.startActivity(intent)
    }

    fun request(onUpdateLocation: ((latitude: Double, longtitude: Double) -> Unit)? = null) {
        com.example.greensignal.util.LocationManager.onUpdateLocation = WeakReference(onUpdateLocation)

        activity.get()?.let {

            if (ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                goSettingScreen(it)
                return
            }
            LocationServices.getFusedLocationProviderClient(it).requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        }
    }

    fun removeCallback(activity: Activity) {
        LocationServices.getFusedLocationProviderClient(activity).flushLocations()
        LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(
            locationCallback
        )
    }
}