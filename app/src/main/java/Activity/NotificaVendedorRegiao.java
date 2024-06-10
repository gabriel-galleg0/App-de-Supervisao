package Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import com.example.appjava.R;

public class NotificaVendedorRegiao extends AppCompatActivity {

    private StorageReference storageRef;

    public NotificaVendedorRegiao() {
        // Referência para o Firebase Storage
        storageRef = FirebaseStorage.getInstance().getReference("gs://appjava1-2968b.appspot.com");
    }

    public void startMonitoring() {
        // Monitora o diretório no Firebase Storage onde as novas fotos são adicionadas
        StorageReference fotosRef = storageRef.child("imagensProblema/");
        fotosRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                // Verifica se o item é uma nova foto com base em algum critério
                // Por exemplo, você pode comparar o tempo de upload com o tempo atual
                // Se for uma nova foto, envie uma notificação
                enviarNotificacao("Um novo problema foi encontrado.");
            }
        }).addOnFailureListener(exception -> {
            // Tratar falhas na obtenção da lista de itens
        });
    }

    private void enviarNotificacao(String mensagem) {
        // Código para enviar uma notificação ao usuário
        // Por exemplo, usando o NotificationManager
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifica_vendedor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}