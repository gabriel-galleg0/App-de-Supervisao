package Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appjava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import Model.Usuario;
import Util.ConfigDb;

public class LoginGerente extends AppCompatActivity {

    TextInputLayout layoutSenha;
    TextInputEditText digitaSenha;
    EditText campoEmail;
    Button botaoEntrarGerente;
    FirebaseAuth autenticacao;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_gerente);
        EdgeToEdge.enable(this);

        autenticacao = ConfigDb.autenticacao();
        inicializar();

        botaoEntrarGerente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                logarGerente(v);
            }
        });
    }

    private void inicializar() {
        botaoEntrarGerente = findViewById(R.id.botaoEntrarGerente);
        campoEmail = findViewById(R.id.email);
        layoutSenha = findViewById(R.id.text_input_senha);
        digitaSenha = layoutSenha.findViewById(R.id.senha_edit_text);

    }

    private void login(){
        Intent intent = new Intent(this, GerenteVisao.class);
        startActivity(intent);
        finish();
    }

    private void logarGerente(View view){
        String email = campoEmail.getText().toString();
        String senha = digitaSenha.getText().toString();


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

    private void logar(Usuario usuario) {
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    salvaLogin("emailPassword");
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
                    Toast.makeText(LoginGerente.this, execao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void salvaLogin(String metodo) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("login_method", metodo);
        editor.apply();
    }

    private String getMethod() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return preferences.getString("login_method", "");
    }
}
