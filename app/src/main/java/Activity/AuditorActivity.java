package Activity;

import com.example.appjava.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appjava.databinding.ActivityAuditorBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import Model.Usuario;
import Util.ConfigDb;

public class AuditorActivity extends AppCompatActivity {

    private ActivityAuditorBinding binding;
    Button botaoEntrar;
    FirebaseAuth autenticacao;
    EditText campoEmail, campoSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auditor);
        autenticacao = ConfigDb.autenticacao();
        incicializar();
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logarUsuario(v);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.principal), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                }
        );}
    /**
     * Método responsável por navegar para a tela de seleção de lojas
     */
    public void login() {
        Intent intent = new Intent(this, SelecionarLojasActivity.class);
        startActivity(intent);
    }

    /**
     * Método responsável por inicializar os componentes da tela
     */
    private void incicializar(){
        campoEmail = findViewById(R.id.email);
        campoSenha = findViewById(R.id.Senha);
        botaoEntrar = findViewById(R.id.botaoEntrar);
    }
    /**
     * Método responsável por realizar o login do usuário
     * @param view
     */
    private void logarUsuario(View view){
        String email = campoEmail.getText().toString();
        String senha = campoSenha.getText().toString();
        if(!email.isEmpty()){
            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setSenha(senha);
            logar(usuario);
            if(!senha.isEmpty()){
            }else {
                Toast.makeText(this, "Preencha a senha", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Preencha o email", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Método responsável por realizar o login do usuário no Firebase
     * @param usuario
     */
    private void logar(Usuario usuario) {
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    login();
                }else {
                    String execao ="";
                    try {
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){

                        execao = "Usuário não cadastrado";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        execao = "Email ou senha incorretos";
                    }catch (Exception e){
                        execao = "Erro ao logar" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(AuditorActivity.this, execao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}