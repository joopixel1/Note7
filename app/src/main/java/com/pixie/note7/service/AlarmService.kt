package com.pixie.note7.service

import android.app.*
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.pixie.note7.receiver.AlarmReceiver
import com.pixie.note7.receiver.AlarmReceiver.MyAlarmObjects.ACTION_STOP_ALARM

class AlarmService : IntentService("AlarmService") {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onHandleIntent(intent: Intent?) {
        val action = intent!!.action
        if(action == ACTION_STOP_ALARM){
            AlarmReceiver.taskRingtone!!.stop()
            AlarmReceiver.vibrator!!.cancel()
        }
    }
}