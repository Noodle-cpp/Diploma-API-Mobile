package com.example.greensignal.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSharedPreferences = staticCompositionLocalOf<SharedPreferences?> { null }

class SharedPrefsProvider private constructor() {
    companion object {
        private var sharedPreferences: SharedPreferences? = null

        fun initialize(context: Context) {
            sharedPreferences = context.getSharedPreferences("green_signal_prefs", Context.MODE_PRIVATE)
        }

        fun get(): SharedPreferences {
            return sharedPreferences ?: error("SharedPrefsProvider is not initialized")
        }
    }
}