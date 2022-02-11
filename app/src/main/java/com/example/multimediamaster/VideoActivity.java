package com.example.multimediamaster;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    public static final String VIDEO_URL = "링크";

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube);

        videoView = (VideoView)findViewById(R.id.videoView);

        MediaController controller = new MediaController(this);
        videoView.setMediaController(controller); // 비디오뷰에 컨트롤러 설정

        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.setVideoURI(Uri.parse(VIDEO_URL)); // 영상 파일 위치 확인
                videoView.requestFocus(); // 파일 정보의 일부를 가져옴
                videoView.start(); // 영상 재생
            }
       });
    }
}