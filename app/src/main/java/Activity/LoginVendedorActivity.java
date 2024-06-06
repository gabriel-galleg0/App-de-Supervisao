package Activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appjava.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

public class LoginVendedorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_vendedor);

        TextView textView = findViewById(R.id.lojasTeste);

        // URL do seu arquivo JSON no Firebase Storage
        String urlDoArquivoJson = "gs://appjava1-2968b.appspot.com/Lojas/loja.json";

        // Crie uma referência para o arquivo no Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(urlDoArquivoJson);

        // Baixe o arquivo JSON e leia seus dados
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            // Converta os bytes em uma string JSON
            String json = new String(bytes, Charset.defaultCharset());

            // Parseie o JSON e extraia os dados necessários
            try {
                JSONObject jsonObject = new JSONObject(json);

                // Extrair as lojas do objeto "lojas"
                JSONObject lojasObject = jsonObject.getJSONObject("lojas");

                // Extrair os dados das lojas
                JSONObject loja1Object = lojasObject.getJSONObject("loja1");
                String nomeLoja1 = loja1Object.getString("nome");
                String cepLoja1 = loja1Object.getString("cep");

                JSONObject loja2Object = lojasObject.getJSONObject("loja2");
                String nomeLoja2 = loja2Object.getString("nome");
                String cepLoja2 = loja2Object.getString("cep");

                // Exiba os dados no TextView
                textView.setText("Nome Loja 1: " + nomeLoja1 + "\nCEP Loja 1: " + cepLoja1 +
                        "\n\nNome Loja 2: " + nomeLoja2 + "\nCEP Loja 2: " + cepLoja2);
            } catch (JSONException e) {
                // Lidar com qualquer erro ao analisar o JSON
                e.printStackTrace();
            }
        }).addOnFailureListener(exception -> {
            // Lidar com falhas ao baixar o arquivo
            Toast.makeText(getApplicationContext(), "Falha ao baixar o arquivo JSON", Toast.LENGTH_SHORT).show();
        });

        // Definir o listener para aplicar as margens conforme as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
