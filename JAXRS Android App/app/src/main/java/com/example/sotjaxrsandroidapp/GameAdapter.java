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

public class GameAdapter extends BaseAdapter {

    // global reference of expected data type, String[]
    private ArrayList<Game> games;

    // global LayoutInflater reference
    private LayoutInflater layoutInflater;

    public GameAdapter(ArrayList<Game> gameArrayList, Context context) {
        this.games = gameArrayList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Object getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return games.get(position).getId();
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
        Game game = (Game) getItem(position);
        tvName.setText("Id: " + game.getId() + "\nName: " + game.getName()
                        + "\nGenre: " + game.getGenre().toString()
                        + "\nPrice: " + round(game.getPrice(),2));

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
