package com.project.misalud;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ExpUser extends AppCompatActivity {

    Button btnSalir2, btnVer;
    FirebaseAuth fbAuth;
    TextView tvUser;
    FirebaseUser fbUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exp_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVer=findViewById(R.id.btnVer);
        btnSalir2=findViewById(R.id.btnSalir2);
        fbAuth=FirebaseAuth.getInstance();
        tvUser=findViewById(R.id.tvUser);
        fbUsuario=fbAuth.getCurrentUser();

        //aqui se muestra el usuario que está registrado
        if (fbUsuario==null){
            Intent intent=new Intent(getApplicationContext(), Ingresar.class);
            startActivity(intent);
            finish();
        }else {
            //tvUsuario.setText(fbUsuario.getEmail());
            tvUser.setText(fbUsuario.getEmail());
        }

        btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para redirigir a la SegundaActivity
                Intent intent = new Intent(ExpUser.this, VerExpUser.class);
                startActivity(intent); // Iniciar la nueva actividad
            }
        });

        //botón de cerrar sesión
        btnSalir2.setOnClickListener(new View.OnClickListener() {
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