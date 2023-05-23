package com.br.guardapaginas.classes.holders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.guardapaginas.R;
import com.br.guardapaginas.classes.Gender;

import java.util.List;

public class GenderAdapter extends RecyclerView.Adapter<GenderAdapter.MyViewHolder> implements GenderRecycleViewInterface{

    private final GenderRecycleViewInterface genderRecycleViewInterface;

    Context context;
    List<Gender> genderItems;

    public GenderAdapter(Context context, List<Gender> genderItems, GenderRecycleViewInterface genderRecycleViewInterface) {
        this.context = context;
        this.genderItems = genderItems;
        this.genderRecycleViewInterface = genderRecycleViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.gender_item, parent, false);
        return new GenderAdapter.MyViewHolder(view, genderRecycleViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull GenderAdapter.MyViewHolder holder, int position) {
        holder.genderName.setText(genderItems.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return genderItems.size();
    }

    @Override
    public void onItemClick(int Position) {

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView genderId, genderName;

        public MyViewHolder(@NonNull View itemView, GenderRecycleViewInterface genderRecycleViewInterface) {
            super(itemView);
            genderId   = itemView.findViewById(R.id.userId);
            genderName = itemView.findViewById(R.id.userName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(genderRecycleViewInterface != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            genderRecycleViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
