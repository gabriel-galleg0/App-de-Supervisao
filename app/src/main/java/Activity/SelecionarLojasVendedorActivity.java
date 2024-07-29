package Activity;



import static Activity.NotificationHelper.createNotificationChannel;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
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

public class SelecionarLojasVendedorActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_seleciona_vendedor); //Isso faz o layout de selecionar lojas para o vendedor ser inflado na tela

        mAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this); //Este método é para a tela ser preenchida de ponta a ponta


        /**
         * Métodos para setar as views (puxar o id do que irá aparecer na tela)
         */
        searchViewLojas = findViewById(R.id.searchViewLojas);
        searchViewRegioes = findViewById(R.id.searchViewRegiao);

        setSearchViewCor(searchViewLojas, Color.WHITE);
        setSearchViewCor(searchViewRegioes, Color.WHITE);

        recyclerViewLojas = findViewById(R.id.recyclerViewLojas);
        recyclerViewRegioes = findViewById(R.id.recyclerViewRegiao);
        botaoSair = findViewById(R.id.sair);

        nomeLojasFirebase = new ArrayList<>();

        carregarNomesLojasFirebase();
        loadRegioes();

        botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOff();
                finish();
            }
        });

        /**
         * Listener para a pesquisa das regiões
         *
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
        /**
         * Listener para pesquisar as lojas
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
    }
    private void setSearchViewCor(SearchView searchView, int color){
        AutoCompleteTextView searchTxt = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if(searchTxt != null){
         searchTxt.setTextColor(color);
        }
    }

    /**
     * Método que carrega as regiões do FirebaseStorage
     */
    private void loadRegioes() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Lojas/Cidades"); //Aqui eu coloco qual caminho será usado para carregar os dados

        storageRef.listAll().addOnSuccessListener(listResult -> {
            List<String> regioes = new ArrayList<>();
            for (StorageReference item : listResult.getItems()) {
                String cidadeNome = item.getName().replace(".json", "");
                regioes.add(cidadeNome);
            }
            regiaoList.clear();
            regiaoList.addAll(regioes);

            LinearLayoutManager layoutManager = new LinearLayoutManager(SelecionarLojasVendedorActivity.this);
            recyclerViewRegioes.setLayoutManager(layoutManager);
            regiaoAdapter = new RegiaoAdapter(SelecionarLojasVendedorActivity.this, regiaoList);
            recyclerViewRegioes.setAdapter(regiaoAdapter);

            /**
             * Aqui faz que quando o usuário tocar na região o app carrega as lojas
             */
            recyclerViewRegioes.addOnItemTouchListener(new RecyclerItemClickListener(SelecionarLojasVendedorActivity.this, recyclerViewRegioes, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    selectedRegiao = regiaoList.get(position);
                    loadJSONData(selectedRegiao);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null){
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    // Implementar ação para clique longo se necessário
                }
            }));

        }).addOnFailureListener(exception -> {
            Toast.makeText(SelecionarLojasVendedorActivity.this, "Erro ao carregar as regiões", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Carrega os dados de regiao
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

                LinearLayoutManager layoutManager = new LinearLayoutManager(SelecionarLojasVendedorActivity.this);
                recyclerViewLojas.setLayoutManager(layoutManager);
                lojaAdapter = new LojaAdapter(SelecionarLojasVendedorActivity.this, lojaList);
                recyclerViewLojas.setAdapter(lojaAdapter);
                /**
                 * Aqui faz que quando o usuário tocar na loja o app carrega as imagens
                 */
                recyclerViewLojas.addOnItemTouchListener(new RecyclerItemClickListener(SelecionarLojasVendedorActivity.this, recyclerViewLojas, new RecyclerItemClickListener.OnItemClickListener() {
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
            Toast.makeText(SelecionarLojasVendedorActivity.this, "Erro ao Carregar a Região", Toast.LENGTH_SHORT).show();
        });
    }
    /**
     * Auto explicativo
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
                Toast.makeText(SelecionarLojasVendedorActivity.this, "Erro ao carregar as lojas", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Método que manda para a próxima tela porém só se existir uma foto em imagenProblema com o msm nome da loja selecionada
     * @param view
     */
    public void proximaTela(View view) {
        if (selectedLojaObject != null) {
            String nomeFantasiaSelecionado = selectedLojaObject.optString("RAZAO SOCIAL");

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference referenciaStorage = storage.getReference().child("imagensProblema/");

            referenciaStorage.listAll().addOnSuccessListener(listResult -> {
                boolean imagemEncontrada = false;

                for (StorageReference item : listResult.getItems()) {
                    String nomeDaImagem = item.getName();

                    if (nomeDaImagem.contains(nomeFantasiaSelecionado)) {
                        imagemEncontrada = true;
                        break;
                    }
                }

                if (imagemEncontrada) {
                    Intent intent = new Intent(SelecionarLojasVendedorActivity.this, VendedorAcoes.class);
                    intent.putExtra("nome_loja", nomeFantasiaSelecionado);
                    intent.putExtra("dados_loja", selectedLojaObject.toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(SelecionarLojasVendedorActivity.this, "Nenhuma imagem encontrada para a loja selecionada", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(exception -> {
                Toast.makeText(SelecionarLojasVendedorActivity.this, "Erro ao listar imagens: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(SelecionarLojasVendedorActivity.this, "Selecione uma loja antes de prosseguir", Toast.LENGTH_SHORT).show();
        }
    }

    private void logOff() {
        mAuth.signOut();
        Intent intent = new Intent(SelecionarLojasVendedorActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

