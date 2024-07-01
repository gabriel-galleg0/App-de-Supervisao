package Activity;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> userToken = new HashMap<>();
        userToken.put("fcmToken", token);

        db.collection("users").document(uid).set(userToken, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Token FCM salvo com sucesso!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Erro ao salvar token FCM", e);
                    }
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().size() > 0){
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");

            NotificationHelper.showNotification(this, title, body);
        }
    }
}
