package com.example.expensetracker.domain.ui.main;

import android.os.AsyncTask;
import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.domain.Expense;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class PageViewModel extends ViewModel {

    private Integer idTrip;
    private Expense[] unperformedExpenses;
    private Expense[] allExpenses;

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();

    private LiveData<Expense[]> expenseList = Transformations.map(mIndex, new Function<Integer, Expense[]>() {
        @Override
        public Expense[] apply(Integer index) {
            if (index == 1) {
                return unperformedExpenses;
            }
            return allExpenses;
        }
    });

    public void setExpenseLists(Expense[] unperformedExpenses, Expense[] allExpenses) {
        this.unperformedExpenses = unperformedExpenses;
        this.allExpenses = allExpenses;
    }


    public void setParams(int index, int idTrip) {
        mIndex.setValue(index);
        this.idTrip = idTrip;
    }

    public LiveData<Expense[]> getLines() {
        return expenseList;
    }

//    private class GetUnperformedExpensesReqTask extends AsyncTask<Integer, Void, Expense[]> {
//
//        @Override
//        protected Expense[] doInBackground(Integer... params) {
//
//            Expense[] unperfExpenses = {};
//            int tripIdParam = params[0];
//            try {
//                String apiUrl = "http://10.0.2.2:8080/group-expensive-tracker/expense/report/trip/" + tripIdParam;
//                RestTemplate restTemplate = new RestTemplate();
//                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//                ResponseEntity<Expense[]> responseEntity = restTemplate.getForEntity(apiUrl, Expense[].class);
//                unperfExpenses = responseEntity.getBody();
//
//            } catch (Exception e) {
//                Log.e("ERROR-GET-UNP-EXPENSES", e.getMessage());
//            }
//            return unperfExpenses;
//        }
//
//        @Override
//        protected Expense[] onPostExecute(Expense[] result) {
//            unperformedExpenses = result;
//            return  result;
//        }
//    }


}
