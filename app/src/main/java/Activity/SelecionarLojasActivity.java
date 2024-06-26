package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

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
import Model.RecyclerItemClickListener;
import Model.RegiaoAdapter;

public class SelecionarLojasActivity extends AppCompatActivity {
    private RecyclerView recyclerViewLojas;
    private RecyclerView recyclerViewRegioes;
    private SearchView searchViewLojas;
    private SearchView searchViewRegioes;
    private List<JSONObject> lojaList = new ArrayList<>(); // Lista de objetos JSONObject
    private List<String> regiaoList = new ArrayList<>();
    private LojaAdapter lojaAdapter;
    private RegiaoAdapter regiaoAdapter;
    private FirebaseAuth mAuth;
    private JSONObject selectedLojaObject;
    private String selectedRegiao;
    private List<String> nomeLojasFirebase;
    private Button botaoSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleciona_vendedor); //Nessa linha o conteudo é gerado através do layout q foi definido, nesse caso (activity_seleciona_vendedor)

        mAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this); //Deixa o conteúdo do layout visivel na tela inteira

        /**
         * Inicializa os elementos da tela
         */
        searchViewLojas = findViewById(R.id.searchViewLojas);
        searchViewRegioes = findViewById(R.id.searchViewRegiao);

        recyclerViewLojas = findViewById(R.id.recyclerViewLojas);
        recyclerViewRegioes = findViewById(R.id.recyclerViewRegiao);
        botaoSair = findViewById(R.id.sair);
        /**
         * Botão para sair do login de usuário, quando o app abrir se clicar nesse botão não irá fazer o login direto
         */
        botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOff();
                finish();
            }
        });

        nomeLojasFirebase = new ArrayList<>();

        carregarNomesLojasFirebase();
        loadRegioes();

        /**
         * Adiciona um listener de pesquisa para os campos de pesquisa nesse caso o de Lojas
         */
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
        /**
         * Adiciona um listener de pesquisa para o campo de Regiões
         */
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
    /**
     * Carrega as Regiões do FireBase pelo caminho definindo na linha 109
     */
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

            /**
             * Listener de toque na tela, que qnd clicar na região vai carregar as lojas daquela região
             */
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

    /**
     * Carrega os dados das lojas da região selecionada
     * @param regiao
     */
    private void loadJSONData(String regiao) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String path = "Lojas/Cidades/" + regiao + ".json";
        StorageReference storageRef = storage.getReference().child(path);

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            String jsonString = new String(bytes);
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                List<JSONObject> lojas = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject loja = jsonArray.getJSONObject(i);
                    lojas.add(loja);
                }
                lojaList.clear();
                lojaList.addAll(lojas);

                LinearLayoutManager layoutManager = new LinearLayoutManager(SelecionarLojasActivity.this);
                recyclerViewLojas.setLayoutManager(layoutManager);
                lojaAdapter = new LojaAdapter(SelecionarLojasActivity.this, lojaList);
                recyclerViewLojas.setAdapter(lojaAdapter);

                /**
                 * Listener de toque na tela, que qnd clicar na loja vai abrir a tela de ações do auditor
                 */
                recyclerViewLojas.addOnItemTouchListener(new RecyclerItemClickListener(SelecionarLojasActivity.this, recyclerViewLojas, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        selectedLojaObject = lojaList.get(position);
                        proximaTela(view);
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

    /**
     * Carrega os nomes das lojas do Firebase
     */
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
    /**
     * Método para navegar para a próxima tela no caso a tela de auditor
     * @param view
     */
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

    private void logOff(){
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}