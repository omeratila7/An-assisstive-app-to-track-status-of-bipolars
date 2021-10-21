package com.example.bitirme

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmService(private val context: Context) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(android.content.Context.ALARM_SERVICE) as AlarmManager?


    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent
                )
            }
        }
    }

    fun setRepetitiveAlarm(timeInMillis: Long) {
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    action = AlarmConstants.ACTION_SET_REPETITIVE_EXACT
                    putExtra(AlarmConstants.EXTRA_EXACT_ALARM_TIME, timeInMillis)
                }
            )
        )
    }

    private fun getIntent() = Intent(
        context,
        AlarmReceiver::class.java
    )

    private fun getPendingIntent(intent: Intent) =
        PendingIntent.getBroadcast(
            context,
            getRandomRequestCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun getRandomRequestCode(): Int = RandomIntUtil.getRandomInt()
}