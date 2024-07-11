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

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerente_visao);
        EdgeToEdge.enable(this);

        apresentacao = findViewById(R.id.apresentacao);
        recyclerView = findViewById(R.id.recyclerTabela);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        nomesArquivos = new ArrayList<>();
        tableAdapter = new AdapterTabela(nomesArquivos);
        recyclerView.setAdapter(tableAdapter);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recuperaNomesArquivos();

    }

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


