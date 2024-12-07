package com.project.misalud;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registrar extends AppCompatActivity {

    TextInputEditText editTextCorreo, editTextContra;
    Button btnRegistrar;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore; // Firestore para almacenar los roles
    ProgressBar progreso;
    TextView tvIngresar;


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

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance(); // Inicializar Firestore
        editTextCorreo = findViewById(R.id.correo);
        editTextContra = findViewById(R.id.contra);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        progreso = findViewById(R.id.barraProgreso);
        tvIngresar = findViewById(R.id.IngresarAhora);

        tvIngresar.setOnClickListener(view -> {
            Intent it = new Intent(getApplicationContext(), Ingresar.class);
            startActivity(it);
            finish();
        });

        btnRegistrar.setOnClickListener(view -> {
            progreso.setVisibility(View.VISIBLE);
            String correo = String.valueOf(editTextCorreo.getText());
            String contra = String.valueOf(editTextContra.getText());

            // Validar campos vacíos
            if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(contra)) {
                Toast.makeText(Registrar.this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
                progreso.setVisibility(View.GONE);
                return;
            }

            // Registrar usuario con Firebase Authentication
            mAuth.createUserWithEmailAndPassword(correo, contra)
                    .addOnCompleteListener(task -> {
                        progreso.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();

                                // Guardar información del usuario en Firestore
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", correo);
                                userData.put("role", "user"); // Rol predeterminado

                                firestore.collection("users").document(userId)
                                        .set(userData)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                // Enviar correo de verificación
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(task2 -> {
                                                            if (task2.isSuccessful()) {
                                                                Toast.makeText(Registrar.this, "Cuenta creada con éxito. Verifique su correo antes de iniciar sesión.",
                                                                        Toast.LENGTH_SHORT).show();
                                                                mAuth.signOut();
                                                                Intent it = new Intent(getApplicationContext(), Ingresar.class);
                                                                startActivity(it);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(Registrar.this, "No se pudo enviar el correo de verificación. Intente de nuevo.",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(Registrar.this, "Error al guardar los datos del usuario.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Manejo de errores específicos
                            if (task.getException() != null) {
                                String errorCode = ((com.google.firebase.auth.FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        Toast.makeText(Registrar.this, "El correo ya está registrado.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        Toast.makeText(Registrar.this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "ERROR_INVALID_EMAIL":
                                        Toast.makeText(Registrar.this, "El correo electrónico es inválido.", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(Registrar.this, "Error desconocido: " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }
                    });

        });
    }
}
