package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.android.volley.VolleyError;
import java.util.List;
import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "default_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 알림 채널 설정
        createNotificationChannel(context);

        String ingredientName = intent.getStringExtra("ingredientName");
        String expirationDate = intent.getStringExtra("expirationDate");
        int daysBefore = intent.getIntExtra("daysBefore", 0); // 며칠 전에 알림을 받을 것인지

        // 유통기한이 설정된 날짜로부터 며칠 전에 알림을 받도록 처리
        if (ingredientName != null && expirationDate != null) {
            // 날짜 형식 파싱
            String[] dateParts = expirationDate.split("-");
            if (dateParts.length == 3) {
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]) - 1; // Calendar는 0-based month
                int day = Integer.parseInt(dateParts[2]);

                // 알림 예약 날짜 설정
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                // daysBefore에 맞춰 알림 날짜 변경
                calendar.add(Calendar.DAY_OF_MONTH, -daysBefore);

                // 알림을 설정할 날짜가 미래일 경우에만 알림을 보냄
                if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                    // 알림 생성
                    String message = (daysBefore == 0)
                            ? ingredientName + "의 유통기한이 오늘입니다."
                            : ingredientName + "의 유통기한이 " + daysBefore + "일 남았습니다.";

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle("유통기한 알림")
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    int notificationId = ingredientName.hashCode(); // 고유 ID
                    notificationManager.notify(notificationId, builder.build());
                } else {
                    // 알림 시간이 이미 지난 경우 처리
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle("유통기한 알림")
                            .setContentText("알림 날짜가 지났습니다.: " + ingredientName)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    int notificationId = (ingredientName + "_expired").hashCode();
                    notificationManager.notify(notificationId, builder.build());
                }
            }
        }
    }

    // 알림 채널을 생성하는 메서드
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default Channel";
            String description = "알림 채널";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // 알림 채널을 시스템에 등록
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}