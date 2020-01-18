package com.example.expensetracker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.Expense;
import com.example.expensetracker.domain.ToDoObject;
import com.example.expensetracker.domain.ui.main.PageViewModel;

import java.util.ArrayList;
import java.util.List;

public class ViewReportAdapter extends ArrayAdapter<Expense> {

    private static final String TAG = "ViewReportAdapter";
    private Context mContext;
    private int mResource;

    public ViewReportAdapter(@NonNull Context context, int report_item_layout, @NonNull Expense[] objects) {
        super(context, report_item_layout, objects);
        mContext = context;
        mResource = report_item_layout;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView productTV = convertView.findViewById(R.id.productValueTV);
        TextView costTV = convertView.findViewById(R.id.costValueTV);
        TextView expenseTV = convertView.findViewById(R.id.expenseTypeValueTV);
        TextView userTV = convertView.findViewById(R.id.userValueTV);

        productTV.setText(getItem(position).getProduct());
        costTV.setText(String.valueOf(getItem(position).getSum()) + getItem(position).getCurrency());
        expenseTV.setText(getItem(position).getExpensiveType());
        userTV.setText(getItem(position).getUser().getFirstName() + " " + getItem(position).getUser().getLastName());

        return convertView;
    }

}
