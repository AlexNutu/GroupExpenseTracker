package com.example.expensetracker.domain.ui.main;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.expensetracker.LoginActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.adapter.ViewReportAdapter;
import com.example.expensetracker.domain.Expense;
import com.example.expensetracker.helper.DatabaseHelper;
import com.example.expensetracker.helper.NetworkStateChecker;
import com.example.expensetracker.helper.Session;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TRIP_ID = "trip_id";

    private PageViewModel pageViewModel;
    private ArrayAdapter<Expense> adapterForReport;
    private ListView reportList;

    private Expense[] unperformedExpenses;

    private Integer idTrip;

    private Session session;
    private DatabaseHelper db;

    public static PlaceholderFragment newInstance(int index, int idTrip) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        bundle.putInt(ARG_TRIP_ID, idTrip);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new Session(getContext());
        db = DatabaseHelper.getInstance(this.getContext());
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            idTrip = getArguments().getInt(ARG_TRIP_ID);

        }
        pageViewModel.setParams(index, idTrip);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_view_report, container, false);

        reportList = root.findViewById(R.id.reportListLV);
        if(NetworkStateChecker.isConnected(this.getContext())) {
            new GetUnperformedExpensesReqTask().execute(idTrip);
        }
        else {
            ArrayList<Expense> reported = db.getUnperformedReportList(idTrip);
            if(reported!=null)
                callGetAllExpensesRequests(reported.toArray(new Expense[0]));
            else
                callGetAllExpensesRequests(new Expense[0]);
        }
        return root;
    }

    private class GetUnperformedExpensesReqTask extends AsyncTask<Integer, Void, Expense[]> {

        @Override
        protected Expense[] doInBackground(Integer... params) {

            Expense[] unperfExpenses = {};
            int tripIdParam = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/expense/report/trip/" + tripIdParam;
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Expense[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Expense[].class);
                unperfExpenses = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-UNP-EXPENSES", e.getMessage());
            }
            return unperfExpenses;
        }

        @Override
        protected void onPostExecute(Expense[] result) {
            callGetAllExpensesRequests(result);
        }
    }

    public void callGetAllExpensesRequests(Expense[] unperformedExpenses) {
        this.unperformedExpenses = unperformedExpenses;
        if(NetworkStateChecker.isConnected(this.getContext())) {
            new GetAllExpensesReqTask().execute(idTrip);
        }
        else {
             Expense[] allExpenses = {};
             ArrayList<Expense> expenses = db.getExpenseReportList(idTrip);
             if(expenses != null)
                allExpenses = expenses.toArray(new Expense[0]);
             else
                 allExpenses = new Expense[0];
             populateListView(allExpenses);
        }
    }

    private class GetAllExpensesReqTask extends AsyncTask<Integer, Void, Expense[]> {

        @Override
        protected Expense[] doInBackground(Integer... params) {

            Expense[] allExpenses = {};
            int tripIdParam = params[0];
            try {
                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/expense?search=trip:" + tripIdParam;
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + session.getCookie());
                HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<Expense[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, Expense[].class);
                allExpenses = responseEntity.getBody();

            } catch (Exception e) {
                Log.e("ERROR-GET-ALL-EXPENSES", e.getMessage());
            }
            return allExpenses;
        }

        @Override
        protected void onPostExecute(Expense[] result) {
            populateListView(result);
        }
    }


    public void populateListView(Expense[] allExpenses) {
        pageViewModel.setExpenseLists(this.unperformedExpenses, allExpenses);

        pageViewModel.getLines().observe(this, new Observer<Expense[]>() {
            @Override
            public void onChanged(@Nullable Expense[] expensesList) {

                // Adding item to list view
//                adapterForReport = new ArrayAdapter<String>(requireContext(), R.layout.fragment_view_report, R.id.report_item_layout, reportString);
                adapterForReport = new ViewReportAdapter(requireContext(), R.layout.report_list_item, expensesList);
                reportList.setAdapter(adapterForReport);
            }
        });
    }
}