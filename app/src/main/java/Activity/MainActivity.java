package Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appjava.R;

import Activity.AuditorActivity;
import Activity.VendedorActivity;

public class MainActivity extends AppCompatActivity {
    Button entrarVendedor;
    Button entrarAuditor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        /**
         * Instanciando os botões pelo ID
         */
        entrarAuditor = findViewById(R.id.entrarAuditor);
        entrarVendedor = findViewById(R.id.entrarVendedor);

        /**
         * Ação do Botão vendedor para trocar de tela
         */
        entrarVendedor.setOnClickListener(v -> {
            startActivity(new Intent(this, VendedorActivity.class));
        });


        /**
         * Ação do Botão auditor para trocar de tela
         */
        entrarAuditor.setOnClickListener(v -> {
            startActivity(new Intent(this, AuditorActivity.class));
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.principal), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }





}