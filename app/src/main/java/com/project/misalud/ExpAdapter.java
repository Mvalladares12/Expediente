package com.project.misalud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//adaptador para mostrar los datos en pantalla por medio del recycler view

public class ExpAdapter extends RecyclerView.Adapter<ExpAdapter.ViewHolder>{
    Context context;
    ArrayList<Exp> arrayList;
    OnItemClickListener onItemClickListener;

    public ExpAdapter(Context context, ArrayList<Exp> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.exp_list_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nombre.setText(arrayList.get(position).getNombres());
        holder.apellido.setText(arrayList.get(position).getApellidos());
        //holder.edad.setText(arrayList.get(position).getEdad());
        holder.genero.setText(arrayList.get(position).getGenero());
        holder.alergia.setText(arrayList.get(position).getAlergias());
        holder.tSanguineo.setText(arrayList.get(position).gettSanguineo());
        holder.factor.setText(arrayList.get(position).getFactorRH());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombre, apellido, edad, genero, alergia, tSanguineo, factor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre=itemView.findViewById(R.id.list_nom);
            apellido=itemView.findViewById(R.id.list_ape);
            edad=itemView.findViewById(R.id.list_edad);
            genero=itemView.findViewById(R.id.list_gen);
            alergia=itemView.findViewById(R.id.list_aler);
            tSanguineo=itemView.findViewById(R.id.list_tsang);
            factor=itemView.findViewById(R.id.list_fact);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onClick(Exp exp);
    }
}