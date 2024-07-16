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
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private boolean exibirProblema; // Flag para controlar qual informação exibir

    public AdapterTabela(List<String> nomesArquivos, boolean exibirProblema) {
        this.nomesArquivos = nomesArquivos;
        this.exibirProblema = true;
    }
    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, parent, false);
            return new TableViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_tabela, parent, false);
            return new TableViewHolder(view);
        }
    }
    public void setExibirProblema(boolean exibirProblema){
        this.exibirProblema = exibirProblema;
    }
    public boolean getExibirProblema(){
        return exibirProblema;
    }
    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            // Configurações para o header, se houver
        } else {
            TableViewHolder tableViewHolder = (TableViewHolder) holder;

            String nomeArquivo = nomesArquivos.get(position - 1);

            if (nomeArquivo.endsWith(".jpg")) {
                nomeArquivo = nomeArquivo.substring(0, nomeArquivo.length() - 4);
            }

            String[] partes = nomeArquivo.split("_");
            if (exibirProblema) {
                // Configurações para o problema
                if (partes.length == 3) {
                    tableViewHolder.txtFunc.setText("Auditor");
                    tableViewHolder.txtSolicitado.setText(partes[0]);
                    tableViewHolder.txtPDV.setText(partes[1]);

                    String auditorNome = nomeAuditor(partes[2]);
                    tableViewHolder.txtNome.setText(auditorNome);
                } else {
                    tableViewHolder.txtFunc.setText("Auditor");
                    tableViewHolder.txtPDV.setText("PDV");
                    tableViewHolder.txtSolicitado.setText("Manutenção");
                    tableViewHolder.txtSituacao.setText("Pendente");
                }
            }
            else {
                // Configurações para a solução
                if (partes.length >= 4) {
                    String nomeVendedor = partes[2];
                    tableViewHolder.txtNome.setText(nomeVendedor);
                    tableViewHolder.txtFunc.setText("Vendedor");
                    tableViewHolder.txtSolicitado.setText(partes[1]);
                    tableViewHolder.txtPDV.setText(partes[0]);


                    tableViewHolder.txtSituacao.setText("Solucionada");
                }else{
                    tableViewHolder.txtFunc.setText("Vendedor");
                    tableViewHolder.txtPDV.setText("PDV");
                    tableViewHolder.txtSolicitado.setText("Nada");
                }
            }
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }
    @Override
    public int getItemCount() {
        return nomesArquivos.size();
    }
    private String nomeAuditor(String uid) {
        switch (uid) {
            case "NXOit8lkyiVilzvwZOPkdZDwrwE2":
                return "Patricia";

            case "EsNWDTZXGKOFJrFnYmL5YoOK5Mi1":
                return "Vania";

            case "GWTtR70qTdXYLPImuIVrkLazCmR2":
                return "Meridiana";

            case "BCfeZjvx7hPcaRymJkQnvPw60ju2":
                return "Luiz Carlos";

            case "Vtjqx0KJO6RZTySSECr6epRAjsq2":
                return "Jessica";

            default:
                return "Nome do Auditor";
        }
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