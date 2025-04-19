package com.example.reinforcement.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * BroadcastReceiver que se ejecuta cuando el dispositivo completa el arranque
 * para reprogramar el reseteo diario
 */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootCompletedReceiver", "Dispositivo reiniciado, reprogramando reseteo diario")
            
            val resetManager = ResetManager(context)
            resetManager.scheduleReset()
        }
    }
}