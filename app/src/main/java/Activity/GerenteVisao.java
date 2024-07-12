package Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appjava.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import Model.AdapterTabela;

public class GerenteVisao extends AppCompatActivity {
    TextView apresentacao;
    private RecyclerView recyclerView;
    private AdapterTabela tableAdapter;
    private List<String> nomesArquivos;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth auth;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerente_visao);
        EdgeToEdge.enable(this); //Método de deixar o layout completo no dispositivo.

        /**
         * Deixei sem um método de inincialização pois são poucas coisas que necessitam ser iniciadas.
         * Por isso está tudo aqui embaixo .
         */
        apresentacao = findViewById(R.id.apresentacao);
        recyclerView = findViewById(R.id.recyclerTabela);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        nomesArquivos = new ArrayList<>();
        tableAdapter = new AdapterTabela(nomesArquivos);
        recyclerView.setAdapter(tableAdapter);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();

        recuperaNomesArquivos();
        mostraNomeUsuario();

    }

    /**
     * Método que pega o email do usuário e mostra o nome, no caso corta o email quando chegar no @, e retira os dois ultimos caracteres
     * antes do arroba (@) para deixar apenas o nome do usuário. Por exemplo, todos emails terão a mesma sintaxe, (gabrielgg@supervisao.com)
     * ent vai pegar esses dois útimos caracteres e excluir.
     */
    private void mostraNomeUsuario() {
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            String email = user.getEmail();
            if(email != null){
                String nomeUsuario = email.split("@")[0];
               if(nomeUsuario.length() >2){
                   nomeUsuario = nomeUsuario.substring(0, nomeUsuario.length() - 2);
               }

                nomeUsuario = nomeUsuario.substring(0, 1).toUpperCase() + nomeUsuario.substring(1).toLowerCase();
                apresentacao.setText("Boas Vindas " + nomeUsuario);
            }
        }
    }

    /**
     * Método que recupera os nomes dos arquivos do Firebase Storage.
     */
    private void recuperaNomesArquivos(){
        StorageReference arquivosRef = storageReference.child("imagensProblema/");

        arquivosRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>(){
            @Override
            public void onSuccess(ListResult listResult){
                for(StorageReference item : listResult.getItems()){
                    String nomeArquivo = item.getName();

                    nomesArquivos.add(nomeArquivo);
        }

                tableAdapter.notifyDataSetChanged();
    }

   }).addOnFailureListener(new OnFailureListener(){

       @Override
            public void onFailure(@NonNull Exception e){
           Log.e("GerenteVisao", "Erro ao recuperar arquivos", e);
       }
        });
    }
}


