package com.example.expensetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.expensetracker.domain.ToDoObject;

import java.util.ArrayList;

public class ToDoListAdapter extends ArrayAdapter<ToDoObject> {

    private static final String TAG = "ToDoListAdapter";
    private Context mContext;
    private int mResource;

    public ToDoListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ToDoObject> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String message = getItem(position).getMessage();
        String name = getItem(position).getUser().getFirstName() + " " + getItem(position).getUser().getLastName();
        String createdDate = getItem(position).getCreateDate();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView messageTV = convertView.findViewById(R.id.messageTV);
        TextView nameTV = convertView.findViewById(R.id.nameToDoTV);
        TextView createdDateTV = convertView.findViewById(R.id.createdDateTV);

        messageTV.setText(message);
        nameTV.setText(name);
        createdDateTV.setText(createdDate);

        return convertView;
    }
}
