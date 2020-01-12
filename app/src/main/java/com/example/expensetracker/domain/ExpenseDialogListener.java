package com.example.expensetracker.domain;

public interface ExpenseDialogListener {
    void addExpenseToDB(String productName, String cost, String selectedCurrency, String expenseType);
}
