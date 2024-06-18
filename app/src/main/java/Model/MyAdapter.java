package Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appjava.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ImageItem> items;
    private Context context;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ImageView ultimoHolder; // Referência ao último ImageView onde a imagem foi capturada

    public MyAdapter(List<ImageItem> items, Context context, ActivityResultLauncher<Intent> cameraLauncher) {
        this.items = items;
        this.context = context;
        this.cameraLauncher = cameraLauncher;
    }

    // Método para obter o último ImageView onde a imagem foi capturada
    public ImageView getUltimoHolder() {
        return ultimoHolder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageItem imageItem = items.get(position);

        if (imageItem != null) {
            holder.pendenciaTextView.setText(imageItem.getPendencia());

            // Carregar imagem do Firebase se existir
            StorageReference storageRef = imageItem.getStorageReference();
            if (storageRef != null && holder.itemView.getContext() != null) {
                Glide.with(holder.itemView.getContext()).load(storageRef).into(holder.imageView);
            }
        }

        // Configuração do botão para tirar foto
        holder.botaoCamera.setOnClickListener(v -> {
            ultimoHolder = holder.recNova; // Guarda a referência ao recNova deste ViewHolder
            tirarFoto();
        });

        // Configuração do botão para salvar imagem no Firebase
        holder.botaoSalvar.setOnClickListener(v -> {
            if (ultimoHolder != null) {
                Bitmap bitmap = ((BitmapDrawable) ultimoHolder.getDrawable()).getBitmap();
                uploadImageToFirebase(bitmap, imageItem);
            } else {
                Toast.makeText(context, "Nenhuma imagem capturada", Toast.LENGTH_SHORT).show();
            }
        });

        holder.imageView.setOnClickListener(v -> {
            ampliarImagem(holder.imageView);
        });
    }


    private void ampliarImagem(ImageView imageView){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ImageView imageViewDialog = new ImageView(context);
        imageViewDialog.setImageBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        builder.setView(imageViewDialog);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        FloatingActionButton botaoCamera;
        Button botaoSalvar;
        ImageView recNova;
        TextView pendenciaTextView; // Adicionar TextView para pendencia

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recAntesFoto);
            botaoCamera = itemView.findViewById(R.id.recFoto);
            botaoSalvar = itemView.findViewById(R.id.recSalvar);
            recNova = itemView.findViewById(R.id.recNova);
            pendenciaTextView = itemView.findViewById(R.id.recPendendias); // Inicializar pendenciaTextView
        }
    }

    private void tirarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(context, "Falha ao abrir a câmera", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebase(Bitmap imageBitmap, ImageItem imageItem) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("imagensSolução/" +imageItem.getNomePdv() + "_" + imageItem.getPendencia() + ".jpg");


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imagesRef.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Aqui você pode salvar o downloadUri no Firebase Database ou realizar outras ações necessárias
                    Toast.makeText(context, "Imagem salva com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Falha ao obter o URL de download.", Toast.LENGTH_SHORT).show();
                }
            });
        }).addOnFailureListener(e -> Toast.makeText(context, "Falha ao fazer o upload da imagem.", Toast.LENGTH_SHORT).show());
    }
}
