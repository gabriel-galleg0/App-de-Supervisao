package Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appjava.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;




import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;

public class LoginAuditorActivity extends AppCompatActivity {
    private Bitmap imageBitmap;

    private FloatingActionButton botaoCameraManutencao;
    private ImageView imageView;
    private FloatingActionButton captureButton;
    private ActivityResultLauncher<Intent> cameraLauncherInvasao;
    private ActivityResultLauncher<Intent> cameraLauncherManutencao;
    private ActivityResultLauncher<Intent> cameraLauncherLimpeza;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private CheckBox checkBoxInvasao;
    private CheckBox checkBoxManutencao;
    private CheckBox checkBoxLimpeza;
    private Button salvarInvasao;
    private ImageView imageViewManutencao;
    private Button botaoEnviar;
    private Button salvarManutencao;
    private Button salvarLimpeza;
    private ImageView fotoTiradaLimpeza;
    private FloatingActionButton botaoCameraLimpeza;

    private Uri uriLimpeza;
    private Uri uriManutencao;
    private Uri uriInvasao;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);
        FirebaseApp.initializeApp(this);


        fotoTiradaLimpeza = findViewById(R.id.fotoTiradaLimpeza);
        salvarLimpeza = findViewById(R.id.salvarLimpeza);
        salvarManutencao = findViewById(R.id.salvarManutencao);
        salvarInvasao = findViewById(R.id.salvarInvasao);
        botaoCameraManutencao = findViewById(R.id.botaoCameraManu);
        imageViewManutencao = findViewById(R.id.fotoTiradaManutencao);
        imageView = findViewById(R.id.fototiradaInvasao);
        captureButton = findViewById(R.id.botaoCamera);
        checkBoxInvasao = findViewById(R.id.checkBoxInvasão);
        checkBoxManutencao = findViewById(R.id.checkBoxManutencao);
        checkBoxLimpeza = findViewById(R.id.checkBoxLimpeza);
        botaoEnviar = findViewById(R.id.botaoEnviar);
        botaoCameraLimpeza = findViewById(R.id.botaoCameraLimpeza);


        botaoEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarImagens();
            }
        });


        salvarInvasao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                imageView.setVisibility(View.GONE);
                captureButton.setVisibility(View.GONE);



                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
                layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.default_top_margin);
                imageView.setLayoutParams(layoutParams);
                moveViewsUp();

                checkBoxInvasao.setChecked(true);
                salvarInvasao.setVisibility(View.GONE);

            }
        });

        salvarManutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageViewManutencao.setVisibility(View.GONE);
                botaoCameraManutencao.setVisibility(View.GONE);

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageViewManutencao.getLayoutParams();
                layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.default_top_margin);
                imageViewManutencao.setLayoutParams(layoutParams);
                moveViewsUpManutencao();

                checkBoxManutencao.setChecked(true);
                salvarManutencao.setVisibility(View.GONE);

            }
        });

        salvarLimpeza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fotoTiradaLimpeza.setVisibility(View.GONE);
                botaoCameraLimpeza.setVisibility(View.GONE);

                moveViewUpLimpeza();

                salvarLimpeza.setVisibility(View.GONE);

            }
        });


/**
 * Listener do checkBox do formulário de invasão
 */
        checkBoxInvasao.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                salvarInvasao.setVisibility(View.VISIBLE);
                captureButton.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
                layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.space_below_checkbox);
                imageView.setLayoutParams(layoutParams);
                moveViewsDown();
            } else {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
                layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.default_top_margin);
                imageView.setLayoutParams(layoutParams);
                moveViewsUp();
                salvarInvasao.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                captureButton.setVisibility(View.GONE);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            initializeCameraInvasao();
            initializeCameraManutencao();
            inicializarCameraLimpeza();
        }


        /**
         * Listener do checkBox do formulário de manutenção
         */
        checkBoxManutencao.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                salvarManutencao.setVisibility(View.VISIBLE);
                botaoCameraManutencao.setVisibility(View.VISIBLE);
                imageViewManutencao.setVisibility(View.VISIBLE);

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageViewManutencao.getLayoutParams();
                layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.espaco_manutencaoimg);
                imageViewManutencao.setLayoutParams(layoutParams);
                moveDownManutencao();

            } else {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageViewManutencao.getLayoutParams();
                layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.espaco_manutencaoimg);
                imageViewManutencao.setLayoutParams(layoutParams);
                moveViewsUpManutencao();
                salvarManutencao.setVisibility(View.GONE);
                imageViewManutencao.setVisibility(View.GONE);
                botaoCameraManutencao.setVisibility(View.GONE);
            }
        });


        checkBoxLimpeza.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                salvarLimpeza.setVisibility(View.VISIBLE);
                botaoCameraLimpeza.setVisibility(View.VISIBLE);
                fotoTiradaLimpeza.setVisibility(View.VISIBLE);

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) fotoTiradaLimpeza.getLayoutParams();
                layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.espaco_manutencaoimg);
                fotoTiradaLimpeza.setLayoutParams(layoutParams);
                moveViewsDownLimpeza();

            } else {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) fotoTiradaLimpeza.getLayoutParams();
                layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.espaco_manutencaoimg);
                fotoTiradaLimpeza.setLayoutParams(layoutParams);
                moveViewUpLimpeza();
                salvarLimpeza.setVisibility(View.GONE);
                fotoTiradaLimpeza.setVisibility(View.GONE);
                botaoCameraLimpeza.setVisibility(View.GONE);
            }
        });
    }


    /**
     * Inicializador da camera para o botão da camera do formulário de invasão
     */
    private void initializeCameraInvasao() {
        cameraLauncherInvasao = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                            imageView.setImageBitmap(imageBitmap);
                        } else {
                            Toast.makeText(LoginAuditorActivity.this, "Falha ao capturar a imagem.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        captureButton.setOnClickListener(view -> takePicture(cameraLauncherInvasao));
    }

    /**
     * Inicializa a Camera para tirar foto da manutenção
     */
    private void initializeCameraManutencao() {
        cameraLauncherManutencao = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                            imageViewManutencao.setImageBitmap(imageBitmap);
                        } else {
                            Toast.makeText(LoginAuditorActivity.this, "Falha ao capturar a imagem.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        botaoCameraManutencao.setOnClickListener(view -> takePicture(cameraLauncherManutencao));
    }

    private void inicializarCameraLimpeza() {
        cameraLauncherLimpeza = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                            fotoTiradaLimpeza.setImageBitmap(imageBitmap);
                        } else {
                            Toast.makeText(LoginAuditorActivity.this, "Falha ao capturar a imagem.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        botaoCameraLimpeza.setOnClickListener(view -> takePicture(cameraLauncherLimpeza));

    }

    private void takePicture(ActivityResultLauncher<Intent> launcher) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            launcher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "Nenhum aplicativo de câmera disponível.", Toast.LENGTH_SHORT).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.principal), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    /**
     * Move para baixo após clicar no checkbox de invasão
     */
    private void moveViewsDown() {
        // Move os itens para baixo
        float spaceBelowCheckbox = getResources().getDimension(R.dimen.espaco_imagem);
        float espacoCamera = getResources().getDimension(R.dimen.espaco_camera);
        float spaceBelowCheckbox2 = getResources().getDimension(R.dimen.espaco_imagem2);
        float espacoFormularios = getResources().getDimension(R.dimen.espacoFormularios);
        float espacoFormulariosLimpeza = getResources().getDimension(R.dimen.espacoFormulariosLimpeza);

        ConstraintLayout.LayoutParams salvarI = (ConstraintLayout.LayoutParams) findViewById(R.id.salvarInvasao).getLayoutParams();
        salvarI.topMargin += espacoCamera;
        findViewById(R.id.salvarInvasao).setLayoutParams(salvarI);

        ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) findViewById(R.id.formularios3).getLayoutParams();
        layoutParams1.topMargin += espacoFormularios;
        findViewById(R.id.formularios3).setLayoutParams(layoutParams1);

        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) findViewById(R.id.formularios4).getLayoutParams();
        layoutParams2.topMargin += espacoFormulariosLimpeza;
        findViewById(R.id.formularios4).setLayoutParams(layoutParams2);

        ConstraintLayout.LayoutParams layoutParams3 = (ConstraintLayout.LayoutParams) findViewById(R.id.checkBoxManutencao).getLayoutParams();
        layoutParams3.topMargin += espacoFormularios;
        findViewById(R.id.checkBoxManutencao).setLayoutParams(layoutParams3);

        ConstraintLayout.LayoutParams layoutParams4 = (ConstraintLayout.LayoutParams) findViewById(R.id.checkBoxLimpeza).getLayoutParams();
        layoutParams4.topMargin += espacoFormulariosLimpeza;
        findViewById(R.id.checkBoxLimpeza).setLayoutParams(layoutParams4);

        ConstraintLayout.LayoutParams layoutParams5 = (ConstraintLayout.LayoutParams) findViewById(R.id.botaoEnviar).getLayoutParams();
        layoutParams5.topMargin += spaceBelowCheckbox;
        findViewById(R.id.botaoEnviar).setLayoutParams(layoutParams5);

        ConstraintLayout.LayoutParams layoutParams6 = (ConstraintLayout.LayoutParams) findViewById(R.id.botaoCamera).getLayoutParams();
        layoutParams6.topMargin += espacoCamera;
        findViewById(R.id.botaoCamera).setLayoutParams(layoutParams6);
    }

    /**
     * Move para baixo após clicar no checkbox de manutenção
     */

    private void moveDownManutencao() {
        float espacoManutencao = getResources().getDimension(R.dimen.espaco_manutencao);
        float espacoCamera = getResources().getDimension(R.dimen.espaco_camera_manutencao);
        float espacoLimpeza = getResources().getDimension(R.dimen.espaco_limpeza);

        ConstraintLayout.LayoutParams salvarM = (ConstraintLayout.LayoutParams) findViewById(R.id.salvarManutencao).getLayoutParams();
        salvarM.topMargin += espacoCamera;
        findViewById(R.id.salvarManutencao).setLayoutParams(salvarM);

        ConstraintLayout.LayoutParams layoutParamsManu1 = (ConstraintLayout.LayoutParams) findViewById(R.id.formularios4).getLayoutParams();
        layoutParamsManu1.topMargin += espacoLimpeza;
        findViewById(R.id.formularios4).setLayoutParams(layoutParamsManu1);

        ConstraintLayout.LayoutParams checkBoxLimpeza = (ConstraintLayout.LayoutParams) findViewById(R.id.checkBoxLimpeza).getLayoutParams();
        checkBoxLimpeza.topMargin += espacoLimpeza;
        findViewById(R.id.checkBoxLimpeza).setLayoutParams(checkBoxLimpeza);

        ConstraintLayout.LayoutParams botaoFormulario = (ConstraintLayout.LayoutParams) findViewById(R.id.botaoEnviar).getLayoutParams();
        botaoFormulario.topMargin += espacoManutencao;
        findViewById(R.id.botaoEnviar).setLayoutParams(botaoFormulario);

        ConstraintLayout.LayoutParams cameraManutencao = (ConstraintLayout.LayoutParams) findViewById(R.id.botaoCameraManu).getLayoutParams();
        cameraManutencao.topMargin += espacoCamera;
        findViewById(R.id.botaoCameraManu).setLayoutParams(cameraManutencao);
    }

    /**
     * Move para baixo após clicar no checkbox de limpeza
     */

    private void moveViewsDownLimpeza() {
        float espacoLimpeza = getResources().getDimension(R.dimen.espacoBotaoenviar);

        ConstraintLayout.LayoutParams botaoEnviar = (ConstraintLayout.LayoutParams) findViewById(R.id.botaoEnviar).getLayoutParams();
        botaoEnviar.topMargin += espacoLimpeza;
        findViewById(R.id.botaoEnviar).setLayoutParams(botaoEnviar);

    }

    /**
     * Sobe o botão enviar Formulário
     */
    private void moveViewUpLimpeza() {
        float espacoLimpeza = getResources().getDimension(R.dimen.espacoBotaoenviar);

        ConstraintLayout.LayoutParams botaoEnviar = (ConstraintLayout.LayoutParams) findViewById(R.id.botaoEnviar).getLayoutParams();
        botaoEnviar.topMargin -= espacoLimpeza;
        findViewById(R.id.botaoEnviar).setLayoutParams(botaoEnviar);
    }

    /**
     * Move para cima após clicar no checkbox de manutenção novamente
     */
    private void moveViewsUpManutencao() {
        float espacoManutencao = getResources().getDimension(R.dimen.espaco_manutencao);
        float espacoCamera = getResources().getDimension(R.dimen.espaco_camera_manutencao);
        float espacoLimpeza = getResources().getDimension(R.dimen.espaco_limpeza);


        ConstraintLayout.LayoutParams salvarM = (ConstraintLayout.LayoutParams) findViewById(R.id.salvarManutencao).getLayoutParams();
        salvarM.topMargin -= espacoCamera;
        findViewById(R.id.salvarManutencao).setLayoutParams(salvarM);

        ConstraintLayout.LayoutParams layoutParamsManu2 = (ConstraintLayout.LayoutParams) findViewById(R.id.formularios4).getLayoutParams();
        layoutParamsManu2.topMargin -= espacoLimpeza;
        findViewById(R.id.formularios4).setLayoutParams(layoutParamsManu2);

        ConstraintLayout.LayoutParams checkBoxLimpeza = (ConstraintLayout.LayoutParams) findViewById(R.id.checkBoxLimpeza).getLayoutParams();
        checkBoxLimpeza.topMargin -= espacoLimpeza;
        findViewById(R.id.checkBoxLimpeza).setLayoutParams(checkBoxLimpeza);

        ConstraintLayout.LayoutParams botaoFormulario = (ConstraintLayout.LayoutParams) findViewById(R.id.botaoEnviar).getLayoutParams();
        botaoFormulario.topMargin -= espacoManutencao;
        findViewById(R.id.botaoEnviar).setLayoutParams(botaoFormulario);

        ConstraintLayout.LayoutParams cameraManutencao = (ConstraintLayout.LayoutParams) findViewById(R.id.botaoCameraManu).getLayoutParams();
        cameraManutencao.topMargin -= espacoCamera;
        findViewById(R.id.botaoCameraManu).setLayoutParams(cameraManutencao);
    }

    /**
     * Move para cima após clicar novamente no checkbox de invasão
     */
    private void moveViewsUp() {
        // Move os itens de volta para a posição original
        float defaultTopMargin2 = getResources().getDimension(R.dimen.default_top_margin4);
        float defaultTopMargin = getResources().getDimension(R.dimen.default_top_margin3);
        float espacoCamera = getResources().getDimension(R.dimen.espaco_camera);
        float espacoFormularios = getResources().getDimension(R.dimen.espacoFormularios);
        float espacoFormulariosLimpeza = getResources().getDimension(R.dimen.espacoFormulariosLimpeza);

        // Ajusta a margem superior dos outros itens de volta para a posição original
        ConstraintLayout.LayoutParams salvarI = (ConstraintLayout.LayoutParams) findViewById(R.id.salvarInvasao).getLayoutParams();
        salvarI.topMargin -= espacoCamera;
        findViewById(R.id.salvarInvasao).setLayoutParams(salvarI);

        ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) findViewById(R.id.formularios3).getLayoutParams();
        layoutParams1.topMargin -= espacoFormularios;
        findViewById(R.id.formularios3).setLayoutParams(layoutParams1);

        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) findViewById(R.id.formularios4).getLayoutParams();
        layoutParams2.topMargin -= espacoFormulariosLimpeza;
        findViewById(R.id.formularios4).setLayoutParams(layoutParams2);

        ConstraintLayout.LayoutParams layoutParams3 = (ConstraintLayout.LayoutParams) findViewById(R.id.checkBoxManutencao).getLayoutParams();
        layoutParams3.topMargin -= espacoFormularios;
        findViewById(R.id.checkBoxManutencao).setLayoutParams(layoutParams3);

        ConstraintLayout.LayoutParams layoutParams4 = (ConstraintLayout.LayoutParams) findViewById(R.id.checkBoxLimpeza).getLayoutParams();
        layoutParams4.topMargin -= espacoFormulariosLimpeza;
        findViewById(R.id.checkBoxLimpeza).setLayoutParams(layoutParams4);

        ConstraintLayout.LayoutParams layoutParams5 = (ConstraintLayout.LayoutParams) findViewById(R.id.botaoEnviar).getLayoutParams();
        layoutParams5.topMargin -= defaultTopMargin;
        findViewById(R.id.botaoEnviar).setLayoutParams(layoutParams5);

        ConstraintLayout.LayoutParams layoutParams6 = (ConstraintLayout.LayoutParams) findViewById(R.id.botaoCamera).getLayoutParams();
        layoutParams6.topMargin -= espacoCamera;
        findViewById(R.id.botaoCamera).setLayoutParams(layoutParams6);
    }

    private void salvarImagens() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://appjava1-2968b.appspot.com");

        // Verifique se a imagem de invasão está presente e envie para o Firebase
        if (imageView.getDrawable() != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                salvarImagemNoFirebase(bitmap, "Invasao", storageRef);
            } else {
                // Lide com o caso em que o Drawable não é um BitmapDrawable
            }
        }

        // Verifique se a imagem de manutenção está presente e envie para o Firebase
        if (imageViewManutencao.getDrawable() != null) {
            Drawable drawable = imageViewManutencao.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                salvarImagemNoFirebase(bitmap, "Manutencao", storageRef);
            } else {
                // Lide com o caso em que o Drawable não é um BitmapDrawable
            }
        }

        // Verifique se a imagem de limpeza está presente e envie para o Firebase
        if (fotoTiradaLimpeza.getDrawable() != null) {
            Drawable drawable = fotoTiradaLimpeza.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                salvarImagemNoFirebase(bitmap, "limpeza", storageRef);
            } else {
                // Lide com o caso em que o Drawable não é um BitmapDrawable
            }
        }
    }


    private void salvarImagemNoFirebase(Bitmap bitmap, String tipo, StorageReference storageRef) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String path = "imagens/" + tipo + "_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageref = storageRef.child(path);

        UploadTask uploadTask = imageref.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> imageref.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                Toast.makeText(LoginAuditorActivity.this, "Imagem salva com sucesso!", Toast.LENGTH_SHORT).show();
                // Salve o downloadUri no Firebase Database ou execute qualquer outra ação necessária
            } else {
                Toast.makeText(LoginAuditorActivity.this, "Falha ao obter o URL de download.", Toast.LENGTH_SHORT).show();
            }
        })).addOnFailureListener(e -> Toast.makeText(LoginAuditorActivity.this, "Falha ao fazer o upload da imagem.", Toast.LENGTH_SHORT).show());
    }


}
