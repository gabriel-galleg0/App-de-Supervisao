package Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appjava.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LojaAdapter extends RecyclerView.Adapter<LojaAdapter.LojaViewHolder> implements Filterable {

    private Context context;
    private List<JSONObject> lojaList;
    private List<JSONObject> lojaListFull; // Lista completa para backup durante a filtragem

    public LojaAdapter(Context context, List<JSONObject> lojaList) {
        this.context = context;
        this.lojaList = lojaList;
        this.lojaListFull = new ArrayList<>(lojaList); // Cópia da lista original
    }

    @NonNull
    @Override
    public LojaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_loja, parent, false);
        return new LojaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LojaViewHolder holder, int position) {
        JSONObject loja = lojaList.get(position);
        holder.bind(loja);
    }

    @Override
    public int getItemCount() {
        return lojaList.size();
    }

    @Override
    public Filter getFilter() {
        return lojaFilter;
    }

    private Filter lojaFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<JSONObject> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(lojaListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (JSONObject loja : lojaListFull) {
                    try {
                        String razaoSocial = loja.getString("RAZAO SOCIAL").toLowerCase();
                        String rua = loja.getString("RUA").toLowerCase();
                        String n = loja.getString("N").toLowerCase();
                        String b = loja.getString("BAIRRO").toLowerCase();

                        if(b.contains(filterPattern)){
                            filteredList.add(loja);
                        }

                        if(n.contains(filterPattern)){
                            filteredList.add(loja);
                        }

                        if(rua.contains(filterPattern)){
                            filteredList.add(loja);
                        }
                        if (razaoSocial.contains(filterPattern)) {
                            filteredList.add(loja);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            lojaList.clear();
            lojaList.addAll((List<JSONObject>) results.values);
            notifyDataSetChanged();
        }
    };

    class LojaViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNomeLoja;
        private TextView textViewRuaLoja;
        private TextView textViewNumeroLoja;

        LojaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNomeLoja = itemView.findViewById(R.id.lojaNome);
            textViewRuaLoja = itemView.findViewById(R.id.ruaLojas);
            textViewNumeroLoja = itemView.findViewById(R.id.numeroLojas);
        }

        void bind(JSONObject loja) {
            try {
                String razaoSocial = loja.getString("RAZAO SOCIAL");
                String rua = loja.getString("RUA");
                String numero = loja.getString("N");

                textViewNumeroLoja.setText("N°" + numero);
                textViewNomeLoja.setText(razaoSocial);
                textViewRuaLoja.setText(rua);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
