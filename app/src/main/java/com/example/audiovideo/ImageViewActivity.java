package com.example.audiovideo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ImageViewActivity extends AppCompatActivity {
    private Button switchBtn;
    private ImageView imageIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        switchBtn = (Button)findViewById(R.id.switchBtn);
        imageIV = (ImageView)findViewById(R.id.imageIV);
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageIV.setImageResource(R.drawable.sanshao);
            }
        });
    }
}
