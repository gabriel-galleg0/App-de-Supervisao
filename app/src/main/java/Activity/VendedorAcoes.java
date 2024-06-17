package Activity;



import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appjava.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import Model.ImageItem;
import Model.MyAdapter;

public class VendedorAcoes extends AppCompatActivity {

    private Button botaoFormulario;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<ImageItem> imageItems = new ArrayList<>();
    private String nomeLojaSelecionada = "";
    private ActivityResultLauncher<Intent> cameraLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vendedor_acoes);
        FirebaseApp.initializeApp(VendedorAcoes.this);
        TextView nomePdv = findViewById(R.id.nomePdv);

        /**
         * inicizlador das variáveis
         */
        botaoFormulario = findViewById(R.id.botaoEnviarVendedor);
        recyclerView = findViewById(R.id.rv_acoes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {

            } else {
                Toast.makeText(this, "Cancelado ou falha ao tirar foto", Toast.LENGTH_SHORT).show();
            }
        });
        adapter = new MyAdapter(imageItems, this, cameraLauncher);
        recyclerView.setAdapter(adapter);


        if (getIntent().hasExtra("nome_loja")) {
            nomeLojaSelecionada = getIntent().getStringExtra("nome_loja");
            nomePdv.setText(nomeLojaSelecionada);
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("imagensProblema");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                String nomeDaImagem = item.getName();
                String[] partesNome = nomeDaImagem.split("_");
                if (partesNome.length >= 2 && partesNome[1].equals(nomeLojaSelecionada)) {
                    String problema = partesNome[0];
                    ImageItem imageItem = new ImageItem(nomeLojaSelecionada, problema, item);
                    imageItems.add(imageItem);
                    adapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(VendedorAcoes.this, "Erro ao listar imagens: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void initializeCameraLauncher() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

                    } else {
                        Toast.makeText(VendedorAcoes.this, "Falha ao capturar a imagem.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
