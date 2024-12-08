package com.project.misalud;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fbAuth;
    Button btnSalir, btnCrear;
    TextView tvUsuario;
    FirebaseUser fbUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fbAuth=FirebaseAuth.getInstance();
        btnSalir=findViewById(R.id.btnSalir);
        tvUsuario=findViewById(R.id.usuarioDetalles);
        fbUsuario=fbAuth.getCurrentUser();
        btnCrear=findViewById(R.id.btnCrear);
        DatabaseReference dbr= FirebaseDatabase.getInstance().getReference();
        //dbr.child("expedientes").child(fbAuth.getCurrentUser().getUid()).child("Nombre").setValue("Mario");

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para redirigir a la SegundaActivity
                Intent intent = new Intent(MainActivity.this, ExpAdmin.class);
                startActivity(intent); // Iniciar la nueva actividad
            }
        });


        //aqui se muestra el usuario que está registrado
        if (fbUsuario==null){
            Intent intent=new Intent(getApplicationContext(), Ingresar.class);
            startActivity(intent);
            finish();
        }else {
            //tvUsuario.setText(fbUsuario.getEmail());
            tvUsuario.setText(fbUsuario.getEmail());
        }


        //botón de cerrar sesión
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                Intent intent=new Intent(getApplicationContext(), Ingresar.class);
                startActivity(intent);
                finish();
            }
        });
    }




}