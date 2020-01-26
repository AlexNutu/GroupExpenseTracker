package com.example.expensetracker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.expensetracker.domain.Expense;
import com.example.expensetracker.domain.ExpenseDialogListener;
import com.example.expensetracker.helper.Session;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class AddComplexExpenseDialog extends AppCompatDialogFragment {

    private Button newProductBTN;
    private Button existingProductBTN;
    private EditText productNameET;
    private EditText costET;
    private EditText percentageET;
    private Spinner currencySpinner;
    private Spinner expenseTypeSpinner;
    private Spinner productsSpinner;

    private ExpenseDialogListener expenseDialogListener;
    private String expenseType;
    private Integer tripId;
    private Session session;

    private String selectedCurrency;
    private String selectedExpenseType;
    private String selectedProduct;

    private AlertDialog d;

    public AddComplexExpenseDialog(String expenseType, Integer tripId) {
        this.expenseType = expenseType;
        this.tripId = tripId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        session = new Session(getContext());

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
                        String cost = costET.getText().toString();
                        String percentage = percentageET.getText().toString().equals("") ? "0.0" : percentageET.getText().toString();
                        if (productNameET.getVisibility() == View.VISIBLE) {
                            String newProductName = productNameET.getText().toString();
                            expenseDialogListener.addExpenseToDB(newProductName, cost, selectedCurrency, percentage, selectedExpenseType);
                        } else {
                            String selectedProductName = selectedProduct;
                            expenseDialogListener.addExpenseToDB(selectedProductName, cost, selectedCurrency, percentage, selectedExpenseType);
                        }

                    }
                });

        d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        // https://stackoverflow.com/questions/19812302/spinners-scrollbar-style
        productsSpinner = view.findViewById(R.id.productSpinner);
        ArrayAdapter<String> productsAdapter;
        String[] existingProducts = {};
        if (expenseType.equals("Group Expense")) {
            try {
                existingProducts = new GetExpenseProductsReqTask().execute("group").get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                existingProducts = new GetExpenseProductsReqTask().execute("collect").get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        productsAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item,
                existingProducts);
        productsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productsSpinner.setAdapter(productsAdapter);
        productsSpinner.setVisibility(View.GONE);
        productsSpinner.setOnItemSelectedListener(productsListener);

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
                    R.array.allGroupExpenseTypes, android.R.layout.simple_spinner_item);
        } else {
            expenseTypeAdapter = ArrayAdapter.createFromResource(this.getContext(),
                    R.array.allCollectExpenseTypes, android.R.layout.simple_spinner_item);
        }
        expenseTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseTypeSpinner.setAdapter(expenseTypeAdapter);
        expenseTypeSpinner.setOnItemSelectedListener(expenseTypeListener);


        newProductBTN = view.findViewById(R.id.newExpenseBTN);
        existingProductBTN = view.findViewById(R.id.existingExpenseBTN);
        newProductBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productNameET.setVisibility(View.VISIBLE);
                productsSpinner.setVisibility(View.GONE);
                checkEmptyFieldValues();

                int expenseTypeId = -1;
                if (expenseType.equals("Group Expense")) {
                    expenseTypeId = R.array.allGroupExpenseTypes;
                } else {
                    expenseTypeId = R.array.allCollectExpenseTypes;
                }
                ArrayAdapter<CharSequence> expenseTypeAdapter;
                expenseTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                        expenseTypeId, android.R.layout.simple_spinner_item);
                expenseTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                expenseTypeSpinner.setAdapter(expenseTypeAdapter);
                expenseTypeSpinner.setOnItemSelectedListener(expenseTypeListener);
            }
        });
        existingProductBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productNameET.setVisibility(View.GONE);
                productsSpinner.setVisibility(View.VISIBLE);
                checkEmptyFieldValues();

                int expenseTypeId = -1;
                if (expenseType.equals("Group Expense")) {
                    expenseTypeId = R.array.groupExpenseTypes;
                } else {
                    expenseTypeId = R.array.collectExpenseTypes;
                }
                ArrayAdapter<CharSequence> expenseTypeAdapter;
                expenseTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                        expenseTypeId, android.R.layout.simple_spinner_item);
                expenseTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                expenseTypeSpinner.setAdapter(expenseTypeAdapter);
                expenseTypeSpinner.setOnItemSelectedListener(expenseTypeListener);
            }
        });

        if (existingProducts.length == 0) {
            existingProductBTN.setEnabled(false);
        }

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
        if ((productNameText.equals("") && productNameET.getVisibility() == View.VISIBLE)
                || costText.equals("")
                || (percentageText.equals("") && percentageET.getVisibility() == View.VISIBLE)) {
            okButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);
        }
    }

    private AdapterView.OnItemSelectedListener productsListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedProduct = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
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

    private class GetExpenseProductsReqTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            String[] products = {};
            String productsType = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/expense/products/" +
                        productsType + "/trip/" + tripId;
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Expense[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Expense[].class);
                if (responseEntity.getBody() != null) {
                    products = new String[responseEntity.getBody().length];
                    Expense[] expenseList = responseEntity.getBody();
                    for (int i = 0; i < expenseList.length; i++) {
                        products[i] = expenseList[i].getProduct();
                    }
                } else {
                    products = new String[]{};
                }

            } catch (Exception e) {
                Log.e("ERROR-GET-PRODUCTS", e.getMessage());
            }

            return products;
        }

        @Override
        protected void onPostExecute(String[] products) {
        }
    }

}
