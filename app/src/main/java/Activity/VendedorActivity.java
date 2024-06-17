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


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import Model.Usuario;
import Util.ConfigDb;

public class VendedorActivity extends AppCompatActivity {

    Button botaoEntrarVendedor;
    EditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor);
        autenticacao = ConfigDb.autenticacao();

        inicializar();

        /**
         * Listenner do botão para logar
         */
        botaoEntrarVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logarUsuario(v);
            }
        });
    }

    /**
     * Método para inicializar os compontentes
     */
    private void inicializar() {
        campoEmail = findViewById(R.id.email);
        campoSenha = findViewById(R.id.Senha);
        botaoEntrarVendedor = findViewById(R.id.botaoEntrarVendedor);
    }

    /**
     * Método que altera de tela quando o login for feito com sucessoq
     */
    public void login() {
        Intent intent = new Intent(this, SelecionarLojasVendedorActivity.class);
        startActivity(intent);
    }

    /**
     * Inicializador de compontentes
     */
    private void incicializar(){
        campoEmail = findViewById(R.id.email);
        campoSenha = findViewById(R.id.Senha);
        botaoEntrarVendedor = findViewById(R.id.botaoEntrar);

    }

    /**
     * Método que confere se os dados enviados são certos quanto ao dados de login
     * @param
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

                /**
                 * Tratamento de erro, esse verifica se a senha está vazia ou não
                 */
            }else {
                Toast.makeText(this, "Preencha a senha", Toast.LENGTH_SHORT).show();

            }
            /**
             * Tratamento de erro, esse verifica se o email está vazio ou não
             */

        }else{
            Toast.makeText(this, "Preencha o email", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Método que faz o login do usuário com o email e a senha
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
                    Toast.makeText(VendedorActivity.this, execao, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}
