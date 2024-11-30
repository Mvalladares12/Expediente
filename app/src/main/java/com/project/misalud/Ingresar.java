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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Ingresar extends AppCompatActivity {

    TextInputEditText editTextCorreo, editTextContra;
    Button btnIngresar;
    FirebaseAuth mAuth;
    ProgressBar progreso;
    TextView tvRegistrar;

    @Override
    public void onStart() {
        super.onStart();
        // Verificar si el usuario ya está autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Si el usuario ya está autenticado, verificar su rol
            checkUserRole(currentUser);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingresar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        editTextCorreo = findViewById(R.id.correo);
        editTextContra = findViewById(R.id.contra);
        btnIngresar = findViewById(R.id.btnIngresar);
        progreso = findViewById(R.id.barraProgreso);
        tvRegistrar = findViewById(R.id.RegistrarAhora);

        tvRegistrar.setOnClickListener(view -> {
            Intent it = new Intent(getApplicationContext(), Registrar.class);
            startActivity(it);
            finish();
        });

        btnIngresar.setOnClickListener(view -> {
            progreso.setVisibility(View.VISIBLE);
            String correo = String.valueOf(editTextCorreo.getText());
            String contra = String.valueOf(editTextContra.getText());

            // Validar campos vacíos
            if (TextUtils.isEmpty(correo)) {
                Toast.makeText(Ingresar.this, "Ingrese un correo electrónico", Toast.LENGTH_SHORT).show();
                progreso.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(contra)) {
                Toast.makeText(Ingresar.this, "Ingrese una contraseña", Toast.LENGTH_SHORT).show();
                progreso.setVisibility(View.GONE);
                return;
            }

            // Iniciar sesión con Firebase Authentication
            mAuth.signInWithEmailAndPassword(correo, contra)
                    .addOnCompleteListener(task -> {
                        progreso.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Verificar el rol del usuario
                                checkUserRole(user);
                            }
                        } else {
                            // Manejo de errores de autenticación
                            if (task.getException() != null) {
                                String errorCode = ((com.google.firebase.auth.FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        Toast.makeText(Ingresar.this, "El correo electrónico no es válido.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "ERROR_USER_NOT_FOUND":
                                        Toast.makeText(Ingresar.this, "Usuario no registrado.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "ERROR_WRONG_PASSWORD":
                                        Toast.makeText(Ingresar.this, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "ERROR_USER_DISABLED":
                                        Toast.makeText(Ingresar.this, "La cuenta ha sido deshabilitada.", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }
                    });
        });
    }

    private void checkUserRole(FirebaseUser user) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = user.getUid();

        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");

                        if ("doctor".equals(role)) {
                            Intent adminIntent = new Intent(Ingresar.this, MainActivity.class);
                            startActivity(adminIntent);
                            finish();
                        } else if ("user".equals(role)) {
                            Intent userIntent = new Intent(Ingresar.this, MainActivity.class);
                            startActivity(userIntent);
                            finish();
                        } else {
                            Toast.makeText(Ingresar.this, "Rol no configurado o desconocido", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Ingresar.this, "Usuario no encontrado en Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
