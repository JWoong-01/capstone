package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.android.volley.VolleyError;
import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "default_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 알림 채널 설정
        createNotificationChannel(context);

        // 알림 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 알림을 보내지 않음
            return;
        }

        if (intent.getBooleanExtra("checkExpiration", false)) {
            ApiRequest apiRequest = new ApiRequest(context);
            apiRequest.fetchIngredients(new ApiRequest.IngredientFetchListener() { // 재료 목록 가져오기
                @Override
                public void onFetchSuccess(List<Ingredient> ingredients) {
                    StringBuilder expiringSoon = new StringBuilder();

                    for (Ingredient ingredient : ingredients) {
                        if (ingredient.isExpiringSoon()) {  // 유통기한이 3일 이내인 재료를 확인
                            expiringSoon.append(ingredient.getName()).append(", ");
                        }
                    }

                    // 유통기한 얼마 남지 않은 재료가 있을 경우 알림 보내기
                    if (expiringSoon.length() > 0) {
                        expiringSoon.setLength(expiringSoon.length() - 2); // 마지막 쉼표 제거

                        // 알림 생성
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle("유통기한 알림")
                                .setContentText("재료의 유통기한이 얼마 남지 않았습니다: " + expiringSoon.toString())
                                .setPriority(NotificationCompat.PRIORITY_HIGH);
                        try {
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            notificationManager.notify(1, builder.build());
                        } catch (SecurityException e) {
                            // 권한이 거부된 경우 예외 처리
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFetchError(VolleyError error) {
                    // 서버 요청 실패 시 처리
                }
            });
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
