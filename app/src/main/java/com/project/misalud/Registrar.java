package com.project.misalud;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Registrar extends AppCompatActivity {

    TextInputEditText editTextCorreo, editTextContra;
    Button btnRegistrar;
    FirebaseAuth mAuth;
    ProgressBar progreso;
    TextView tvIngresar;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent it=new Intent(getApplicationContext(), MainActivity.class);
            startActivity(it);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth=FirebaseAuth.getInstance();
        editTextCorreo=findViewById(R.id.correo);
        editTextContra=findViewById(R.id.contra);
        btnRegistrar=findViewById(R.id.btnRegistrar);
        progreso=findViewById(R.id.barraProgreso);
        tvIngresar=findViewById(R.id.IngresarAhora);
        tvIngresar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent it=new Intent(getApplicationContext(), Ingresar.class);
                startActivity(it);
                finish();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progreso.setVisibility(View.VISIBLE);
                String correo, contra;
                correo=String.valueOf(editTextCorreo.getText());
                contra=String.valueOf(editTextContra.getText());

                //Revisar si los campos están vacíos

                if (TextUtils.isEmpty(correo)){
                    Toast.makeText(Registrar.this,"Ingrese un correo electronico", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contra)){
                    Toast.makeText(Registrar.this,"Ingrese una contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(correo, contra)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progreso.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    /* Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);*/
                                    Toast.makeText(Registrar.this, "Cuenta creada.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent it=new Intent(getApplicationContext(), Ingresar.class);
                                    startActivity(it);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Registrar.this, "Registro fallido.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            }
        });
    }
}