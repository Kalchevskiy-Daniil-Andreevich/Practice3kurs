package com.example.databaseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CarBrandAdapter extends ArrayAdapter<CarBrand> {

    public CarBrandAdapter(Context context, List<CarBrand> carBrands) {
        super(context, 0, carBrands);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CarBrand carBrand = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(carBrand.getName());
        return convertView;
    }
}
