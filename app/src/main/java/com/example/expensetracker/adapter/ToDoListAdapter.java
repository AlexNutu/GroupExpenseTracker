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
import com.example.expensetracker.dialog.CheckToDoDialog;
import com.example.expensetracker.dialog.DeleteToDoDialog;
import com.example.expensetracker.domain.ToDoObjectWithTrip;

import java.util.ArrayList;

public class ToDoListAdapter extends ArrayAdapter<ToDoObjectWithTrip> {

    private static final String TAG = "ToDoListAdapter";
    private Context mContext;
    private int mResource;
    private Integer idTrip;

    public ToDoListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ToDoObjectWithTrip> objects, Integer idTripParam) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.idTrip = idTripParam;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Long idNote = getItem(position).getId();
        String message = getItem(position).getMessage();
        Boolean approved = getItem(position).getApproved();
        String name = getItem(position).getUser().getFirstName() + " " + getItem(position).getUser().getLastName();
        String createdDate = getItem(position).getCreateDate();
        String modifiedDate = getItem(position).getModifyDate();
        final ToDoObjectWithTrip toDoCurrent = new ToDoObjectWithTrip(idNote, approved, message, getItem(position).getUser(), null, createdDate, modifiedDate);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView messageTV = convertView.findViewById(R.id.messageTV);
        TextView nameTV = convertView.findViewById(R.id.nameToDoTV);
        TextView createdDateTV = convertView.findViewById(R.id.createdDateTV);
        ImageButton deleteToDoBtn = convertView.findViewById(R.id.deleteToDoBtn);
        ImageButton checkTodoBtn = convertView.findViewById(R.id.checkToDoBtn);
        ImageButton checkedTodoBtn = convertView.findViewById(R.id.checkedToDoBtn);

        messageTV.setText(message);
        nameTV.setText(name);
        createdDateTV.setText(createdDate);
        deleteToDoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteToDoDialog(idNote);
            }
        });
        if (approved) {
            messageTV.setPaintFlags(messageTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            checkTodoBtn.setVisibility(View.GONE);
            checkedTodoBtn.setVisibility(View.VISIBLE);
            checkedTodoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDoCurrent.setApproved(false);
                    openUpdateDialog(toDoCurrent);
                }
            });
        } else {
            checkTodoBtn.setVisibility(View.VISIBLE);
            checkedTodoBtn.setVisibility(View.GONE);
            checkTodoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDoCurrent.setApproved(true);
                    openUpdateDialog(toDoCurrent);
                }
            });
        }

        return convertView;
    }

    public void openDeleteToDoDialog(Long idNote) {
        DeleteToDoDialog deleteToDoDialog = new DeleteToDoDialog(idNote, idTrip);
        deleteToDoDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "add todo dialog");
    }

    public void openUpdateDialog(ToDoObjectWithTrip toDoCurrent) {
        CheckToDoDialog checkToDoDialog = new CheckToDoDialog(toDoCurrent, idTrip);
        checkToDoDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "update todo dialog");
    }
}
