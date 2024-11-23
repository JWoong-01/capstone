package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Calendar;

public class SettingActivity extends AppCompatActivity {
    private SwitchCompat switchNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        switchNotification = findViewById(R.id.switch_notification);

        // SharedPreferences 초기화
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isNotificationEnabled = sharedPreferences.getBoolean("notifications", false);
        switchNotification.setChecked(isNotificationEnabled);

        // 스위치 상태 변경 리스너 설정
        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("notifications", isChecked);
                editor.apply();

                if (isChecked) {
                    // 알림 활성화
                    scheduleDailyNotification();
                } else {
                    // 알림 비활성화
                    cancelScheduledNotification();
                }
            }
        });
    }

    // 알림 설정
    private void scheduleDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9); // 알림 시간 설정 (오전 9시)
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("checkExpiration", true);  // 유통기한 체크 기능
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);  // 알람 매니저 설정(알림 예약)

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    // 알림 해제
    private void cancelScheduledNotification() {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
