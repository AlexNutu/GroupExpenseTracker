package com.example.expensetracker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.User;

import java.util.ArrayList;

public class MembersListAdapter extends ArrayAdapter<User> {

    private static final String TAG = "MembersListAdapter";
    private Context mContext;
    private int mResource;

    public MembersListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView fullNameUserTV = convertView.findViewById(R.id.fullNameUserTV);
        TextView emailUserTV = convertView.findViewById(R.id.emailUserTV);

        fullNameUserTV.setText(getItem(position).getFirstName() + " " + getItem(position).getLastName());
        emailUserTV.setText(getItem(position).getEmail());

        return convertView;
    }
}
