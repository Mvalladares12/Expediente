package com.project.misalud;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registrar extends AppCompatActivity {

    TextInputEditText editTextCorreo, editTextContra, editNombre, editApellido, editJuntaVi, editProfesion, editNumeroJun;
    Button btnRegistrar;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore; // Firestore para almacenar los roles
    ProgressBar progreso;
    TextView tvIngresar;

    Spinner spinnerOpciones;


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
        spinnerOpciones = findViewById(R.id.spinnerSeleccion);
        editNombre = findViewById(R.id.nombreR);
        editApellido = findViewById(R.id.apellidosR);
        editJuntaVi = findViewById(R.id.juntaVi);
        editProfesion = findViewById(R.id.profesion);
        editNumeroJun = findViewById(R.id.numeroJunta);

        tvIngresar.setOnClickListener(view -> {
            Intent it = new Intent(getApplicationContext(), Ingresar.class);
            startActivity(it);
            finish();
        });

        //Array
        seleccionSpinner();





        btnRegistrar.setOnClickListener(view -> {
            progreso.setVisibility(View.VISIBLE);
            String correo = String.valueOf(editTextCorreo.getText());
            String contra = String.valueOf(editTextContra.getText());
            String nombre = String.valueOf(editNombre.getText());
            String apellidos = String.valueOf(editApellido.getText());

            // Validar campos vacíos
            if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(contra)|| TextUtils.isEmpty(nombre)|| TextUtils.isEmpty(apellidos)) {
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

                                //llamar las opciones del array
                                String selectedOption = spinnerOpciones.getSelectedItem().toString();
                                //agregar  los valores que se obtienen
                                String juntaVigilancia = editJuntaVi.getText().toString();
                                String profesion = editProfesion.getText().toString();
                                String numeroJunta = editNumeroJun.getText().toString();

                                // Guardar información del usuario en Firestore
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", correo);
                                userData.put("role", selectedOption); // Rol predeterminado
                                userData.put("nombre", nombre);
                                userData.put("apellido", apellidos);

                                // Agregar especialización solo si el rol es "Doctor"
                                if (selectedOption.equals("Doctor")) {
                                    if (juntaVigilancia.isEmpty() || profesion.isEmpty() || numeroJunta.isEmpty()) {
                                        Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    //envia los datos si es un doctor
                                    userData.put("junta", juntaVigilancia);
                                    userData.put("profesion", profesion);
                                    userData.put("numerojun", numeroJunta);
                                }


                                //agregar datos a firestore
                                firestore.collection("users").document(userId)
                                        .set(userData)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                // Enviar correo de verificación para verificar que si exista
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

    //creacion y configuracion del array
    private void seleccionSpinner() {
        // Opciones del Spinner
        String[] seleccion = {"Doctor", "Usuario"};

        // Configurar adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, seleccion);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOpciones.setAdapter(adapter);


        //eventos del array en la seleccion para respuesta de las selecciones del usuario
        spinnerOpciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRole = seleccion[position];

                // Mostrar o ocultar campos basados en la selección
                if (selectedRole.equals("Doctor")) {
                    editJuntaVi.setVisibility(View.VISIBLE);
                    editProfesion.setVisibility(View.VISIBLE);
                    editNumeroJun.setVisibility(View.VISIBLE);
                } else {
                    editJuntaVi.setVisibility(View.GONE);
                    editProfesion.setVisibility(View.GONE);
                    editNumeroJun.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

    }


}
