package com.example.expensetracker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.expensetracker.R;
import com.example.expensetracker.domain.Trip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TripListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private ValueFilter valueFilter;
    private List<Trip> mTripList;
    private ArrayList<Trip> mTripForFiltering = new ArrayList<Trip>();

    public TripListAdapter(@NonNull Context context, @NonNull Trip[] trips) {
        mContext = context;
        mTripList = new ArrayList<>();
        mTripList.addAll(Arrays.asList(trips));
        mTripForFiltering.addAll(Arrays.asList(trips));
    }

    public void addListItemToAdapter(List<Trip> tripList) {
        mTripList.addAll(tripList);
        mTripForFiltering.addAll(tripList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTripList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTripList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = View.inflate(mContext, R.layout.trip_list_item, null);
        TextView tvName = (TextView) view.findViewById(R.id.tripNameItemTV);
        TextView tvDestination = (TextView) view.findViewById(R.id.tripDestinationItemTV);
        TextView tvStartDate = (TextView) view.findViewById(R.id.startDateItemTV);
        TextView tvEndDate = (TextView) view.findViewById(R.id.endDateItemTV);

        tvName.setText(mTripList.get(position).getName());
        tvDestination.setText(mTripList.get(position).getDestination());
        String shortStartDate = mTripList.get(position).getStartDate();//.substring(0, 10);
        String shortEndDate = mTripList.get(position).getEndDate();//.substring(0, 10);
        tvStartDate.setText(shortStartDate);
        tvEndDate.setText(shortEndDate);

        view.setTag(mTripList.get(position).getId());
        return view;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {

            valueFilter = new ValueFilter();
        }

        return valueFilter;
    }

    private class ValueFilter extends Filter {

        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Trip> filterList = new ArrayList<Trip>();
                for (int i = 0; i < mTripForFiltering.size(); i++) {
                    if ((mTripForFiltering.get(i).getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        Trip trip = new Trip();
                        trip.setName(mTripForFiltering.get(i).getName());
                        trip.setDestination(mTripForFiltering.get(i).getDestination());
                        trip.setStartDate(mTripForFiltering.get(i).getStartDate());
                        trip.setEndDate(mTripForFiltering.get(i).getEndDate());
                        trip.setId(mTripForFiltering.get(i).getId());
                        filterList.add(trip);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mTripForFiltering.size();
                results.values = mTripForFiltering;
            }
            return results;
        }


        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mTripList = (ArrayList<Trip>) results.values;
            notifyDataSetChanged();
        }
    }
}
