package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private JSONObject selectedLojaObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_vendedor);

        /**
         * Configuração para o Toolbar aparecer na tela
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerLojas = findViewById(R.id.spinner_lojas);

        /**
         * Carrega os dados JSON da loja do FireBase
         */
        loadJSONData();
    }

    private void loadJSONData() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Lojas/loja1.json");

        /**
         * Configutação para o StringuBuilder
         */
        StringBuilder stringBuilder = new StringBuilder();
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            String jsonString = new String(bytes);

            try {
                /**
                 * Converte o Jsonn para JsonArray
                 */
                JSONArray jsonArray = new JSONArray(jsonString);
                List<String> lojas = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    lojas.add(jsonArray.getJSONObject(i).getString("NOME_FANTASIA"));
                }

                // Configurando o adaptador do Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SelecionarLojasActivity.this,
                        android.R.layout.simple_spinner_item, lojas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLojas.setAdapter(adapter);

                spinnerLojas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Extraindo o item selecionado do JSON
                        try {
                            String selectedLoja = (String) parent.getItemAtPosition(position);
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
            // Tratamento de falha ao carregar o JSON
            Toast.makeText(SelecionarLojasActivity.this, "Erro ao carregar dados do JSON", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Método para navegar para a próxima tela
     * @param view
     */
    public void proximaTela(View view) {
        if (selectedLojaObject != null) {
            // Enviando para a tela de tirar fotos junto com os dados da loja
            Intent intent = new Intent(SelecionarLojasActivity.this, LoginAuditorActivity.class);
            intent.putExtra("nome_loja", selectedLojaObject.optString("NOME_FANTASIA"));
            intent.putExtra("dados_loja", selectedLojaObject.toString());
            startActivity(intent);
        } else {
            Toast.makeText(SelecionarLojasActivity.this, "Selecione uma loja antes de prosseguir", Toast.LENGTH_SHORT).show();
        }
    }
}
