package Activity;

import com.example.appjava.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

public class VendedorActivity extends AppCompatActivity {
    Button botaoEntrarVendedor, verficicacaoTel;
    EditText telefone ;
    TextInputLayout verificacaoLayout;
    TextInputEditText verificacaocd;
    private FirebaseAuth autenticacao;
    private String verificacaoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor);
        autenticacao = FirebaseAuth.getInstance();
        inicializar();
        EdgeToEdge.enable(this);

        botaoEntrarVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(verificacaocd.getText().toString())) {
                    Toast.makeText(VendedorActivity.this, "Código errado", Toast.LENGTH_SHORT).show();
                } else {
                    verifyCode(verificacaocd.getText().toString());
                }
            }
        });

        verficicacaoTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(telefone.getText().toString())) {
                    Toast.makeText(VendedorActivity.this, "Preencha o telefone", Toast.LENGTH_SHORT).show();
                } else {
                    String numero = telefone.getText().toString();
                    sendVerificationCode(numero);
                }
            }
        });
    }

    private void sendVerificationCode(String telefone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(autenticacao)
                .setPhoneNumber("+55" + telefone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String codigo = credential.getSmsCode();
            if (codigo != null) {
                verifyCode(codigo);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VendedorActivity.this, "Código de Validação Inválido", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verificacaoid = s;
        }
    };

    private void verifyCode(String codigo) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificacaoid, codigo);
        logarCredenciais(credential);
    }

    private void logarCredenciais(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    if (user != null) {
                        salvarMetodoLogin("sms");
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (task.isSuccessful()) {
                                    String token = task.getResult();
                                    saveTokenToDatabase(user.getUid(), token); // Salvar no Realtime Database
                                } else {
                                    Log.e("VendedorActivity", "Não foi possível obter o token FCM", task.getException());
                                }
                            }
                        });
                    }
                    Toast.makeText(VendedorActivity.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                    login();
                } else {
                    Toast.makeText(VendedorActivity.this, "Falha no login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void salvarMetodoLogin(String metodo) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("login_method", metodo);
        editor.apply();
    }

    private String pegarMetodoLogin() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return preferences.getString("login_method", null);
    }

    private void saveTokenToDatabase(String userId, String token) {
        // Referência para o nó 'users' no Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Criar um nó para o usuário com o UID como chave
        DatabaseReference userRef = databaseReference.child(userId);

        // Salvar os dados (telefone, token FCM, etc.) no nó do usuário
        userRef.child("phoneNumber").setValue(telefone.getText().toString()); // Exemplo de salvar o telefone
        userRef.child("fcmToken").setValue(token);
        userRef.child("smsVerified").setValue(true);

        Log.d("VendedorActivity", "Token FCM salvo com sucesso no Realtime Database");
    }

    private void inicializar() {
        botaoEntrarVendedor = findViewById(R.id.botaoEntrarVendedor);
        verficicacaoTel = findViewById(R.id.botaoVerificacaoTel);
        telefone = findViewById(R.id.telefone);
        verificacaoLayout = findViewById(R.id.verificacao);
        verificacaocd = verificacaoLayout.findViewById(R.id.verificacao_edit_text);
        autenticacao = FirebaseAuth.getInstance();
    }

    public void login() {
        Intent intent = new Intent(this, SelecionarLojasVendedorActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioLogado = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioLogado != null) {
            if("sms".equals(pegarMetodoLogin())){
            startActivity(new Intent(this, SelecionarLojasVendedorActivity.class));
        }else{
                FirebaseAuth.getInstance().signOut();
            }
            finish();
        }
    }
}
