package com.kekegdsz.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kekegdsz.imagepicker.ImageSelectActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_image_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ImageSelectActivity.start(MainActivity.this);
            }
        });
    }
}
