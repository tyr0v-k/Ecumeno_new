package com.ecumeno.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.ecumeno.R
import com.ecumeno.data.local.preferences.PrefsHelper
import com.ecumeno.core.utils.models.CalendarDay
import com.ecumeno.core.utils.models.enums.FastLevel
import com.ecumeno.core.utils.EasterCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {
    private val calculator = EasterCalculator
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        scope.launch{
            val prefs = PrefsHelper(context)
            // 1. Получаем данные по дню
            val day = calculator.getDailyCalendar(prefs.confession)
            // 2. Показ уведомления
            showNotification(context, day)

            // 3. Перепланирование на следующий день
            if (prefs.isNotificationEnabled) {
                AlarmUtils.scheduleNotification(context, prefs.notificationHour, prefs.notificationMinute)
            }
        }

    }

    private fun showNotification(context: Context, day: CalendarDay) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ecumeno_channel_id"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Ecumeno notifications", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val dates : String
        if (day.holidays.isNotEmpty()) {
            dates = day.holidays.joinToString("\n") { holiday -> context.getString(context.resources.getIdentifier(holiday.name, "string", context.packageName)) }
        } else {
            dates = context.getString(R.string.no_dates).toString()
        }

        // Отображение поста
        val fastText = when (day.fastLevel) {
            FastLevel.NO_FAST -> context.getString(R.string.fast_no_fast)
            FastLevel.CONTINUOUS_WEEK -> context.getString(R.string.fast_continuous_week)
            FastLevel.XEROPHAGY -> context.getString(R.string.fast_xerophagy)
            FastLevel.NO_FISH -> context.getString(R.string.fast_no_fish)
            FastLevel.NO_OIL -> context.getString(R.string.fast_no_oil)
            FastLevel.FAST -> context.getString(R.string.fast_strict)
            FastLevel.WINE_OIL_ALLOWED -> context.getString(R.string.fast_wine_oil_allowed)
        }

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(HtmlCompat.fromHtml("<b>" + context.getString(R.string.app_name) + "</b>", FROM_HTML_MODE_LEGACY))
            .setContentText(context.getString(R.string.notification_message_small))
            .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_message).format(dates, fastText.lowercase())))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
    }
}