package com.example.sotjaxrsandroidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class CustomerAdapter extends BaseAdapter {

    // global reference of expected data type, String[]
    private ArrayList<Customer> customers;

    // global LayoutInflater reference
    private LayoutInflater layoutInflater;

    public CustomerAdapter(ArrayList<Customer> customerArrayList, Context context) {
        this.customers = customerArrayList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return customers.size();
    }

    @Override
    public Object getItem(int position) {
        return customers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return customers.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // check if the view being just created, ot it is the one which went out
        if(convertView == null){

            // if it is new, initialise it using 'adapter_row' layout
            convertView = layoutInflater.inflate(R.layout.adapter_row, parent, false);
        }

        // retrieve TextView with the id 'textView4'
        TextView tvName = (TextView) convertView.findViewById(R.id.adapter_row);

        // get the appropriate data from the array using position index
        // and set it to tvName TextView
        Customer cus = (Customer) getItem(position);
        tvName.setText("Id: " + cus.getId() + "\nName: " + cus.getName()
                        + "\nEmail: " + cus.getEmail()
                        + "\nBalance: " + round(cus.getBalance(),2)
                        + "\nOwned games: " + cus.getOwnedGames().size());

        // return the view
        return convertView;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
