package com.project.misalud;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class VerExpUser extends AppCompatActivity {

    TextView tvVacio2;
    FirebaseAuth fbAuth;
    FirebaseUser fbUsuario;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_exp_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        tvVacio2=findViewById(R.id.tvVacio2);
        fbAuth= FirebaseAuth.getInstance();
        fbUsuario=fbAuth.getCurrentUser();


        //ver los expedientes del paciente
        RecyclerView recyclerView=findViewById(R.id.rvVista2);
        ArrayList<Exp> arrayList=new ArrayList<>();
        ExpAdapter adapter=new ExpAdapter(VerExpUser.this, arrayList);

        db.getReference().child("expedientes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Exp> arrayList = new ArrayList<>();
                //asdfasdfasdfasdf
                String correoDeseado = fbUsuario.getEmail();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Exp exp = dataSnapshot.getValue(Exp.class);
                    //modificar este if si no muestra nada la lista
                    if (exp != null && correoDeseado.equals(exp.getCorreoPaciente())) {
                        //Objects.requireNonNull(exp).setLlave(dataSnapshot.getKey());
                        //asdfasdfasdfas
                        exp.setLlave(dataSnapshot.getKey());
                        arrayList.add(exp);
                    }
                }

                if (arrayList.isEmpty()) {
                    tvVacio2.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvVacio2.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                ExpAdapter adapter = new ExpAdapter(VerExpUser.this, arrayList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new ExpAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Exp exp) {
                        //View view=LayoutInflater.from(ExpAdmin.this).inflate(R.layout.add_exp_dialog,null);

                        View view1= LayoutInflater.from(VerExpUser.this).inflate(R.layout.add_exp_dialog, null);
                        TextInputLayout lyNombre, lyApellido, lyEdad, lyGenero, lyAlergias, lyTSanguineo, lyFactor, lyCorreo;
                        lyNombre=view1.findViewById(R.id.lyNombre);
                        lyApellido=view1.findViewById(R.id.lyApellido);
                        lyEdad=view1.findViewById(R.id.lyEdad);
                        lyGenero=view1.findViewById(R.id.lyGenero);
                        lyAlergias=view1.findViewById(R.id.lyAlergias);
                        lyTSanguineo=view1.findViewById(R.id.lyTSanguineo);
                        lyFactor=view1.findViewById(R.id.lyFactor);
                        lyCorreo=view1.findViewById(R.id.lyCorreo);

                        lyNombre.setHint("Nombre");
                        lyApellido.setHint("Apellido");
                        lyEdad.setHint("Edad");
                        lyGenero.setHint("Genero");
                        lyAlergias.setHint("Alergias");
                        lyTSanguineo.setHint("Tipo sanguíneo");
                        lyFactor.setHint("Factor RH");
                        lyEdad.setHint("Edad");
                        lyCorreo.setHint("Correo del paciente");

                        TextInputEditText etNombre, etApellido, etEdad, etGenero, etAlergias, etTSanguineo, etFactor, etCorreo;
                        etNombre=view1.findViewById(R.id.etNombre);
                        etApellido=view1.findViewById(R.id.etApellido);
                        etEdad=view1.findViewById(R.id.etEdad);
                        etGenero=view1.findViewById(R.id.etGenero);
                        etAlergias=view1.findViewById(R.id.etAlergias);
                        etTSanguineo=view1.findViewById(R.id.etTSanguineo);
                        etFactor=view1.findViewById(R.id.etFactor);
                        etCorreo=view1.findViewById(R.id.etCorreo);

                        etNombre.setText(exp.getNombres());
                        etApellido.setText(exp.getApellidos());
                        etEdad.setText(String.valueOf(exp.getEdad()));
                        //etEdad.setText(exp.getEdad());
                        etGenero.setText(exp.getGenero());
                        etAlergias.setText(exp.getAlergias());
                        etTSanguineo.setText(exp.gettSanguineo());
                        etFactor.setText(exp.getFactorRH());
                        etCorreo.setText(exp.getCorreoPaciente());


                        etNombre.setFocusableInTouchMode(false);
                        etNombre.setInputType(InputType.TYPE_NULL);
                        etApellido.setFocusable(false);
                        etApellido.setInputType(InputType.TYPE_NULL);
                        etEdad.setFocusable(false);
                        etEdad.setInputType(InputType.TYPE_NULL);
                        etGenero.setFocusable(false);
                        etGenero.setInputType(InputType.TYPE_NULL);
                        etAlergias.setFocusable(false);
                        etAlergias.setInputType(InputType.TYPE_NULL);
                        etTSanguineo.setFocusable(false);
                        etTSanguineo.setInputType(InputType.TYPE_NULL);
                        etFactor.setFocusable(false);
                        etFactor.setInputType(InputType.TYPE_NULL);
                        etCorreo.setFocusable(false);
                        etCorreo.setInputType(InputType.TYPE_NULL);
                        etNombre.setFocusable(false);
                        etNombre.setInputType(InputType.TYPE_NULL);
                        ProgressDialog progressDialog=new ProgressDialog(VerExpUser.this);

                        AlertDialog alertDialog=new AlertDialog.Builder(VerExpUser.this)
                                .setTitle("Registro")
                                .setView(view1)
                                .setNeutralButton("", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                            Exp exp1=new Exp();

                                            db.getReference().child("expedientes").child(exp.getLlave()).setValue(exp1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                        //}
                                    }
                                })
                                .setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create();
                        alertDialog.show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}