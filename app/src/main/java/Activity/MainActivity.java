package Activity;



import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    Button entrarVendedor;
    Button entrarAuditor;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Habilita o layout de borda a borda
        setContentView(R.layout.activity_main2); // Define o layout activity_main2

        // Configuração dos botões
        entrarVendedor = findViewById(R.id.entrarVendedor);
        entrarAuditor = findViewById(R.id.entrarAuditor);

        // Ação do botão vendedor para trocar de tela
        entrarVendedor.setOnClickListener(v -> {
            startActivity(new Intent(this, VendedorActivity.class));
        });

        // Ação do botão auditor para trocar de tela
        entrarAuditor.setOnClickListener(v -> {
            startActivity(new Intent(this, AuditorActivity.class));
        });



        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // Usuário já está autenticado
            saveUserToDatastore(user);
        } else {
            // Realize a autenticação por SMS e, em seguida, chame saveUserToDatastore(user)
        }




        NotificationHelper.createNotificationChannel(this);

        // Solicitar permissões necessárias
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Permissão já concedida
        }

        /**
         * Instanciando os botões pelo ID
         */
        entrarAuditor = findViewById(R.id.entrarAuditor);
        entrarVendedor = findViewById(R.id.entrarVendedor);

        /**
         * Ação do Botão vendedor para trocar de tela
         */
        entrarVendedor.setOnClickListener(v -> {
            startActivity(new Intent(this, VendedorActivity.class));
        });

        /**
         * Ação do Botão auditor para trocar de tela
         */
        entrarAuditor.setOnClickListener(v -> {
            startActivity(new Intent(this, AuditorActivity.class));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.principal1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void saveUserToDatastore(FirebaseUser user) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("MainActivity", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Envie esses dados para o seu servidor para salvar no Datastore
                    // Você pode usar Retrofit, HttpUrlConnection ou qualquer biblioteca de rede para enviar os dados
                    sendUserDataToServer(user.getUid(), user.getPhoneNumber(), token);
                });
    }

    private void sendUserDataToServer(String uid, String phoneNumber, String fcmToken) {
        // Implementar o envio dos dados para o seu servidor para salvar no Datastore
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida pelo usuário
                // Inicialize o serviço de notificação ou outras funcionalidades necessárias
            } else {
                // Permissão negada pelo usuário
                // Informe ao usuário sobre a importância da permissão
            }
        }
    }
}
