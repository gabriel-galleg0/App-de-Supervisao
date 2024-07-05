package Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.appjava.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "canalPadrao";
    private static final CharSequence CHANNEL_NAME = "Canal de Notificações";
    private static final String CHANNEL_DESCRIPTION = "Notificações";

    /**
     * Método que cria um canal para comunicar as notificações
     * @param context
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
