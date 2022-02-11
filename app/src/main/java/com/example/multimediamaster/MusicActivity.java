package com.example.multimediamaster;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
*미디어 플레이어 클래스 : 멀티미디어를 위해 제공하는 미디어 API들이 들어있는 android.media 패키지의 핵심

*오디오 파일을 재생하려면 대상을 지정해야 하는데 이 때 사용되는 데이터 소스 지정 장법은 크게 3가지로 구분됨
 - 인터넷에 있는 파일 위치 지정 : 미디어가 있는 위치를 URL로 지정함
 - 프로젝트 파일에 포함한 후 위치 지정 : 프로젝트의 리소스 또는 asset 폴더에 넣은 후 그 위치를 지정함
 - 단말 SD 카드에 넣은 후 위치 지정

*미디어 플레이어로 음악 파일을 재생하는 3단계 과정
 - setDataSource()로 URL을 지정함으로써 대상 파일을 알려줌
 -> prepare()로 재생 준비함(대상 파일의 몇 프레임을 미리 읽어 들이고 정보를 확인함)
 -> start()로 파일을 재생함
 */
public class MusicActivity extends AppCompatActivity {

    MediaPlayer player;
    int position = 0; // 현재 재생 위치

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                killPlayer();

                player = MediaPlayer.create(MusicActivity.this, R.raw.fldan);
                player.start();
                tv.setText("~재생중~");
            }
        });

        Button btn2 = (Button)findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player != null) {
                    player.stop();
                    player.reset();
                    tv.setText("중지. ");
                }
            }
        });

        Button btn3 = (Button)findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player != null) {
                    position = player.getCurrentPosition(); // 어디까지 플레이 되었는지 position에 할당함
                    player.pause();
                    tv.setText("일시 정지!");
                }
            }
        });

        Button btn4 = (Button)findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player != null && !player.isPlaying()) { // 재생되는 중이 아니면
                    player.start();
                    player.seekTo(position); // 시작되는 시점은 position으로부터
                    tv.setText("~재생중~");
                }
            }
        });

        tv = (TextView)findViewById(R.id.tv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killPlayer();
    }

    private void killPlayer() { // 여러번 누를 경우 계속 생성되는거 방지함
        if(player != null) {
            try {
                player.release();; // Media Player 객체의 리소스 해제하기
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}