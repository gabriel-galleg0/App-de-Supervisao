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
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
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
    private Button botaoFormulario;
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
        FirebaseApp.initializeApp(VendedorAcoes.this);
        setContentView(R.layout.activity_vendedor_acoes);
        TextView nomePdv = findViewById(R.id.nomePdv);

        FirebaseAppCheck fireCheck = FirebaseAppCheck.getInstance();
        fireCheck.installAppCheckProviderFactory(
        PlayIntegrityAppCheckProviderFactory.getInstance());

        Log.d("DEBUG0", "Context: " + this);
        /**
         * inicizlador das variáveis
         */
        botaoFormulario = findViewById(R.id.botaoEnviarVendedor);
        recyclerView = findViewById(R.id.rv_acoes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EdgeToEdge.enable(this);

        auth = FirebaseAuth.getInstance();
        Log.d("VendedorAcoes", "Usuário logado: " + auth.getCurrentUser());
        Log.d("DEBUG", "UID: " + auth.getUid());
        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(VendedorAcoes.this, VendedorActivity.class);
            startActivity(intent);
            finish();
        }else{

            String regiao = getIntent().getStringExtra("regiao");
            String nomeLoja = getIntent().getStringExtra("nome_loja");

            Log.d("INFOS", "infos: " + regiao  + " " + nomeLoja);

            if(nomeLoja!=null){
                nomeLojaSelecionada = nomeLoja;
                nomePdv.setText(nomeLojaSelecionada);
                carregarRecycler(nomeLojaSelecionada);
            }
        }
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
            carregarRecycler(nomeLojaSelecionada);
            nomePdv.setText(nomeLojaSelecionada);
        }
     }
    private void carregarRecycler(String nomeLojaSelecionada) {
        RecyclerView recyclerView = findViewById(R.id.rv_acoes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("imagensProblema/");

        storageRef.listAll().addOnSuccessListener(listResult -> {
        imageItems.clear();

            for (StorageReference item : listResult.getItems()) {
                String nomeArquivo = item.getName();
                String[] dividido = nomeArquivo.split("_");

                if (dividido.length >= 4) {
                    String lojaNome = dividido[1];
                    Log.d("VendedorAcoes", "Nome da loja: " + lojaNome + " Nome da loja selecionada: " + nomeLojaSelecionada);

                    if (nomeLojaSelecionada.contains(lojaNome)) {
                        String pendencia = dividido[0];
                        ImageItem imageItem = new ImageItem(nomeLojaSelecionada, pendencia, item);
                        imageItems.add(imageItem);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        });
    }
}