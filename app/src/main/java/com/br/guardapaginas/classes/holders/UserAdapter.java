package com.br.guardapaginas.classes.holders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.guardapaginas.R;
import com.br.guardapaginas.classes.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> implements UserRecycleViewInterface{

    private final UserRecycleViewInterface userRecycleViewInterface;
    Context context;
    List<User> userItems;

    public UserAdapter(Context context, List<User> userItems, UserRecycleViewInterface userRecycleViewInterface) {
        this.context = context;
        this.userItems = userItems;
        this.userRecycleViewInterface = userRecycleViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_item, parent, false);
        return new MyViewHolder(view, userRecycleViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.userEmail.setText(userItems.get(position).getEmail());
        holder.userName.setText(userItems.get(position).getName());
        holder.userRegistration.setText(userItems.get(position).getRegistration());
    }

    @Override
    public int getItemCount() {
        return userItems.size();
    }

    @Override
    public void onItemClick(int Position) {

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView userEmail, userName, userRegistration;

        public MyViewHolder(@NonNull View itemView, UserRecycleViewInterface userRecycleViewInterface) {
            super(itemView);
            userEmail        = itemView.findViewById(R.id.userEmail);
            userName         = itemView.findViewById(R.id.userName);
            userRegistration = itemView.findViewById(R.id.userRegistration);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(userRecycleViewInterface != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            userRecycleViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}