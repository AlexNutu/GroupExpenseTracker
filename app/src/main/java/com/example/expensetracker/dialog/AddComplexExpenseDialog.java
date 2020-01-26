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

public class AddComplexExpenseDialog extends AppCompatDialogFragment {

    private EditText productNameET;
    private EditText costET;
    private EditText percentageET;
    private Spinner currencySpinner;
    private Spinner expenseTypeSpinner;

    private ExpenseDialogListener expenseDialogListener;
    private String expenseType;
    private String selectedCurrency;
    private String selectedExpenseType;

    private AlertDialog d;

    public AddComplexExpenseDialog(String expenseType) {
        this.expenseType = expenseType;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.complex_expense_dialog, null);

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
                        String percentage = percentageET.getText().toString().equals("") ? "0.0" : percentageET.getText().toString();
                        expenseDialogListener.addExpenseToDB(productName, cost, selectedCurrency, percentage, selectedExpenseType);
                    }
                });

        d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        productNameET = view.findViewById(R.id.productET);
        productNameET.addTextChangedListener(mTextWatcher);
        costET = view.findViewById(R.id.sumET);
        costET.addTextChangedListener(mTextWatcher);
        percentageET = view.findViewById(R.id.expensePercentET);
        percentageET.addTextChangedListener(mTextWatcher);

        currencySpinner = view.findViewById(R.id.currencySpinner);
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.currencies, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);
        currencySpinner.setOnItemSelectedListener(currencyListener);

        expenseTypeSpinner = view.findViewById(R.id.expenseTypeSpinner);
        ArrayAdapter<CharSequence> expenseTypeAdapter;
        if (expenseType.equals("Group Expense")) {
            expenseTypeAdapter = ArrayAdapter.createFromResource(this.getContext(),
                    R.array.groupExpenseTypes, android.R.layout.simple_spinner_item);
        } else {
            expenseTypeAdapter = ArrayAdapter.createFromResource(this.getContext(),
                    R.array.collectExpenseTypes, android.R.layout.simple_spinner_item);
        }
        expenseTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseTypeSpinner.setAdapter(expenseTypeAdapter);
        expenseTypeSpinner.setOnItemSelectedListener(expenseTypeListener);

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
            checkEmptyFieldValues();
        }
    };

    private void checkEmptyFieldValues() {
        final Button okButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
        String productNameText = productNameET.getText().toString().trim();
        String costText = costET.getText().toString().trim();
        String percentageText = percentageET.getText().toString().trim();
        if (productNameText.equals("")
                || costText.equals("")
                || (percentageText.equals("") && percentageET.getVisibility() == View.VISIBLE)) {
            okButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);
        }
    }

    private AdapterView.OnItemSelectedListener currencyListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedCurrency = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private AdapterView.OnItemSelectedListener expenseTypeListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedExpenseType = parent.getItemAtPosition(position).toString();
            if (selectedExpenseType.equals("Initial Collect Expense")) {
                percentageET.setVisibility(View.VISIBLE);
                checkEmptyFieldValues();
            } else {
                percentageET.setVisibility(View.GONE);
                checkEmptyFieldValues();
            }
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
