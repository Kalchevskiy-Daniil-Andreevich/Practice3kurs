package com.example.databaseapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView countryListView;
    private ListView carBrandListView;
    private CountryAdapter countryAdapter;
    private CarBrandAdapter carBrandAdapter;
    private MapView mapView;
    private GoogleMap googleMap;

    private Map<String, LatLng> countryCoordinates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        countryListView = findViewById(R.id.countryListView);
        carBrandListView = findViewById(R.id.carBrandListView);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        initCountryCoordinates();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                        return false;
                    }
                });
            }
        });

        loadCountries();
    }

    private void initCountryCoordinates() {
        countryCoordinates.put("Germany", new LatLng(51.1657, 10.4515));
        countryCoordinates.put("Japan", new LatLng(36.2048, 138.2529));
        countryCoordinates.put("USA", new LatLng(39.8283, -101.2795));
        countryCoordinates.put("Italy", new LatLng(41.8719, 12.5674));
        countryCoordinates.put("South Korea", new LatLng(35.9078, 127.7669));
    }

    private void loadCountries() {
        List<Country> countries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_COUNTRIES, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COUNTRY_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COUNTRY_NAME));
            countries.add(new Country(id, name));
        }
        cursor.close();

        countryAdapter = new CountryAdapter(this, countries);
        countryListView.setAdapter(countryAdapter);

        countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Country selectedCountry = countryAdapter.getItem(position);
                loadCarBrands(selectedCountry.getId());

                LatLng coordinates = countryCoordinates.get(selectedCountry.getName());
                if (coordinates != null) {
                    googleMap.clear();
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(coordinates)
                            .title(selectedCountry.getName());

                    googleMap.addMarker(markerOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 5));

                    mapView.setVisibility(View.VISIBLE);
                    mapView.onResume();
                }
            }
        });
    }

    private void loadCarBrands(long countryId) {
        List<CarBrand> carBrands = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_COUNTRY_ID_FK + " = ?";
        String[] selectionArgs = {String.valueOf(countryId)};

        Cursor cursor = db.query(DatabaseHelper.TABLE_CAR_BRANDS, null, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAR_BRAND_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAR_BRAND_NAME));
            carBrands.add(new CarBrand(id, name));


            addMarkerToMap(name);
        }
        cursor.close();

        carBrandAdapter = new CarBrandAdapter(this, carBrands);
        carBrandListView.setAdapter(carBrandAdapter);
    }

    private void addMarkerToMap(String carBrandName) {
        if (googleMap != null) {
            LatLng markerPosition = new LatLng(0, 0);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(markerPosition)
                    .title(carBrandName);

            googleMap.addMarker(markerOptions);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 10));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
