package com.example.klachapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Activity_second extends AppCompatActivity {

    private MediaPlayer redAndroidSound;
    private MediaPlayer greenAndroidSound;
    private final FirstFragment firstFragment = new FirstFragment();
    private final SecondFragment secondFragment = new SecondFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Button backMain = findViewById(R.id.backMain);
        ImageButton redBtn = findViewById(R.id.redAndroid);
        ImageButton greenBtn = findViewById(R.id.greenAndroid);

        redAndroidSound = MediaPlayer.create(this, R.raw.android_sound_red);
        greenAndroidSound = MediaPlayer.create(this, R.raw.android_sound_green);

        Button btnFirstFragment = findViewById(R.id.fragmentFirst);
        Button btnSecondFragment = findViewById(R.id.fragmentSecond);

        Button btnBrowser = findViewById(R.id.browser);

        View.OnClickListener goBackMain = view -> welcomeMain();

        View.OnClickListener clickRedAndroid = view -> {
            toastForRed();
            soundPlayButton(redAndroidSound, greenAndroidSound);
        };

        View.OnClickListener clickGreenAndroid = view -> {
            toastForGreen();
            soundPlayButton(greenAndroidSound, redAndroidSound);
        };

        View.OnClickListener clickBrowser = view -> {
            goBrowser();
        };

        backMain.setOnClickListener(goBackMain);
        redBtn.setOnClickListener(clickRedAndroid);
        greenBtn.setOnClickListener(clickGreenAndroid);
        btnBrowser.setOnClickListener(clickBrowser);

        setNewFragment(firstFragment);

        btnFirstFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewFragment(firstFragment);
            }
        });

        btnSecondFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewFragment(secondFragment);
            }
        });

    }

    protected void onDestroy() {
        super.onDestroy();
        if (redAndroidSound != null) {
            redAndroidSound.release();
            redAndroidSound = null;
        }
        if (greenAndroidSound != null) {
            greenAndroidSound.release();
            greenAndroidSound = null;
        }
    }

    private void welcomeMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void toastForRed() {
        Toast.makeText(this, "Я красный Android", Toast.LENGTH_LONG).show();
    }

    private void toastForGreen() {
        Toast.makeText(this, "Я зелёный Android", Toast.LENGTH_LONG).show();
    }

    private void soundPlayButton(MediaPlayer soundFirst, MediaPlayer soundSecond) {
        if(soundFirst.isPlaying()) {
            soundFirst.pause();
            soundFirst.seekTo(0);
        }

        if(soundSecond.isPlaying()){
            soundSecond.pause();
            soundSecond.seekTo(0);
        }

        soundFirst.start();
    }

    private void setNewFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void goBrowser() {
        Intent intent = new Intent(this, WebActivity.class);
        startActivity(intent);
    }
}