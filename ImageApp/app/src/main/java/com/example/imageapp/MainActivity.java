package com.example.imageapp;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Spinner imageSpinner;
    private Spinner formatSpinner;
    private TextView formatTextView;
    private Button saveButton;
    private Button closeButton;
    private Bitmap bitmap;
    private final int[] imageResources = { R.raw.google, R.raw.instagram, R.raw.youtube };
    private final String[] imageNames = { "google.png", "instagram.png", "youtube.png" };
    private int currentImageIndex = 0;
    private String currentImageFormat = "png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupSpinners();
        setupButtons();

        copyImagesToInternalStorage();
        loadSelectedImage(currentImageIndex);
    }

    private void initializeViews() {
        imageView = findViewById(R.id.imageView);
        imageSpinner = findViewById(R.id.imageSpinner);
        formatSpinner = findViewById(R.id.formatSpinner);
        formatTextView = findViewById(R.id.formatTextView);
        saveButton = findViewById(R.id.saveButton);
        closeButton = findViewById(R.id.closeButton);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> imageAdapter = ArrayAdapter.createFromResource(this,
                R.array.image_options, android.R.layout.simple_spinner_item);
        imageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSpinner.setAdapter(imageAdapter);

        imageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentImageIndex = position;
                loadSelectedImage(currentImageIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<CharSequence> formatAdapter = ArrayAdapter.createFromResource(this,
                R.array.format_options, android.R.layout.simple_spinner_item);
        formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatSpinner.setAdapter(formatAdapter);
    }

    private void setupButtons() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageInSelectedFormat();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void copyImagesToInternalStorage() {
        String internalStoragePath = getFilesDir().getAbsolutePath();

        for (String imageName : imageNames) {
            File outputFile = new File(internalStoragePath, imageName);
            if (outputFile.exists()) {
                showToast(imageName + " already exists in internal storage");
            } else {
                try {
                    InputStream inputStream = getResources().openRawResource(
                            getResources().getIdentifier(imageName.replace(".png", ""), "raw", getPackageName()));
                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.close();
                    inputStream.close();
                    showToast(imageName + " successfully copied to internal storage");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadSelectedImage(int position) {
        bitmap = BitmapFactory.decodeResource(getResources(), imageResources[position]);
        imageView.setImageBitmap(bitmap);
        updateFormatTextView();
    }

    private void saveImageInSelectedFormat() {
        String selectedFormat = formatSpinner.getSelectedItem().toString();
        String selectedImageName = imageSpinner.getSelectedItem().toString().toLowerCase();
        String fileName = selectedImageName + "." + selectedFormat.toLowerCase();

        File file = new File(getFilesDir(), fileName);

        if (file.exists()) {
            showToast("Image " + fileName + " already saved in this format");
        } else {
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);

                if (selectedFormat.equalsIgnoreCase("JPEG")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } else if (selectedFormat.equalsIgnoreCase("WEBP")) {
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
                } else if (selectedFormat.equalsIgnoreCase("PNG")) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } else if (selectedFormat.equalsIgnoreCase("BMP")) {
                    BitmapDrawable bmpDrawable = new BitmapDrawable(getResources(), bitmap);
                    bmpDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                }

                outputStream.flush();
                showToast("Image " + fileName + " saved successfully");
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Error saving image");
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        currentImageFormat = selectedFormat.toLowerCase();
        updateFormatTextView();
    }

    private void updateFormatTextView() {
        String imageName = imageNames[currentImageIndex];
        String format = currentImageFormat.toLowerCase();
        formatTextView.setText("Format: " + imageName.replace(".png", "." + format));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
