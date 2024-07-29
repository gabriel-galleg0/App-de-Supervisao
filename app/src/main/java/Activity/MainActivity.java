package Activity;

import static Activity.NotificationHelper.createNotificationChannel;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appjava.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    Button entrarVendedor;
    Button entrarAuditor;
    Button entrarSupervisao;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Habilita o layout de borda a borda
        setContentView(R.layout.activity_main2); // Define o layout activity_main2
        inicializar(); // Inicializa os componentes
        FirebaseApp.initializeApp(this);
        createNotificationChannel(this);
        FirebaseAppCheck fireCheck = FirebaseAppCheck.getInstance();
        fireCheck.installAppCheckProviderFactory(
        PlayIntegrityAppCheckProviderFactory.getInstance());

        // Verificar a versão do Android para criar o canal de notificação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "canalPadrao",
                    "Canal de Notificações",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notificações");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Configuração dos botões
        entrarVendedor.setOnClickListener(v -> startActivity(new Intent(this, VendedorActivity.class)));
        entrarAuditor.setOnClickListener(v -> startActivity(new Intent(this, AuditorActivity.class)));
        entrarSupervisao.setOnClickListener(v -> startActivity(new Intent(this, LoginGerente.class)));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // Verifica se o usuário está logado
        if (user != null) {
            saveUserToDatastore(user);
        }

        // Solicita permissões
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    PERMISSION_REQUEST_CODE);
        }

        // Ajusta o padding para considerar as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.principal1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void inicializar() {
        entrarVendedor = findViewById(R.id.entrarVendedor);
        entrarAuditor = findViewById(R.id.entrarAuditor);
        entrarSupervisao = findViewById(R.id.entrarSupervisao);
    }

    private void saveUserToDatastore(FirebaseUser user) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("MainActivity", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // pega um novo token fcm
                    String token = task.getResult();
                    sendUserDataToServer(user.getUid(), user.getPhoneNumber(), token);
                });
    }

    private void sendUserDataToServer(String uid, String phoneNumber, String fcmToken) {
        // Aqui é para caso necessite implementar algum envio de dados para o servidor
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida pelo usuário
            } else {
                // Ainda preciso implementar algo caso o usuário negue as notificações
            }
        }
    }
}
