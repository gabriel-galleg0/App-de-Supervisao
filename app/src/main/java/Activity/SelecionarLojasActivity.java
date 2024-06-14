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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelecionarLojasActivity extends AppCompatActivity {

    private Spinner spinnerLojas;
    private Spinner spinnerRegiao;
    private Button btnSelecionarRegiao;
    private JSONObject selectedLojaObject;
    private String selectedRegiao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleciona_auditor);

        // Configuração para o Toolbar aparecer na tela
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerLojas = findViewById(R.id.spinner_lojas);
        spinnerRegiao = findViewById(R.id.spinner_regiao);
        btnSelecionarRegiao = findViewById(R.id.botaoRegiao);

        loadRegioes();

        btnSelecionarRegiao.setOnClickListener(v -> {
            if (selectedRegiao != null) {
                loadJSONData(selectedRegiao);
            } else {
                Toast.makeText(SelecionarLojasActivity.this, "Selecione uma região antes de prosseguir", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para carregar as regiões disponíveis
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

        spinnerRegiao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRegiao = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Não faz nada
            }
        });
    }

    // Método para carregar os dados do JSON da loja com base na região selecionada
    private void loadJSONData(String regiao) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String path = "Lojas/" + regiao + "/loja1.json";
        StorageReference storageRef = storage.getReference().child(path);

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            String jsonString = new String(bytes);

            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                List<String> lojas = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    lojas.add(jsonArray.getJSONObject(i).getString("NOME_FANTASIA"));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(SelecionarLojasActivity.this,
                        android.R.layout.simple_spinner_item, lojas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLojas.setAdapter(adapter);

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
                        // Não faz nada
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(exception -> {
            Toast.makeText(SelecionarLojasActivity.this, "Erro ao carregar dados do JSON", Toast.LENGTH_SHORT).show();
        });
    }

    // Método para navegar para a próxima tela
    public void proximaTela(View view) {
        if (selectedLojaObject != null) {
            Intent intent = new Intent(SelecionarLojasActivity.this, LoginAuditorActivity.class);
            intent.putExtra("nome_loja", selectedLojaObject.optString("NOME_FANTASIA"));
            intent.putExtra("dados_loja", selectedLojaObject.toString());
            startActivity(intent);
        } else {
            Toast.makeText(SelecionarLojasActivity.this, "Selecione uma loja antes de prosseguir", Toast.LENGTH_SHORT).show();
        }
    }
}