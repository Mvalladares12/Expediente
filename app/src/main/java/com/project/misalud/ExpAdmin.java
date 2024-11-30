package com.project.misalud;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

//aqui va la logica del CRUD

public class ExpAdmin extends AppCompatActivity {

    //Borrar en caso que no funcione
    Dialog dialog;
    FirebaseAuth fbAuth;
    FirebaseUser fbUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exp_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvVacio;
        tvVacio=findViewById(R.id.tvVacio);
        fbAuth=FirebaseAuth.getInstance();
        fbUsuario=fbAuth.getCurrentUser();

        FirebaseApp.initializeApp(ExpAdmin.this);
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        //boton para agregar registro
        FloatingActionButton agregar=findViewById(R.id.btnAgregar);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view1= LayoutInflater.from(ExpAdmin.this).inflate(R.layout.add_exp_dialog, null);
                TextInputLayout lyNombre, lyApellido, lyEdad, lyGenero, lyAlergias, lyTSanguineo, lyFactor;
                lyNombre=view1.findViewById(R.id.lyNombre);
                lyApellido=view1.findViewById(R.id.lyApellido);
                lyEdad=view1.findViewById(R.id.lyEdad);
                lyGenero=view1.findViewById(R.id.lyGenero);
                lyAlergias=view1.findViewById(R.id.lyAlergias);
                lyTSanguineo=view1.findViewById(R.id.lyTSanguineo);
                lyFactor=view1.findViewById(R.id.lyFactor);

                TextInputEditText etNombre, etApellido, etEdad, etGenero, etAlergias, etTSanguineo, etFactor;
                etNombre=view1.findViewById(R.id.etNombre);
                etApellido=view1.findViewById(R.id.etApellido);
                etEdad=view1.findViewById(R.id.etEdad);
                etGenero=view1.findViewById(R.id.etGenero);
                etAlergias=view1.findViewById(R.id.etAlergias);
                etTSanguineo=view1.findViewById(R.id.etTSanguineo);
                etFactor=view1.findViewById(R.id.etFactor);

                //pantalla flotante para agregar registros
                AlertDialog dialogo=new AlertDialog.Builder(ExpAdmin.this)
                        .setTitle("Agregar registro")
                        .setView(view1)
                        .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Objects.requireNonNull(etNombre.getText()).toString().isEmpty()){
                                    lyNombre.setError("Debe completar este campo!");
                                } else if (Objects.requireNonNull(etApellido.getText()).toString().isEmpty()) {
                                    lyApellido.setError("Debe completar este campo!");
                                }else if (Objects.requireNonNull(etEdad.getText()).toString().isEmpty()) {
                                    lyEdad.setError("Debe completar este campo!");
                                }else if (Objects.requireNonNull(etGenero.getText()).toString().isEmpty()) {
                                    lyGenero.setError("Debe completar este campo!");
                                }else if (Objects.requireNonNull(etAlergias.getText()).toString().isEmpty()) {
                                    lyAlergias.setError("Debe completar este campo!");
                                }else if (Objects.requireNonNull(etTSanguineo.getText()).toString().isEmpty()) {
                                    lyTSanguineo.setError("Debe completar este campo!");
                                }else if (Objects.requireNonNull(etFactor.getText()).toString().isEmpty()) {
                                    lyFactor.setError("Debe completar este campo!");
                                }else {
                                    //Borrar en caso de que no funcione
                                    dialog=crearProgressDialog();
                                    dialog.show();
                                    //descomentar en caso de que no funcion
//                                    ProgressDialog dialog=new ProgressDialog(ExpAdmin.this);
//                                    dialog.setMessage("afd");
                                    Exp exp=new Exp();
                                    exp.setNombres(etNombre.getText().toString());
                                    exp.setApellidos(etApellido.getText().toString());
                                    exp.setEdad(Integer.parseInt(etEdad.getText().toString()));
                                    exp.setGenero(etGenero.getText().toString());
                                    exp.setAlergias(etAlergias.getText().toString());
                                    exp.settSanguineo(etTSanguineo.getText().toString());
                                    exp.setFactorRH(etFactor.getText().toString());
                                    exp.setNombreMed(fbUsuario.getEmail());
                                    db.getReference().child("expedientes").push().setValue(exp).addOnSuccessListener(new OnSuccessListener<Void>() {


                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                            dialogInterface.dismiss();
                                            Toast.makeText(ExpAdmin.this,"Guardado exitosamente", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            Toast.makeText(ExpAdmin.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                dialogo.show();
            }
        });


        //hacer cambios en los registros
        RecyclerView recyclerView=findViewById(R.id.rvVista);

        db.getReference().child("expedientes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Exp> arrayList=new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Exp exp=dataSnapshot.getValue(Exp.class);
                    Objects.requireNonNull(exp).setLlave(dataSnapshot.getKey());
                    arrayList.add(exp);
                }

                if (arrayList.isEmpty()){
                    tvVacio.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    tvVacio.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                ExpAdapter adapter=new ExpAdapter(ExpAdmin.this, arrayList);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new ExpAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Exp exp) {
                        //View view=LayoutInflater.from(ExpAdmin.this).inflate(R.layout.add_exp_dialog,null);

                        View view1= LayoutInflater.from(ExpAdmin.this).inflate(R.layout.add_exp_dialog, null);
                        TextInputLayout lyNombre, lyApellido, lyEdad, lyGenero, lyAlergias, lyTSanguineo, lyFactor;
                        lyNombre=view1.findViewById(R.id.lyNombre);
                        lyApellido=view1.findViewById(R.id.lyApellido);
                        lyEdad=view1.findViewById(R.id.lyEdad);
                        lyGenero=view1.findViewById(R.id.lyGenero);
                        lyAlergias=view1.findViewById(R.id.lyAlergias);
                        lyTSanguineo=view1.findViewById(R.id.lyTSanguineo);
                        lyFactor=view1.findViewById(R.id.lyFactor);

                        TextInputEditText etNombre, etApellido, etEdad, etGenero, etAlergias, etTSanguineo, etFactor;
                        etNombre=view1.findViewById(R.id.etNombre);
                        etApellido=view1.findViewById(R.id.etApellido);
                        etEdad=view1.findViewById(R.id.etEdad);
                        etGenero=view1.findViewById(R.id.etGenero);
                        etAlergias=view1.findViewById(R.id.etAlergias);
                        etTSanguineo=view1.findViewById(R.id.etTSanguineo);
                        etFactor=view1.findViewById(R.id.etFactor);

                        etNombre.setText(exp.getNombres());
                        etApellido.setText(exp.getApellidos());
                        etEdad.setText(String.valueOf(exp.getEdad()));
                        //etEdad.setText(exp.getEdad());
                        etGenero.setText(exp.getGenero());
                        etAlergias.setText(exp.getAlergias());
                        etTSanguineo.setText(exp.gettSanguineo());
                        etFactor.setText(exp.getFactorRH());

                        ProgressDialog progressDialog=new ProgressDialog(ExpAdmin.this);

                        AlertDialog alertDialog=new AlertDialog.Builder(ExpAdmin.this)
                                .setTitle("Editar")
                                .setView(view1)
                                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (Objects.requireNonNull(etNombre.getText()).toString().isEmpty()){
                                            lyNombre.setError("Debe completar este campo!");
                                        } else if (Objects.requireNonNull(etApellido.getText()).toString().isEmpty()) {
                                            lyApellido.setError("Debe completar este campo!");
                                        }else if (Objects.requireNonNull(etEdad.getText()).toString().isEmpty()) {
                                            lyEdad.setError("Debe completar este campo!");
                                        }else if (Objects.requireNonNull(etGenero.getText()).toString().isEmpty()) {
                                            lyGenero.setError("Debe completar este campo!");
                                        }else if (Objects.requireNonNull(etAlergias.getText()).toString().isEmpty()) {
                                            lyAlergias.setError("Debe completar este campo!");
                                        }else if (Objects.requireNonNull(etTSanguineo.getText()).toString().isEmpty()) {
                                            lyTSanguineo.setError("Debe completar este campo!");
                                        }else if (Objects.requireNonNull(etFactor.getText()).toString().isEmpty()) {
                                            lyFactor.setError("Debe completar este campo!");
                                        }else {
                                            //Borrar en caso de que no funcione
                                            dialog=crearProgressDialog();
                                            dialog.show();
                                            //descomentar en caso de que no funcion
//                                    progressDialog.setMessage("afd");
//                                    progressDialog.show();
                                            Exp exp1=new Exp();
                                            exp1.setNombres(etNombre.getText().toString());
                                            exp1.setApellidos(etApellido.getText().toString());
                                            exp1.setEdad(Integer.parseInt(etEdad.getText().toString().trim()));
                                            exp1.setGenero(etGenero.getText().toString());
                                            exp1.setAlergias(etAlergias.getText().toString());
                                            exp1.settSanguineo(etTSanguineo.getText().toString());
                                            exp1.setFactorRH(etFactor.getText().toString());
                                            exp1.setNombreMed(fbUsuario.getEmail());
                                            db.getReference().child("expedientes").child(exp.getLlave()).setValue(exp1).addOnSuccessListener(new OnSuccessListener<Void>() {


                                                @Override
                                                public void onSuccess(Void unused) {
                                                    //progressDialog.dismiss();
                                                    dialog.dismiss();
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(ExpAdmin.this,"Editado exitosamente", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //progressDialog.dismiss();
                                                    dialog.dismiss();
                                                    Toast.makeText(ExpAdmin.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                })
                                .setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressDialog.setTitle("Borrando...");
                                        progressDialog.show();
                                        db.getReference().child("expedientes").child(exp.getLlave()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(ExpAdmin.this, "Borrado exitosamente", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                            }
                                        });
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
    private Dialog crearProgressDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);

        ProgressBar pgb=new ProgressBar(this);
        builder.setView(pgb);

        return builder.create();
    }
}