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

import java.util.ArrayList;
import java.util.List;

public class RegiaoAdapter extends RecyclerView.Adapter<RegiaoAdapter.RegiaoViewHolder> implements Filterable {

    private Context context;
    private List<String> regiaoList;
    private List<String> regiaoListFull; // Lista completa para backup durante a filtragem

    public RegiaoAdapter(Context context, List<String> regiaoList) {
        this.context = context;
        this.regiaoList = regiaoList;
        this.regiaoListFull = new ArrayList<>(regiaoList); // Cópia da lista original
    }

    @NonNull
    @Override
    public RegiaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_regiao, parent, false);
        return new RegiaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegiaoViewHolder holder, int position) {
        String regiao = regiaoList.get(position);
        holder.bind(regiao);
    }

    @Override
    public int getItemCount() {
        return regiaoList.size();
    }

    @Override
    public Filter getFilter() {
        return regiaoFilter;
    }

    private Filter regiaoFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(regiaoListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (String regiao : regiaoListFull) {
                    if (regiao.toLowerCase().contains(filterPattern)) {
                        filteredList.add(regiao);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            regiaoList.clear();
            regiaoList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class RegiaoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewRegiao;

        RegiaoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRegiao = itemView.findViewById(R.id.regiaoNome);
        }

        void bind(String regiao) {
            textViewRegiao.setText(regiao);
        }
    }
}
