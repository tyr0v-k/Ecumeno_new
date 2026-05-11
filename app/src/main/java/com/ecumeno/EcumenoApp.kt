package com.ecumeno

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.ecumeno.data.local.preferences.PreferencesRepository

class EcumenoApp : Application() {
    val preferencesRepository by lazy { PreferencesRepository(this) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Ecumeno notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "ecumeno_channel_id"
    }
}