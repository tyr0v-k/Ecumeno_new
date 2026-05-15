package com.ecumeno.infrastructure.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ecumeno.EcumenoApp
import com.ecumeno.R
import com.ecumeno.core.calculator.EasterCalculator
import com.ecumeno.core.domain.CalendarDay
import com.ecumeno.data.local.preferences.Confession
import com.ecumeno.core.domain.FastLevel
import kotlinx.coroutines.flow.first

class NotificationWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val preferencesRepository = (applicationContext as EcumenoApp).preferencesRepository
            val confession = Confession.fromPreferences(preferencesRepository.confession.first())
            val day = EasterCalculator.getDailyCalendar(confession)

            showNotification(applicationContext, day, confession)

            if (preferencesRepository.isNotificationEnabled.first()) {
                AlarmUtils.scheduleNotification(
                    applicationContext,
                    preferencesRepository.notificationHour.first(),
                    preferencesRepository.notificationMinute.first()
                )
            }

            Result.success()
        } catch (e: SecurityException) {
            Result.failure()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private fun showNotification(context: Context, day: CalendarDay, confession: Confession) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ecumeno_channel_id"

        val dates : String
        if (day.holidays.isNotEmpty()) {
            dates = day.holidays.joinToString("\n") { holiday -> context.getString(context.resources.getIdentifier(holiday.name, "string", context.packageName)) }
        } else {
            dates = context.getString(R.string.no_dates)
        }

        val fastText = when (day.fastLevel) {
            FastLevel.NO_FAST -> context.getString(R.string.fast_no_fast)
            FastLevel.CONTINUOUS_WEEK -> context.getString(R.string.fast_continuous_week)
            FastLevel.XEROPHAGY -> context.getString(R.string.fast_xerophagy)
            FastLevel.FISH -> context.getString(R.string.fast_fish)
            FastLevel.NO_OIL -> context.getString(R.string.fast_no_oil)
            FastLevel.FAST -> context.getString(R.string.fast_strict)
            FastLevel.OIL_ALLOWED -> context.getString(R.string.fast_oil_allowed)
            FastLevel.ABSTINENCE -> context.getString(R.string.abstinence)
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
            .setStyle(NotificationCompat.BigTextStyle().bigText(if (confession != Confession.lut) context.getString(R.string.notification_message).format(dates, fastText.lowercase()) else context.getString(R.string.notification_message_lut).format(dates)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
    }
}