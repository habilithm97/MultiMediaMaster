package com.example.multimediamaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ViewUtils;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.File;
/*

 */

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {

    ImageView img;

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn2 = (Button)findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PreViewActivity.class);
                startActivity(intent);
            }
        });

        AutoPermissions.Companion.loadAllPermissions(this, 101);

        img = (ImageView)findViewById(R.id.img);

        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    public void takePicture() { // 먼저 파일 생성 후, 카메라 앱에서 사진 찍으면 그 파일에 저장할 것임
        if(file == null) { // 파일이 없으면
            file = createFile(); // 파일 생성
        }

        Uri fileUri = FileProvider.getUriForFile(this, "org.techtown.capture.intent.fileprovider", file); // File 객체로부터 Uri 객체 생성, 카메라 앱에서 공유하여 사용할 수
        // 있는 파일 정보를 Uri 객체로 만들 수 있음
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 미리 인텐트 객체에 정의된 액션 정보 -> 시스템에게 사진앱을 실행해달라고 요청함
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // 파일에서부터 Uri 정보를 하나 생성해서 설정함, EXTRA_OUTPUT은 어떤 파일로 저장할건지 지정할 수 있음

        if(intent.resolveActivity(getPackageManager()) != null) { // 카메라 앱 유무 확인, 카메라 앱이 있으면
            startActivityForResult(intent, 101); // 카메라 앱 화면 띄우기
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 카메라 앱으로 사진 촬영 후 카메라 액티비티를 닫으면 호출됨
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) { // 정상적으로 사진을 찍으면
            BitmapFactory.Options options = new BitmapFactory.Options(); // 이미지 파일을 비트맵 객체로 생성함
            options.inSampleSize = 8; // 1/8 크기로 비트맵 객체 생성
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options); // 파일을 디코딩해서 비트맵 객체로 생성
            img.setImageBitmap(bitmap);
        }
    }

    private File createFile() {
        String fileName = "capture.jpg"; // sd카드 파일
        File storageDir = Environment.getExternalStorageDirectory();
        File outFile = new File(storageDir, fileName);

        return outFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int i, String[] permissions) {
        Toast.makeText(this, "권한이 거부되었습니다. : " + permissions.length, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGranted(int i, String[] permissions) {
        Toast.makeText(this, "권한이 승인되었습니다. : " + permissions.length, Toast.LENGTH_SHORT).show();
    }
}