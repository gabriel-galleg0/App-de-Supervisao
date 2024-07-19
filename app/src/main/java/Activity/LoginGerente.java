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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

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
        setContentView(R.layout.login_gerente); //xml do layout
        EdgeToEdge.enable(this); // Método que deixa o layout em fullscreen

        autenticacao = ConfigDb.autenticacao();
        inicializar();

        botaoEntrarGerente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logarGerente(v);
            }
        });
    }

    /**
     * Método de inincialização de variáveis
     */
    private void inicializar() {
        botaoEntrarGerente = findViewById(R.id.botaoEntrarGerente);
        campoEmail = findViewById(R.id.email);
        layoutSenha = findViewById(R.id.text_input_senha);
        digitaSenha = layoutSenha.findViewById(R.id.senha_edit_text);

    }

    /**
     * Método para quando o usuário logar com o email e senha ele irá ser direcionado para a activity do GerenteVisao
     */
    private void login() {
        Intent intent = new Intent(this, NavigationDrawer.class);
        startActivity(intent);
        finish();
    }

    /**
     * Método para logar usuarios que possuem @supervisao.com no email somente
     *
     * @param view
     */
    private void logarGerente(View view) {
        String email = campoEmail.getText().toString();
        String senha = digitaSenha.getText().toString();


        if (!email.isEmpty()) {
            if (email.endsWith("@supervisao.com")) { // if para verificar se o email tem @supervisao.com
                if (!senha.isEmpty()) {
                    Usuario usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    logar(usuario);
                } else {
                    Toast.makeText(this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Somente Supervisores possuem acesso", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método para logar com email e senha
     *
     * @param usuario
     */
    private void logar(Usuario usuario) {
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    salvaLogin("emailPassword");
                    login();
                    FirebaseUser firebaseUser = autenticacao.getCurrentUser();
                    if(firebaseUser != null){
                        salvarToken(firebaseUser);
                    }
                } else {
                    String execao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {

                        execao = "Usuário não cadastrado";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        execao = "Email ou senha incorretos";
                    } catch (Exception e) {
                        execao = "Erro ao logar" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginGerente.this, execao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void salvarToken(FirebaseUser firebaseUser){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(LoginGerente.this, "Não foi possivel obter o token", Toast.LENGTH_SHORT).show();
                    return;
                }
                String token = task.getResult();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                databaseReference.child("fcmToken").setValue(token);
                databaseReference.child("phoneNumber").setValue(null);
                databaseReference.child("smsVerified").setValue(true);
                login();
            }
        });
    }

    /**
     * Salva qual foi o método de login do usuário
     *
     * @param metodo
     */
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioLogado = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioLogado != null) {
            String email = usuarioLogado.getEmail();
            if (email != null && "emailPassword".equals(getMethod())) {
                if (email.endsWith("@supervisao.com")) {
                    startActivity(new Intent(this, NavigationDrawer.class));
                    finish();
                } else {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                }
            } else {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        }
    }
}