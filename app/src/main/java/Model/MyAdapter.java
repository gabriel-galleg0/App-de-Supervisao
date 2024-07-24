package Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appjava.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<ImageItem> items;
    private Context context;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ImageView ultimoHolder;
    private FirebaseAuth auth;
    private String numeroTelefoneUsuario = "";
    public MyAdapter(List<ImageItem> items, Context context, ActivityResultLauncher<Intent> cameraLauncher) {
        this.items = items;
        this.context = context;
        this.cameraLauncher = cameraLauncher;
        this.auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!= null && currentUser.getPhoneNumber() != null){
            numeroTelefoneUsuario = currentUser.getPhoneNumber();
        }
    }
    /**
     * Método para obter a referencia do ultimo imageView onde foi tirada a foto
     * @return
     */
    public ImageView getUltimoHolder() {
        return ultimoHolder;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);

    }
    /**
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageItem imageItem = items.get(position);

        if (imageItem != null) {
            holder.pendenciaTextView.setText(imageItem.getPendencia());
            /**
             * Método para carregar imagens do FireBase Storage
             */
            StorageReference storageRef = imageItem.getStorageReference();
            if (storageRef != null) {
                Glide.with(holder.itemView.getContext())
                .load(storageRef)
                .into(holder.imageView);
                Log.d("MyAdapter", "carregando" + storageRef.getPath());
            }
        }
        /**
         * Listener do botão para tirar fotos
         */
        holder.botaoCamera.setOnClickListener(v -> {
            ultimoHolder = holder.recNova; // Guarda a referência ao recNova deste ViewHolder para a imagem ficar salva no ImageView
            tirarFoto();
        });
        /**
         * Listener do botão para salvar as imagens
         */
        holder.botaoSalvar.setOnClickListener(v -> {
            if (ultimoHolder != null) {
                Bitmap bitmap = ((BitmapDrawable) ultimoHolder.getDrawable()).getBitmap();
                uploadImageToFirebase(bitmap, imageItem, holder);
                holder.pgb.setVisibility(View.VISIBLE);
                holder.botaoSalvar.setVisibility(View.GONE);
            } else {
                Toast.makeText(context, "Nenhuma imagem capturada", Toast.LENGTH_SHORT).show();
            }
        });
        /**
         * Listener para ampliar as imagens
         */
        holder.imageView.setOnClickListener(v -> {
            ampliarImagem(holder.imageView);
        });
    }
    /**
     * Método para ampliar as imagens com 90% do tamanho da tela do dispositivo
     * @param imageView
     */
    private void ampliarImagem(ImageView imageView){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ImageView imageViewDialog = new ImageView(context);
        imageViewDialog.setImageBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        imageViewDialog.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageViewDialog.setAdjustViewBounds(true);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        /**
         * Define o tamanho da imageView para dentro do AlertDialog
         */
        builder.setView(imageViewDialog);
        /**
         * Cria uma dialgo com o builder
         */
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**
     * Contagem de itens dentro do firebase para usar no método de itens presentes no viewHolder lá em VendedorAcoes
     * @return
     */
    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
    /**
     * ViewHolder para os itens
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        FloatingActionButton botaoCamera;
        Button botaoSalvar;
        ImageView recNova;
        TextView pendenciaTextView;
        ProgressBar pgb;
        /**
         * Método construtor do ViewHolder
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            pgb = itemView.findViewById(R.id.progress_vendedor);
            pgb.setVisibility(View.GONE);
            imageView = itemView.findViewById(R.id.recAntesFoto);
            botaoCamera = itemView.findViewById(R.id.recFoto);
            botaoSalvar = itemView.findViewById(R.id.recSalvar);
            recNova = itemView.findViewById(R.id.recNova);
            pendenciaTextView = itemView.findViewById(R.id.recPendendias);
        }
    }
    /**
     * Método para tirar fotos
     */
    private void tirarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(context, "Falha ao abrir a câmera", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Método para salvar as imagens no Firebase Storage
     * @param imageBitmap
     * @param imageItem
     */
    private void uploadImageToFirebase(Bitmap imageBitmap, ImageItem imageItem, ViewHolder holder) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        /*
        Pega o uid do usuário que esta enviando a imagem pro firebase
        */
        String caminho =  "imagensSolução/" + imageItem.getNomePdv() + "_" + imageItem.getPendencia()+ "_" + numeroTelefoneUsuario + ".jpg";

        StorageReference imagesRef = storageRef.child(caminho);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        Log.d("MyAdapter", "Caminho " + imagesRef.getPath());
        Log.d("MyAdapter", "Tamanho" + data.length);
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imagesRef.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Imagem salva com sucesso!", Toast.LENGTH_SHORT).show();
                    holder.pgb.setVisibility(View.GONE);
                } else {
                    Toast.makeText(context, "Falha ao obter o URL de download.", Toast.LENGTH_SHORT).show();
                    holder.pgb.setVisibility(View.GONE);
                }
            });
        }).addOnFailureListener(e -> Toast.makeText(context, "Falha ao fazer o upload da imagem.", Toast.LENGTH_SHORT).show());
        holder.pgb.setVisibility(View.GONE);
        holder.botaoSalvar.setVisibility(View.VISIBLE);
    }
}