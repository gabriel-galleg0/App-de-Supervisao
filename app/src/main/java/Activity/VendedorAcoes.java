package Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appjava.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import Model.ImageItem;
import Model.MyAdapter;
public class VendedorAcoes extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button botaoFormulario;
    private ProgressBar carrega;
    private String regiaoSelecionada = "";
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<ImageItem> imageItems = new ArrayList<>();
    private String nomeLojaSelecionada = "";
    private ActivityResultLauncher<Intent> cameraLauncher;
    private FirebaseAuth auth;
    private String numeroTelefoneUsuario = "";

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
        EdgeToEdge.enable(this);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && currentUser.getPhoneNumber() != null) {
            numeroTelefoneUsuario = currentUser.getPhoneNumber();
        }


        botaoFormulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VendedorAcoes.this, "Formulário enviado", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        /**
         * ResultActivity da camera
         */
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getExtras() != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    /**
                     * Define a imagem tirada no imageView nesse caso o recNova que está dentro de item_layout
                     */
                    if (adapter.getUltimoHolder() != null) {
                        adapter.getUltimoHolder().setImageBitmap(imageBitmap);
                    } else {
                        Toast.makeText(this, "Erro: Nenhum item selecionado", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Cancelado ou falha ao tirar foto", Toast.LENGTH_SHORT).show();
            }
        });
        adapter = new MyAdapter(imageItems, this, cameraLauncher);
        recyclerView.setAdapter(adapter);

        if (getIntent().hasExtra("nome_loja")) {
            nomeLojaSelecionada = getIntent().getStringExtra("nome_loja");
            Log.d("VendedorAcoes", "Nome da loja selecionada: " + nomeLojaSelecionada);
            nomePdv.setText(nomeLojaSelecionada);

        } else {

        }


        /**
         * Instancias do FireBaseStorage e StorageReference
         */


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("imagensProblema/");

        Log.d("VendedorAcoes", "Storage " + storageRef.getPath());
        storageRef.listAll().addOnSuccessListener(listResult -> {
            imageItems.clear();
            for (StorageReference item : listResult.getItems()) {
                String nomeDaImagem = item.getName();

                String[] partesNome = nomeDaImagem.split("_");
                if (partesNome.length >= 4) {

                    String lojaNome = partesNome[1];

                    if (lojaNome.equals(nomeLojaSelecionada)) {
                        String pendencia = partesNome[0];
                        ImageItem imageItem = new ImageItem(nomeDaImagem, pendencia, item);
                        imageItems.add(imageItem);
                        Log.d("VendedorAcoes", "Nome da imagem: " + nomeDaImagem);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(VendedorAcoes.this, "Erro ao listar imagens: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
