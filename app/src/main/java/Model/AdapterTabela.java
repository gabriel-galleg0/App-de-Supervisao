package Model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appjava.R;

import java.util.List;

public class AdapterTabela extends RecyclerView.Adapter<AdapterTabela.TableViewHolder> {
    private List<String> nomesArquivos;

    public AdapterTabela(List<String> nomesArquivos) {
        this.nomesArquivos = nomesArquivos;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_gerente_visao, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
       String nomeArquivo = nomesArquivos.get(position);
       holder.txtFunc.setText("Auditor");
       holder.txtNome.setText("Nome do Auditor");
       holder.txtPDV.setText("PDV");
       holder.txtSolicitado.setText("Manutenção");
       holder.txtSituacao.setText("Pendente");

    }

    @Override
    public int getItemCount() {
        return nomesArquivos.size();
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView txtFunc, txtNome, txtPDV, txtSolicitado, txtSituacao;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFunc = itemView.findViewById(R.id.txtFunc);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtPDV = itemView.findViewById(R.id.txtPDV);
            txtSolicitado = itemView.findViewById(R.id.txtSolicitado);
            txtSituacao = itemView.findViewById(R.id.txtSituacao);

        }
    }
}
