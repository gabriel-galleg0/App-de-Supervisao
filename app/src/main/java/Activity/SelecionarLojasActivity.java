package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import Model.RecyclerItemClickListener;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Model.LojaAdapter;
import Model.RegiaoAdapter;

public class SelecionarLojasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLojas;
    private RecyclerView recyclerViewRegioes;
    private SearchView searchViewLojas;
    private SearchView searchViewRegioes;
    private List<String> lojaList = new ArrayList<>();
    private List<String> regiaoList = new ArrayList<>();
    private LojaAdapter lojaAdapter;
    private RegiaoAdapter regiaoAdapter;
    private FirebaseAuth mAuth;
    private JSONObject selectedLojaObject;
    private String selectedRegiao;
    private List<String> nomeLojasFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleciona_vendedor);

        mAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);

        searchViewLojas = findViewById(R.id.searchViewLojas);
        searchViewRegioes = findViewById(R.id.searchViewRegiao);

        recyclerViewLojas = findViewById(R.id.recyclerViewLojas);
        recyclerViewRegioes = findViewById(R.id.recyclerViewRegiao);

        nomeLojasFirebase = new ArrayList<>();

        carregarNomesLojasFirebase();
        loadRegioes();

        searchViewLojas.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                lojaAdapter.getFilter().filter(newText);
                return true;
            }
        });

        searchViewRegioes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                regiaoAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void loadRegioes() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Lojas/Cidades");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            List<String> regioes = new ArrayList<>();
            for (StorageReference item : listResult.getItems()) {
                String cidadeNome = item.getName().replace(".json", "");
                regioes.add(cidadeNome);
            }
            regiaoList.clear();
            regiaoList.addAll(regioes);

            LinearLayoutManager layoutManager = new LinearLayoutManager(SelecionarLojasActivity.this);
            recyclerViewRegioes.setLayoutManager(layoutManager);
            regiaoAdapter = new RegiaoAdapter(SelecionarLojasActivity.this, regiaoList);
            recyclerViewRegioes.setAdapter(regiaoAdapter);

            recyclerViewRegioes.addOnItemTouchListener(new RecyclerItemClickListener(SelecionarLojasActivity.this, recyclerViewRegioes, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    selectedRegiao = regiaoList.get(position);
                    loadJSONData(selectedRegiao);
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    // Implementar ação para clique longo se necessário
                }
            }));

        }).addOnFailureListener(exception -> {
            Toast.makeText(SelecionarLojasActivity.this, "Erro ao carregar as regiões", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadJSONData(String regiao) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String path = "Lojas/Cidades/" + regiao + ".json";
        StorageReference storageRef = storage.getReference().child(path);

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            String jsonString = new String(bytes);
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                List<String> lojas = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    lojas.add(jsonArray.getJSONObject(i).getString("RAZAO SOCIAL"));
                }
                lojaList.clear();
                lojaList.addAll(lojas);

                LinearLayoutManager layoutManager = new LinearLayoutManager(SelecionarLojasActivity.this);
                recyclerViewLojas.setLayoutManager(layoutManager);
                lojaAdapter = new LojaAdapter(SelecionarLojasActivity.this, lojaList);
                recyclerViewLojas.setAdapter(lojaAdapter);

                recyclerViewLojas.addOnItemTouchListener(new RecyclerItemClickListener(SelecionarLojasActivity.this, recyclerViewLojas, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            selectedLojaObject = jsonArray.getJSONObject(position);
                            proximaTela(view);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // Implementar ação para clique longo se necessário
                    }
                }));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(exception -> {
            Toast.makeText(SelecionarLojasActivity.this, "Erro ao Carregar a Região", Toast.LENGTH_SHORT).show();
        });
    }

    private void carregarNomesLojasFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Lojas");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nomeLojasFirebase.clear();
                for (DataSnapshot lojaSnapshot : dataSnapshot.getChildren()) {
                    String nomeDaLoja = lojaSnapshot.child("RAZAO SOCIAL").getValue(String.class);
                    if (nomeDaLoja != null) {
                        nomeLojasFirebase.add(nomeDaLoja);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SelecionarLojasActivity.this, "Erro ao carregar as lojas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void proximaTela(View view) {
        if (selectedLojaObject != null) {
            Intent intent = new Intent(SelecionarLojasActivity.this, LoginAuditorActivity.class);
            intent.putExtra("nome_loja", selectedLojaObject.optString("RAZAO SOCIAL"));
            intent.putExtra("dados_loja", selectedLojaObject.toString());
            startActivity(intent);
        } else {
            Toast.makeText(SelecionarLojasActivity.this, "Selecione uma loja antes de prosseguir", Toast.LENGTH_SHORT).show();
        }
    }
}
