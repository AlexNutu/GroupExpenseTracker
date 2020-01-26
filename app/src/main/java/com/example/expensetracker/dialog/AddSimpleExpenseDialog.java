package com.example.expensetracker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.ExpenseDialogListener;

public class AddSimpleExpenseDialog extends AppCompatDialogFragment {

    private EditText productNameET;
    private EditText costET;
    private Spinner spinnnerCurrency;

    private ExpenseDialogListener expenseDialogListener;
    private String expenseType;
    private String selectedCurrency;

    private AlertDialog d;

    public AddSimpleExpenseDialog(String expenseType) {
        this.expenseType = expenseType;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.simple_expense_dialog, null);

        builder.setView(view)
                .setTitle(expenseType)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String productName = productNameET.getText().toString();
                        String cost = costET.getText().toString();
                        expenseDialogListener.addExpenseToDB(productName, cost, selectedCurrency, "0.0", expenseType);
                    }
                });

        d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                    ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        productNameET = view.findViewById(R.id.productET);
        productNameET.addTextChangedListener(mTextWatcher);
        costET = view.findViewById(R.id.sumET);
        costET.addTextChangedListener(mTextWatcher);

        spinnnerCurrency = view.findViewById(R.id.currencySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnnerCurrency.setAdapter(adapter);
        spinnnerCurrency.setOnItemSelectedListener(currencyListener);

        return d;
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            final Button okButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
            String productNameText = productNameET.getText().toString().trim();
            String costText = costET.getText().toString().trim();
            if (productNameText.equals("") || costText.equals("")) {
                okButton.setEnabled(false);
            } else {
                okButton.setEnabled(true);
            }
        }
    };

    private AdapterView.OnItemSelectedListener currencyListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedCurrency = parent.getItemAtPosition(position).toString();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);

        try {
            expenseDialogListener = (ExpenseDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }


}
