package com.example.expensetracker.domain.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.expensetracker.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TRIP_ID = "trip_id";

    private PageViewModel pageViewModel;
    private ArrayAdapter<String> adapterForReport;


    private Integer idTrip;


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
        View root = inflater.inflate(R.layout.fragment_view_report, container, false);
        final ListView reportList = root.findViewById(R.id.reportList);

        pageViewModel.getLines().observe(this, new Observer<String[]>() {
            @Override
            public void onChanged(@Nullable String[] reportString) {

                // Adding item to list view
                adapterForReport = new ArrayAdapter<String>(requireContext(), R.layout.list_item, R.id.trip_name, reportString);
                reportList.setAdapter(adapterForReport);

            }
        });

        return root;
    }
}