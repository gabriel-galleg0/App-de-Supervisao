package Activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appjava.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "canalPadrao";
    private String lojas = "";
    private String regiao ="";
    private String pendencias = "";

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

        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String iconUrl = remoteMessage.getData().get("icon");
            String notificationId = remoteMessage.getData().get("notification_id");

            long notificationIdLong = Long.parseLong(notificationId);

            showNotification(notificationIdLong, title, body, iconUrl);
        }
    }

    private void showNotification(long notificationIdLong, String title, String messageBody, String iconUrl) {
        Log.d("Notification", "Mensagem " + messageBody);

        String[] partes = messageBody.split(" ");
         lojas = "";
         regiao = "";
        pendencias = "";

        if(partes.length > 5){
            regiao = partes[partes.length - 1];
            lojas = partes[5];
            pendencias = partes[0];

            for (int i = 6; i < partes.length -4; i++) {
                lojas += " " + partes[i];
            }
        }

        Log.d("Notification", "Loja: "  + lojas + " Regiao " + regiao);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Boolean smsVerified = dataSnapshot.child("smsVerified").getValue(Boolean.class);
                        if (smsVerified != null && smsVerified) {
                            // Usuário verificado, abrir a atividade
                            Intent intent = new Intent(MyFirebaseMessagingService.this, VendedorAcoes.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("regiao", regiao);
                            intent.putExtra("pendencias", pendencias);
                            intent.putExtra("nome_loja", lojas);
                            PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_factu_teste)  // icon padrao mas não ta certo ainda, está pequeno
                                    .setContentTitle(title)
                                    .setContentText(messageBody)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent);

                            if (iconUrl != null && !iconUrl.isEmpty()) {
                                // Carregar a imagem do ícone usando Glide
                                Glide.with(MyFirebaseMessagingService.this)
                                        .asBitmap()
                                        .load(iconUrl)
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                notificationBuilder.setLargeIcon(resource);  // Define o ícone grande da notificação
                                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                notificationManager.notify((int)notificationIdLong, notificationBuilder.build());
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                            } else {
                                // Caso algo de errado com o ícone, mostrar um ícone padrão
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(0, notificationBuilder.build());
                            }
                        } else {
                            Log.d(TAG, "Usuário não verificado ou campo smsVerified não encontrado.");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Erro ao verificar smsVerified: ", databaseError.toException());
                }
            });
        }
    }
}
