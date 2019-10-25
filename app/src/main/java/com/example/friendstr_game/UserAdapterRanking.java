package com.example.friendstr_game;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapterRanking extends RecyclerView.Adapter<UserAdapterRanking.ViewHolder> {

   List<UsersAndScore> usersData = new ArrayList<>();

    Context context;

    public UserAdapterRanking(Context context) {
        this.context = context;
    }

    public void setUsersData(List<UsersAndScore> usersData) {
        this.usersData = usersData;
    }



    @NonNull
    @Override
    public UserAdapterRanking.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layout = R.layout.user_information;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapterRanking.ViewHolder holder, int position) {

        UsersAndScore user = usersData.get(position);
        holder.userDetails(user);
    }

    @Override
    public int getItemCount() {
        return this.usersData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameR, scoreR;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameR = itemView.findViewById(R.id.user_nameR);
            scoreR = itemView.findViewById(R.id.scoreR);

        }

        //Update CardView details accordingly
        public void userDetails(UsersAndScore user)
        {
            String userName = user.getUsername();
            String highScore = Integer.toString(user.getUserScore());
            userNameR.setText(userName);
            scoreR.setText(highScore);
        }
    }


}

