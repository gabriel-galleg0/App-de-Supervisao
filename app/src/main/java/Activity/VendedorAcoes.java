package Activity;

import static android.content.Intent.getIntent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appjava.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
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
    private static final int MAX_IMAGES_TO_DISPLAY = 3;
    private String nomeLojaSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vendedor_acoes);
        FirebaseApp.initializeApp(VendedorAcoes.this);
        TextView nomePdv = findViewById(R.id.nomePdv);

        botaoFormulario = findViewById(R.id.botaoEnviarVendedor);
        recyclerView = findViewById(R.id.rv_acoes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(imageItems);
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
}
