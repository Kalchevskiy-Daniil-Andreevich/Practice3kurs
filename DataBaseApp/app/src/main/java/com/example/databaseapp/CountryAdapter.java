package com.example.databaseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CountryAdapter extends ArrayAdapter<Country> {

    public CountryAdapter(Context context, List<Country> countries) {
        super(context, 0, countries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Country country = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(country.getName());
        return convertView;
    }
}
