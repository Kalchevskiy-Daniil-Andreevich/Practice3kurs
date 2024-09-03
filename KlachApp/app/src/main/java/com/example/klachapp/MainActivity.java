package com.example.klachapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView resultsTextView = findViewById(R.id.resultsTextView);
        EditText numberFieldFirst = findViewById(R.id.numberFieldFirst);
        EditText numberFieldSecond = findViewById(R.id.numberFieldSecond);
        Button buttonPlus = findViewById(R.id.buttonPlus);
        Button buttonMinus = findViewById(R.id.buttonMinus);
        Button buttonMultiply = findViewById(R.id.buttonMultiply);
        Button buttonDivide = findViewById(R.id.buttonDivide);
        Button buttonClear = findViewById(R.id.buttonClear);

        Button buttonInfo = findViewById(R.id.info_app);
        Button buttonClose = findViewById(R.id.closeApp);

        Button newActivity = findViewById(R.id.newActivity);


        View.OnClickListener operationListener = view -> {
            float numberFirst;
            float numberSecond;
            try {
                numberFirst = Float.parseFloat(numberFieldFirst.getText().toString());
                numberSecond = Float.parseFloat(numberFieldSecond.getText().toString());
            }
            catch (NumberFormatException error) {
                resultsTextView.setText("Некорректный ввод");
                return;
            }

            float result;
            int id = view.getId();
            if (id == R.id.buttonPlus) {
                result = numberFirst + numberSecond;
            } else if (id == R.id.buttonMinus) {
                result = numberFirst - numberSecond;
            } else if (id == R.id.buttonMultiply) {
                result = numberFirst * numberSecond;
            } else if (id == R.id.buttonDivide) {
                if (numberSecond != 0) {
                    result = numberFirst / numberSecond;
                } else {
                    resultsTextView.setText("Деление на ноль невозможно");
                    return;
                }
            } else {
                resultsTextView.setText("Неизвестная операция");
                return;
            }
            resultsTextView.setText(String.valueOf(result));
        };

        View.OnClickListener clearListener = view -> {
            numberFieldFirst.setText("");
            numberFieldSecond.setText("");
            resultsTextView.setText("");
        };

        View.OnClickListener informationApp = view -> {
            showInfoToast(resultsTextView.getText().toString(), buttonInfo);
        };

        View.OnClickListener startActivity = view ->{
            startNewActivity();
        };

        buttonPlus.setOnClickListener(operationListener);
        buttonMinus.setOnClickListener(operationListener);
        buttonMultiply.setOnClickListener(operationListener);
        buttonDivide.setOnClickListener(operationListener);
        buttonClear.setOnClickListener(clearListener);

        buttonInfo.setOnClickListener(informationApp);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoAlert("Вы точно хотите закрыть данное приложения?");
            }
        });

        newActivity.setOnClickListener(startActivity);
    }

    private void showInfoToast(String myText, Button btn) {
        btn.setText("Нажми ещё раз");
        Toast.makeText(this, myText, Toast.LENGTH_LONG).show();
    }

    private void showInfoAlert(String infoText){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Закрыть приложения")
                .setMessage(infoText)
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();;
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startNewActivity(){
        Intent intent = new Intent(this, Activity_second.class);
        startActivity(intent);
    }
}

