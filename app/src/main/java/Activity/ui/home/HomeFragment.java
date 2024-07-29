package Activity.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appjava.R;
import com.example.appjava.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.AdapterTabela;

public class HomeFragment extends Fragment implements OnChartValueSelectedListener {

    private Button btnAuditor, btnVendedor, btnGrafico;
    private FragmentHomeBinding binding;
    private TextView apresentacao, apresentacaoAuditor, explicacao, explicaVendedor;
    private RecyclerView recyclerView;
    private AdapterTabela tableAdapter;
    private List<String> nomesArquivos;
    private FirebaseStorage storage;
    private StorageReference storageReference, storageReferenceB;
    private FirebaseAuth auth;
    private PieChart pieChart;
    private BarChart barChart;
    private boolean exibirProblema = true; // Flag para controlar qual informação exibir

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicialize as variáveis e configure o RecyclerView
        apresentacao = binding.apresentacao;
        apresentacaoAuditor = binding.txtExplicacaoAuditor;
        explicacao = binding.txtExplicacao;
        explicaVendedor = binding.txtExplicacaoVendedor;
        pieChart = binding.graficoPizza;
        barChart= binding.graficoBarra;

        btnGrafico = binding.btnSelcGrafico;
        btnVendedor = binding.btnSelcVendedor;
        btnVendedor.setBackgroundResource(R.drawable.btn_shape_supervisao);

        btnAuditor = binding.btnSelcAuditor;
        btnAuditor.setBackgroundResource(R.drawable.btn_shape_supervisao);
        recyclerView = binding.recyclerTabela;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        nomesArquivos = new ArrayList<>();
        tableAdapter = new AdapterTabela(nomesArquivos, exibirProblema);
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setVisibility(View.GONE);

        pieChart.setVisibility(View.GONE);
        barChart.setVisibility(View.GONE);
        apresentacaoAuditor.setVisibility(View.GONE);
        explicaVendedor.setVisibility(View.GONE);

        btnAuditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barChart.setVisibility(View.GONE);
                pieChart.setVisibility(View.GONE);
                explicacao.setVisibility(View.GONE);
                explicaVendedor.setVisibility(View.GONE);
                apresentacaoAuditor.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                tableAdapter.setExibirProblema(true);
                recuperaNomesArquivos(); // Recupera os nomes de arquivos de "imagensProblema/"

            }
        });


        btnGrafico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barChart.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                apresentacaoAuditor.setVisibility(View.GONE);
                explicaVendedor.setVisibility(View.GONE);
            }
        });
        btnVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barChart.setVisibility(View.GONE);
                pieChart.setVisibility(View.GONE);
                explicacao.setVisibility(View.GONE);
                apresentacaoAuditor.setVisibility(View.GONE);
                explicaVendedor.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                tableAdapter.setExibirProblema(false);
                recuperaNomesArquivos(); // Recupera os nomes de arquivos de "imagensSolucao/"
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        storageReferenceB = storage.getReference();
        auth = FirebaseAuth.getInstance();


        recuperaNomesArquivos(); // Inicialmente, carrega os nomes de arquivos de "imagensProblema/"
        mostraNomeUsuario();
        carregaGrafico();
        carregaBarras();

        barChart.setOnChartValueSelectedListener(this);

        return root;
    }

    private void carregaBarras() {
        storageReferenceB = storage.getReference().child("imagensProblema/");
        storageReferenceB.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Map<String, Integer> categoria = new HashMap<>();
                categoria.put("Invasao", 0);
                categoria.put("Manutencao", 0);
                categoria.put("Limpeza", 0);

                for(StorageReference item:listResult.getItems()){
                    String nomeArquivo = item.getName();
                    if(nomeArquivo.contains("Invasao")){
                        categoria.put("Invasao", categoria.get("Invasao") + 1);
                    }else if (nomeArquivo.contains("Manutencao")){
                        categoria.put("Manutencao", categoria.get("Manutencao") + 1);
                    }else if (nomeArquivo.contains("Limpeza")){
                        categoria.put("Limpeza", categoria.get("Limpeza") + 1);
                    }
                }
                updateBarChart(categoria);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("HomeFragment", "Erro ao recuperar arquivos", e);
            }
        });
    }

    private void updateBarChart(Map<String, Integer> categoria) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int i = 0;
        for(Map.Entry<String, Integer> entry : categoria.entrySet()){
            entries.add(new BarEntry(i, entry.getValue()));
            labels.add(entry.getKey());
            i++;
        }
        BarDataSet dataSet = new BarDataSet(entries, "Categorias");

        int corI = ContextCompat.getColor(requireContext(), R.color.invasao);
        int corL = ContextCompat.getColor(requireContext(), R.color.Limpeza);
        int corM = ContextCompat.getColor(requireContext(), R.color.manutencao);
        int colores[] = {corL, corI, corM};
        dataSet.setColors(colores);

        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.setDescription(null);
        barChart.setFitBars(true);
        barChart.setDrawValueAboveBar(true);
        barChart.setExtraTopOffset(10f);


        Legend legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(10f);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        //Configuração do eixo X
        XAxis xasis = barChart.getXAxis();
        xasis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xasis.setGranularity(1f);
        xasis.setLabelCount(labels.size(), true);
        xasis.setDrawGridLines(false);
        xasis.setDrawLabels(true);
        xasis.setLabelRotationAngle(45f);


        barChart.invalidate();
    }

    private void carregaGrafico() {
        StorageReference  imagensProb = storage.getReference().child("imagensProblema/");
        StorageReference  imagensSolucao = storage.getReference().child("imagensSolução/");

        final int[] problemaCount = {0};
        final int[] solucaoCount = {0};

        imagensProb.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                problemaCount[0] = listResult.getItems().size();
                imagensSolucao.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        solucaoCount[0] = listResult.getItems().size();
                        updatePieChart(problemaCount[0], solucaoCount[0]);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("HomeFragment", "Erro ao recuperar arquivos", e);
                    }
                });
            }
        });
    }

            private void updatePieChart(int problemaCount, int solucaoCount) {
                ArrayList<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry(problemaCount, "Problema"));
                entries.add(new PieEntry(solucaoCount, "Solução"));

                PieDataSet dataSet = new PieDataSet(entries, "Categorias");

                int corP = ContextCompat.getColor(requireContext(), R.color.vermelho);
                int corS = ContextCompat.getColor(requireContext(), R.color.verde);

                int[] colors = {corP, corS};
                dataSet.setColors(colors);

                PieData data = new PieData(dataSet);
                data.setValueTextSize(12f);
                data.setValueTextColor(Color.WHITE);

                //config do gráfico
                pieChart.setData(data);
                pieChart.getDescription().setEnabled(false);
                pieChart.setUsePercentValues(true);
                pieChart.setHoleColor(Color.TRANSPARENT);;

                pieChart.invalidate();
            }

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(e instanceof PieEntry){
                    String categoria = (String) e.getData();
                    Toast.makeText(getContext(), "Categoria: " + categoria, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {}
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
        StorageReference arquivosRef;
        if (tableAdapter != null && tableAdapter.getExibirProblema()) {
            arquivosRef = storageReference.child("imagensProblema/");
        } else {
            arquivosRef = storageReference.child("imagensSolução/");
        }

        arquivosRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                nomesArquivos.clear(); // Limpa a lista atual
                for (StorageReference item : listResult.getItems()) {
                    String nomeArquivo = item.getName();
                    nomesArquivos.add(nomeArquivo);
                }

                tableAdapter.notifyDataSetChanged(); // Notifica o adapter sobre mudanças
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("HomeFragment", "Erro ao recuperar arquivos", e);
            }
        });
    }
}
