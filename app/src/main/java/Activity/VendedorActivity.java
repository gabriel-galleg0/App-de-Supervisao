package Activity;

import com.example.appjava.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import Model.Usuario;
import Util.ConfigDb;

public class VendedorActivity extends AppCompatActivity {
    Button botaoEntrarVendedor, verficicacaoTel;
    EditText telefone, verificacaocd;
    private FirebaseAuth autenticacao;
    private String verificacaoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor);
        autenticacao = ConfigDb.autenticacao();
        EdgeToEdge.enable(this);
        inicializar();
        /**
         * Listenner do botão para logar
         */
        botaoEntrarVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(verificacaocd.getText().toString())){
                    Toast.makeText(VendedorActivity.this, "Código errado", Toast.LENGTH_SHORT).show();
                }
                verifycode(verificacaocd.getText().toString().toString());
            }
        });

        verficicacaoTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(telefone.getText().toString())){
                 Toast.makeText(VendedorActivity.this, "Preencha o telefone", Toast.LENGTH_SHORT).show();
                }
                else{
                    String numero = telefone.getText().toString();
                    sendVerificationCode(numero);
                }
            }
        });
    }

    private void sendVerificationCode(String telefone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(autenticacao)
                        .setPhoneNumber("+55" + telefone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
        final String codigo = credential.getSmsCode();

            if (codigo != null) {
                verifycode(codigo);
            }
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
        Toast.makeText(VendedorActivity.this, "Código de Validação Inválido", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCodeSent(@NonNull String s,
                @NonNull PhoneAuthProvider.ForceResendingToken token)
        {
            super.onCodeSent(s, token);
            verificacaoid = s;
        }
    };

    private void verifycode(String codigo) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificacaoid, codigo);
        logarCredenciais(credential);
    }

    private void logarCredenciais(PhoneAuthCredential credential)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(VendedorActivity.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                    login();
                }
            }
            });
        }



    /**
     * Método para inicializar os compontentes
     */
    private void inicializar() {
        verficicacaoTel = findViewById(R.id.botaoVerificacaoTel);
        telefone = findViewById(R.id.telefone);
        verificacaocd = findViewById(R.id.verificacao);
        botaoEntrarVendedor = findViewById(R.id.botaoEntrarVendedor);
        autenticacao = FirebaseAuth.getInstance();
    }
    /**
     * Método que altera de tela quando o login for feito com sucessoq
     */
    public void login() {
        Intent intent = new Intent(this, SelecionarLojasVendedorActivity.class);
        startActivity(intent);
    }
    /**
     * Método que confere se os dados enviados são certos quanto ao dados de login
     * @param
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioLogado = FirebaseAuth.getInstance().getCurrentUser();
        if(usuarioLogado != null){
            startActivity(new Intent(this, SelecionarLojasVendedorActivity.class));
            finish();
        }
    }
}