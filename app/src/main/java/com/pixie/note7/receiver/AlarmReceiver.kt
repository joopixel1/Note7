package com.pixie.note7.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.pixie.note7.MainActivity
import com.pixie.note7.R
import com.pixie.note7.service.AlarmService
import java.util.*

@Suppress("DEPRECATION")
class AlarmReceiver :BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onReceive(context: Context, intent: Intent?) {

        vibrator=context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator!!.vibrate(4000)

        alert= RingtoneManager.getDefaultUri (RingtoneManager.TYPE_ALARM)
        taskRingtone=RingtoneManager.getRingtone (context,alert)
        taskRingtone!!.play ()


        val title = intent?.getStringExtra("noteType")
        val message = intent!!.getStringExtra("noteTitle")
        val alarm = intent?.getStringExtra("alarm")
        val timestamp = intent?.extras?.getLong("timestamp") ?: 0


        if (timestamp > 0) {
            val notification = AlarmNotificationHelper(context)
            val mNotification = notification.getNotificationBuilder(title, message).build()
            notification.getManager().notify(getID(), mNotification)
            // mNotificationId is a unique int for each notification that you must define
        }
    }


    inner class AlarmNotificationHelper(context: Context): ContextWrapper(context){

        //for the channel
        private val MYCHANNEL_ID= "Pixie Notification ID"
        private val MYCHANNEL_NAME= "Pixie Note"


        private var manager: NotificationManager?=null
        init {
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                createChannels()
            }
        }

        //step 1 get manager
        fun getManager(): NotificationManager {
            if(manager==null)
                manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return manager as NotificationManager
        }

        // Step 2 create notification channel
        @RequiresApi(Build.VERSION_CODES.O)
        private fun createChannels(){
            val channel= NotificationChannel(MYCHANNEL_ID, MYCHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            channel.setShowBadge(true)
            channel.enableLights(true)
            channel.lightColor = Color.parseColor("#e8334a")
            channel.description = "Pixie Note"
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            getManager().createNotificationChannel(channel)
        }

        //step 3 build notification
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun getNotificationBuilder(title: String?, message: String?): NotificationCompat.Builder{

            val res = this.resources

            //action Show
            val notifyIntent= Intent(this, MainActivity::class.java)
            notifyIntent.putExtra("Service", true)
            notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            notifyIntent.action = ACTION_STOP_ALARM
            val pendingIntent = PendingIntent.getActivity(this.applicationContext, notificationId, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

            //action dismiss
            val dismissIntent = Intent(this, AlarmService::class.java)
            dismissIntent.action = ACTION_STOP_ALARM
            val pendingDismiss = PendingIntent.getService(this, notificationId, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

            //getNotificationBuilder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return NotificationCompat.Builder(applicationContext,MYCHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.iconround))
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setContentText(message)
                    .addAction(R.drawable.ic_alarm,"SHOW", pendingIntent)
                    .addAction(R.drawable.ic_alarm_stop,"STOP", pendingDismiss)
                    .setTimeoutAfter(3 * 60 * 1000)
                    .setUsesChronometer(true)
            }else{
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                return NotificationCompat.Builder(applicationContext,MYCHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.iconround))
                    .setAutoCancel(true)
                    .setUsesChronometer(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setSound(uri)
                    .setContentText(message)
            }

        }

    }

    companion  object MyAlarmObjects{
        private val notificationId = System.currentTimeMillis().toInt()
        const val ACTION_STOP_ALARM ="ACTION_STOP_ALARM"

        var taskRingtone: Ringtone? = null
        var alert: Uri? = null
        var vibrator: Vibrator? = null

        fun getID(): Int {
            return (Date().time / 1000L % Int.MAX_VALUE).toInt()
        }

    }

}