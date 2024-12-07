package com.project.misalud;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RecuperarContra extends AppCompatActivity {

    TextInputEditText editTextCorreo;
    Button btnConfirmar;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore; // Firestore para almacenar los roles
    ProgressBar progreso;
    TextView tvIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recuperar_contra);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance(); // Inicializar Firestore
        editTextCorreo = findViewById(R.id.correo);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        progreso = findViewById(R.id.barraProgreso);
        tvIngresar = findViewById(R.id.IngresarAhora);

        tvIngresar.setOnClickListener(view -> {
            Intent it = new Intent(getApplicationContext(), Ingresar.class);
            startActivity(it);
            finish();
        });

        btnConfirmar.setOnClickListener(view -> {
           String email = editTextCorreo.getText().toString();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RecuperarContra.this, "Por favor, introduce tu correo electrónico.", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(RecuperarContra.this, "Correo enviado, Revise su correo", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(RecuperarContra.this, "Revisa si su email es correcto", Toast.LENGTH_SHORT).show();
                }
            });


        });
    }
}