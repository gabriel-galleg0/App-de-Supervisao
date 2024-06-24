package Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appjava.R;

import java.util.ArrayList;
import java.util.List;

public class LojaRecyclerAdapter extends RecyclerView.Adapter<LojaRecyclerAdapter.LojaViewHolder> {

    private Context context;
    private List<String> lojaList;
    private List<String> lojaListFull; // Lista completa para restauração após filtrar

    public LojaRecyclerAdapter(Context context, List<String> lojaList) {
        this.context = context;
        this.lojaList = lojaList;
        this.lojaListFull = new ArrayList<>(lojaList);
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
        holder.lojaName.setText(loja);
    }

    @Override
    public int getItemCount() {
        return lojaList.size();
    }

    public void filter(String text) {
        lojaList.clear();
        if (text.isEmpty()) {
            lojaList.addAll(lojaListFull);
        } else {
            text = text.toLowerCase();
            for (String loja : lojaListFull) {
                if (loja.toLowerCase().contains(text)) {
                    lojaList.add(loja);
                }
            }
        }
        notifyDataSetChanged();
    }

    class LojaViewHolder extends RecyclerView.ViewHolder {
        TextView lojaName;

        LojaViewHolder(View itemView) {
            super(itemView);
            lojaName = itemView.findViewById(R.id.lojaNome);
        }
    }
}