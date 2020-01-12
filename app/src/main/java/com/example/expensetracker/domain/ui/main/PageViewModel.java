package com.example.expensetracker.domain.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private Integer idTrip;

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String[]> reportTexts = Transformations.map(mIndex, new Function<Integer, String[]>() {
        @Override
        public String[] apply(Integer index) {

            String[] v1 = {"Unperformed 1 cu trip " + idTrip, "Unperformed 2 cu trip " + idTrip, "Unperformed 3 cu trip " + idTrip};
            String[] v2 = {"Report 1 cu trip " + idTrip, "Report 2 cu trip " + idTrip, "Report 3 cu trip " + idTrip};

            if (index == 1) {
                return v1;
            } else {
                return v2;
            }
//            return "Hello world from section: " + input;
        }
    });

    public void setParams(int index, int idTrip) {
        mIndex.setValue(index);
        this.idTrip = idTrip;
    }

    public LiveData<String[]> getLines() {
        return reportTexts;
    }



}
