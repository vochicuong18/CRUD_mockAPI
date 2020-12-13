package com.example.API_CRUD.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.API_CRUD.R;
import com.example.API_CRUD.entities.User;

import java.util.ArrayList;

public class ListUserAdapter extends BaseAdapter {
    private ArrayList<User> users;

    public ListUserAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        System.out.println("123" + users);
        View viewUser;
        if (view == null) {
            viewUser = View.inflate(viewGroup.getContext(), R.layout.item_listview, null);
        } else viewUser = view;
        viewUser = View.inflate(viewGroup.getContext(), R.layout.item_listview, null);
        TextView tvName = viewUser.findViewById(R.id.tvName);
        TextView tvUsername = viewUser.findViewById(R.id.tvUsername);
        TextView tvId = viewUser.findViewById(R.id.tvId);
        tvId.setText("ID: " + users.get(i).getId());
        tvUsername.setText("User: " + users.get(i).getUsername());
        tvName.setText("Name: " + users.get(i).getName());
        return viewUser;
    }

}
