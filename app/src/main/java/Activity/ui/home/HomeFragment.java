package Activity.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appjava.R;
import com.example.appjava.databinding.FragmentHomeBinding;
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

public class HomeFragment extends Fragment {

    private Button btnAuditor;
    private FragmentHomeBinding binding;
    private TextView apresentacao;
    private RecyclerView recyclerView;
    private AdapterTabela tableAdapter;
    private List<String> nomesArquivos;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicialize as variáveis e configure o RecyclerView
        apresentacao = binding.apresentacao;
        btnAuditor = binding.btnSelcAuditor;
        recyclerView = binding.recyclerTabela;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        nomesArquivos = new ArrayList<>();
        tableAdapter = new AdapterTabela(nomesArquivos);
        recyclerView.setAdapter(tableAdapter);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();

        recuperaNomesArquivos();
        mostraNomeUsuario();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void mostraNomeUsuario() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                String nomeUsuario = email.split("@")[0];
                if (nomeUsuario.length() > 2) {
                    nomeUsuario = nomeUsuario.substring(0, nomeUsuario.length() - 2);
                }

                nomeUsuario = nomeUsuario.substring(0, 1).toUpperCase() + nomeUsuario.substring(1).toLowerCase();
                apresentacao.setText("Boas Vindas " + nomeUsuario);
            }
        }
    }

    private void recuperaNomesArquivos() {
        StorageReference arquivosRef = storageReference.child("imagensProblema/");

        arquivosRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    String nomeArquivo = item.getName();

                    nomesArquivos.add(nomeArquivo);
                }

                tableAdapter.notifyDataSetChanged();
            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("HomeFragment", "Erro ao recuperar arquivos", e);
            }
        });
    }
}
