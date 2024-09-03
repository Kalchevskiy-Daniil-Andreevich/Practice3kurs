package com.example.databaseapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "carbrands.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_COUNTRIES = "countries";
    public static final String COLUMN_COUNTRY_ID = "_id";
    public static final String COLUMN_COUNTRY_NAME = "country_name";

    public static final String TABLE_CAR_BRANDS = "car_brands";
    public static final String COLUMN_CAR_BRAND_ID = "_id";
    public static final String COLUMN_CAR_BRAND_NAME = "car_brand_name";
    public static final String COLUMN_COUNTRY_ID_FK = "country_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCountriesTable = "CREATE TABLE " + TABLE_COUNTRIES + " (" +
                COLUMN_COUNTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COUNTRY_NAME + " TEXT NOT NULL);";

        String createCarBrandsTable = "CREATE TABLE " + TABLE_CAR_BRANDS + " (" +
                COLUMN_CAR_BRAND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CAR_BRAND_NAME + " TEXT NOT NULL, " +
                COLUMN_COUNTRY_ID_FK + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_COUNTRY_ID_FK + ") REFERENCES " + TABLE_COUNTRIES + "(" + COLUMN_COUNTRY_ID + "));";

        db.execSQL(createCountriesTable);
        db.execSQL(createCarBrandsTable);

        db.execSQL("INSERT INTO " + TABLE_COUNTRIES + " (" + COLUMN_COUNTRY_NAME + ") VALUES ('Germany'), ('Japan'), ('USA'), ('Italy'), ('South Korea');");
        db.execSQL("INSERT INTO " + TABLE_CAR_BRANDS + " (" + COLUMN_CAR_BRAND_NAME + ", " + COLUMN_COUNTRY_ID_FK + ") VALUES ('BMW', 1), ('Audi', 1)," +
                "('Mercedes Benz', 1), ('Volkswagen', 1), ('Volkswagen', 1), ('Opel', 1), ('Toyota', 2), ('Honda', 2),  ('Nissan', 2), ('Mitsubishi', 2),  ('Mazda', 2), ('Subaru', 2)," +
                "('Ford', 3), ('Chevrolet', 3), ('Cadillac', 3), ('Lincoln', 3), ('Dodge', 3), ('Jeep', 3)," +
                "('Ferrari', 4), ('Lamborghini', 4), ('Fiat', 4), ('Maserati', 4), ('Alfa Romeo', 4), ('Iveco', 4)," +
                "('Hyundai', 5), ('Kia', 5), ('Daewoo', 5), ('Genesis', 5), ('SsangYong', 5);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR_BRANDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRIES);
        onCreate(db);
    }
}
