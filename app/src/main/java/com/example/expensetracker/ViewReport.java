package com.example.expensetracker;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.expensetracker.domain.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

public class ViewReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int idTripParam = 0;
        if (getIntent() != null) {
            idTripParam = getIntent().getIntExtra("tripId", 0);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), idTripParam);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }
}