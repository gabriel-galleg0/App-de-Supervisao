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

public class LojaAdapter extends RecyclerView.Adapter<LojaAdapter.LojaViewHolder> implements Filterable {

    private Context context;
    private List<String> lojaList;
    private List<String> lojaListFull; // Lista completa para backup durante a filtragem

    public LojaAdapter(Context context, List<String> lojaList) {
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
        String loja = lojaList.get(position);
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
            List<String> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(lojaListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (String loja : lojaListFull) {
                    if (loja.toLowerCase().contains(filterPattern)) {
                        filteredList.add(loja);
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
            lojaList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class LojaViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewLoja;

        LojaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLoja = itemView.findViewById(R.id.lojaNome);
        }

        void bind(String loja) {
            textViewLoja.setText(loja);
        }
    }
}
