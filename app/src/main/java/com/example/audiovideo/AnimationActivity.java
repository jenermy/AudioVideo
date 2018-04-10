package com.example.audiovideo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AnimationActivity extends AppCompatActivity {
    private PicturePlayerView picturePlayerView;
    private Button startBtn;
    String[] paths = {
            "/assets/dog7.png",
            "/assets/dog1.jpg",
            "/assets/dog2.jpg",
            "/assets/dog3.jpg",
            "/assets/dog4.jpg",
            "/assets/dog5.jpg",
            "/assets/dog6.png",
            "/assets/four.png",
            "/assets/ic_launcher_round.png",
            "/assets/junjie1.png",
            "/assets/junjie2.jpg",
            "/assets/junjie3.jpeg",
            "/assets/junjie4.png",
            "/assets/junjie5.jpg",
            "/assets/junjie6.png",
            "/assets/junjie7.jpg",
            "/assets/junjie8.png",
            "/assets/junjie9.png",
            "/assets/junjie10.jpeg",
            "/assets/junjie11.jpg",
            "/assets/junjie12.jpg",
            "/assets/monster.jpg",
            "/assets/pikaqiu.png",
            "/assets/push.png",
            "/assets/push_smll.png",
            "/assets/three.jpg",
            "/assets/two.jpg",
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        picturePlayerView = (PicturePlayerView)findViewById(R.id.picturePlayerView);
        startBtn = (Button)findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picturePlayerView.start(paths,1000);
            }
        });
    }
}
