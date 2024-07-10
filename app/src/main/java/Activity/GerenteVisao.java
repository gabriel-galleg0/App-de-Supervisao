package Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appjava.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import Model.AdapterTabela;

public class GerenteVisao extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdapterTabela tableAdapter;
    private List<String> nomesArquivos;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerente_visao);

        recyclerView = findViewById(R.id.recyclerTabela);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        nomesArquivos = new ArrayList<>();
        tableAdapter = new AdapterTabela(nomesArquivos);
        recyclerView.setAdapter(tableAdapter);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recuperaNomesArquivos();

    }

    private void recuperaNomesArquivos() {
        StorageReference arquivosRefS = storageReference.child("imagensSolução/");
        StorageReference arquivosRefP = storageReference.child("imagensProblema/");

        arquivosRefS.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
               for(StorageReference item: listResult.getItems()) {)
            }})
    }


}
