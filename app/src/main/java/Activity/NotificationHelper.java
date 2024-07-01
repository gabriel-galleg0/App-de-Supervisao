package Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.appjava.R;

public class NotificationHelper {


    private static final String CHANNEL_ID = "canalPadrao";
    private static final CharSequence CHANNEL_NAME = "My Channel";
    private static final String CHANNEL_DESCRIPTION = "Channel description";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showNotification(Context context, String title, String message) {
        if (!areNotificationsEnabled(context)) {
            // Notificações estão bloqueadas
            // Informar o usuário ou tomar outra ação apropriada
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.factu_png2)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public static boolean areNotificationsEnabled(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return notificationManager.areNotificationsEnabled();
        } else {
            return Settings.Secure.getInt(context.getContentResolver(), "enabled_notification_listeners", 0) != 0;
        }
    }
}
