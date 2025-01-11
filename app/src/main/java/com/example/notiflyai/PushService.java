package com.example.notiflyai;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushService extends FirebaseMessagingService {

    private static final String TAG = "PushService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Пример извлечения данных:
        String title = "NotiFly";
        String body = "Новое сообщение";
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        Log.d(TAG, "onMessageReceived: title=" + title + ", body=" + body);

        // Просто показать уведомление, как пример:
        showNotification(title, body);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM Token: " + token);
        // Обычно отправляют token на сервер
    }

    private void showNotification(String title, String body) {
        String channelId = "notifly_channel_id";

        // Создадим канал (только если API >= 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "NotiFlyChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        // Собираем само уведомление
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId) // или getApplicationContext()
                        .setSmallIcon(R.drawable.ic_launcher_foreground) // замените на свою иконку
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Отправляем
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}