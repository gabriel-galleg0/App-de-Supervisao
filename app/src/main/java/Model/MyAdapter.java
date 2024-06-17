package Model;

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
    private ViewHolder ultimoHolder;

    public MyAdapter(List<ImageItem> items, Context context, ActivityResultLauncher<Intent> cameraLauncher) {
        this.items = items;
        this.context = context;
        this.cameraLauncher = cameraLauncher;
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

            StorageReference storageRef = imageItem.getStorageReference();
            if (storageRef != null && holder.itemView.getContext() != null) {
                Glide.with(holder.itemView.getContext()).load(storageRef).into(holder.imageView);
            }
        }

        holder.botaoCamera.setOnClickListener(v -> {
            ultimoHolder = holder;
            tirarFoto();
        });

        holder.botaoSalvar.setOnClickListener(v -> {
            if (holder.fotoNova.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) holder.fotoNova.getDrawable()).getBitmap();
                uploadImageToFirebase(bitmap, imageItem);
            } else {
                Toast.makeText(context, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pendenciaTextView;
        ImageView imageView;
        FloatingActionButton botaoCamera;
        Button botaoSalvar;
        ImageView fotoNova;

        public ViewHolder(View itemView) {
            super(itemView);
            pendenciaTextView = itemView.findViewById(R.id.recPendendias);
            imageView = itemView.findViewById(R.id.recAntesFoto);
            botaoCamera = itemView.findViewById(R.id.recFoto);
            botaoSalvar = itemView.findViewById(R.id.recSalvar);
            fotoNova = itemView.findViewById(R.id.recNova);
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
        StorageReference imagesRef = storageRef.child(imageItem.getNomePdv() + "_" + imageItem.getPendencia() + ".jpg");

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
