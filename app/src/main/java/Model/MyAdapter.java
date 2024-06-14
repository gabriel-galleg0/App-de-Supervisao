package Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.appjava.R;

import com.google.firebase.storage.StorageReference;


import java.util.List;



public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ImageItem> items;
    private Context context;

    public MyAdapter(List<ImageItem> items) {
        this.items = items;
        this.context = context;
    }

    /**
     * Método para criar um novo ViewHolder no RecyclerView.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);

    }

    /**
     * Método para vincular os dados do item ao ViewHolder.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageItem imageItem = items.get(position);

        /**
         * Verifica se a imagem é nula para só dps dessa verificação continuar
         */
        if (imageItem != null) {
            /**
             * Define qual pendencia está presente no banco de Dados
             */
            holder.pendenciaTextView.setText(imageItem.getPendencia());

            /**
             * Carrega as imagens do FireBase Storage
             */
            StorageReference storageRef = imageItem.getStorageReference();

            if (storageRef != null && holder.itemView.getContext() != null) {
                /**
                 * Glide, "funciona" como um cache de imagens para o Android, nessa parte ele carrega a imagem do firebase storage no imageview
                 */
                Glide.with(holder.itemView.getContext())
                        .load(storageRef)
                        .into(holder.imageView);
            }
        }
    }

    /**
     * Método para obter o número total de itens no RecyclerView.
     * @return
     */
    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    /**
     * Classe ViewHolder para armazenar as referências para os elementos de layout do item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView pendenciaTextView;
        ImageView imageView;

        /**
         * Construtor para inicializar as referências para os elementos de layout do item.
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            pendenciaTextView = itemView.findViewById(R.id.recPendendias);
            imageView = itemView.findViewById(R.id.recAntesFoto);
        }
    }
}
