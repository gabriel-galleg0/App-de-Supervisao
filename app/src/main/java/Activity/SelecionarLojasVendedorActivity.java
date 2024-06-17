package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appjava.R;
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

public class SelecionarLojasVendedorActivity extends AppCompatActivity {

    private Spinner spinnerLojas;
    private Spinner spinnerRegiao;
    private Button btnSelecionarRegiao;
    private JSONObject selectedLojaObject;
    private String selectedRegiao;
    private List<String> nomeLojasFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleciona_vendedor);

        /**
         * Faz a toolbar aparecer no topo da tela
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerLojas = findViewById(R.id.spinner_lojas);
        spinnerRegiao = findViewById(R.id.spinner_regiao);
        btnSelecionarRegiao = findViewById(R.id.botaoRegiao);

        nomeLojasFirebase = new ArrayList<>();

        /**
         * Carrega quais as lojas presentes no RealTimeFireBase
         */
        carregarNomesLojasFirebase();

        loadRegioes();

        btnSelecionarRegiao.setOnClickListener(v -> {
            if (selectedRegiao != null) {
                loadJSONData(selectedRegiao);
            } else {
                Toast.makeText(SelecionarLojasVendedorActivity.this, "Selecione uma região antes de prosseguir", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Mostra quais regiões estão presentes no Banco de dados
     */
    private void loadRegioes() {
        List<String> regioes = new ArrayList<>();
        regioes.add("Abrao");
        regioes.add("Centro");
        regioes.add("Norte");
        regioes.add("Sul");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, regioes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegiao.setAdapter(adapter);

        /**
         * Listener para quando o usuário selecionar uma região
         */
        spinnerRegiao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRegiao = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            //Aqui não coloquei método por não saber o que colocar caso não selecione nenhum item
            }
        });
    }

    /**
     * Carrega os dados do JSON de acordo com a região selecionada no Spinner
     * @param regiao
     */
    private void loadJSONData(String regiao) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String path = "Lojas/" + regiao + "/loja1.json";
        StorageReference storageRef = storage.getReference().child(path);

        /**
         * Faz o download do JSON usando o storageRef
         */
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            String jsonString = new String(bytes);

            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                List<String> lojas = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    lojas.add(jsonArray.getJSONObject(i).getString("NOME_FANTASIA"));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(SelecionarLojasVendedorActivity.this,
                        android.R.layout.simple_spinner_item, lojas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLojas.setAdapter(adapter);

                /**
                 * Listenner do spinner de lojas para quando o usuário selecionar uma loja
                 */
                spinnerLojas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            selectedLojaObject = jsonArray.getJSONObject(position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Não faz nada tbm pois acho que não tem necessidade fazer algo caso não seja selecionado
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(exception -> {
            Toast.makeText(SelecionarLojasVendedorActivity.this, "Erro ao carregar dados do JSON", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Carrega os nomes das lojas no firebase
     */
    private void carregarNomesLojasFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Lojas");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nomeLojasFirebase.clear();
                for (DataSnapshot lojaSnapshot : dataSnapshot.getChildren()) {
                    String nomeDaLoja = lojaSnapshot.child("nome_fantasia").getValue(String.class);
                    if (nomeDaLoja != null) {
                        nomeLojasFirebase.add(nomeDaLoja);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SelecionarLojasVendedorActivity.this, "Erro ao carregar nomes das lojas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Faz com que quando apertado no botão encaminhe para a próxima tela
     * @param view
     */
    public void proximaTela(View view) {
        if (selectedLojaObject != null) {
            String nomeFantasiaSelecionado = selectedLojaObject.optString("NOME_FANTASIA");

            /**
             * Acesso ao firebase Storage, creio que não seria necessário colocar aqui novamente mas coloquei por precaução
             */
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference referenciaStorage = storage.getReference().child("imagensProblema");

            /**
             * Confere se existe alguma imagem dentro da pasta do FireBaseStorage com o nome da loja selecionada
             */
            referenciaStorage.listAll().addOnSuccessListener(listResult -> {
                boolean imagemEncontrada = false;

                /**
                 * Laço de repetição para ver os itens dentro do Storage
                 */
                for (StorageReference item : listResult.getItems()) {
                    /**
                     * Pega o nome da imagem e transforma numa string para comparar com o nome da loja selecionada
                     */
                    String nomeDaImagem = item.getName();

                    /**
                     * Verifica se o nome da imagem bate com o nome da loja selecionada
                     */
                    if (nomeDaImagem.contains(nomeFantasiaSelecionado)) {
                        imagemEncontrada = true;
                        break;
                    }
                }

                if (imagemEncontrada) {
                    /**
                     * Quando a imagem for encontrada e os nomes forem iguais, leva para a tela de ações do vendedor para realizar o atendimento
                     */
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


}
