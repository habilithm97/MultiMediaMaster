package com.example.multimediamaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

/*
*카메라 미리보기 기능을 구현하려면 일반 뷰가 아닌 서피스뷰를 사용해야함
*서피스뷰(SurfaceView)
 -서피스뷰는 서피스 홀더 객체에 의해 생성되고 제어됨
 */
public class PreViewActivity extends AppCompatActivity implements AutoPermissionsListener {

    CameraSurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_view);

        AutoPermissions.Companion.loadAllPermissions(this, 101);

        FrameLayout previewFrame = findViewById(R.id.previewFrame);
        surfaceView = new CameraSurfaceView(this);
        previewFrame.addView(surfaceView); // 서피스뷰를 프레임 레이아웃에 표시함

        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int i, String[] permissions) {
        Toast.makeText(this, "권한 거부 : " + permissions.length, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGranted(int i, String[] permissions) {
        Toast.makeText(this, "권한 승인 : " + permissions.length, Toast.LENGTH_SHORT).show();
    }

    class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

        private SurfaceHolder holder; // 서피스 홀더
        private Camera camera = null; // 카메라 객체

        public CameraSurfaceView(Context context) {
            super(context);

            holder = getHolder(); // 서피스뷰 내에 있는 서피스 홀더 객체 참조
            holder.addCallback(this); // 콜백 인터페이스가 등록되도록
        }

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
            camera = Camera.open(); // 카메라 객체 참조
            setCameraOri();

            try {
                camera.setPreviewDisplay(holder); // 카메라 객체에 이 서피스 뷰를 미리보기 화면으로 사용
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) { // 화면에 보여지기 전에 크기를 결정
            camera.startPreview(); // 미리보기 화면에 픽셀을 막 뿌리기 시작함
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
            camera.stopPreview(); // 미리보기는 많은 자원을 먹기 때문에 중지시킴
            camera.release(); // 리소스가 사라짐
            camera = null; // 카메라 해제
        }

        public boolean capture(Camera.PictureCallback handler) { // 콜백 객체
            if(camera != null) {
                camera.takePicture(null, null, handler); // 사진 촬영
                return true;
            } else {
                return false;
            }
        }
        public void setCameraOri() {
            if(camera == null) {
                return;
            }
            /*
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(0, info);

            WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            int rotation = manager.getDefaultDisplay().getRotation(); // 회전 정보 확인

            int degrees = 0;

            switch (rotation) {
                case Surface.ROTATION_0 : degrees = 0; break;
                case Surface.ROTATION_90 : degrees = 90; break;
                case Surface.ROTATION_180 : degrees = 180; break;
                case Surface.ROTATION_270 : degrees = 270; break;
            }

            int result;
            if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            } */

            camera.setDisplayOrientation(90);
        }
    }

    public void takePicture() {
        surfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) { // 사진을 찍으면 캡쳐한 이미지 데이터터가 파라미터인 data에 전달 -> data는 비트맵 객체로 만들거임

                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length); // 전달받은 바이트 배열을 비트맵 객체로 생성
                    String outUriStr = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "캡쳐된 이미지", "카메라를 사용한 캡쳐된 이미지"); // 미디어 앨범에 추가

                    if(outUriStr == null) {
                        Log.d("캡쳐 샘플. ", "이미지 추가 실패. ");
                        return;

                    } else {
                        Uri outUri = Uri.parse(outUriStr);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, outUri));
                        // ACTION_MEDIA_SCANNER_SCAN_FILE : 파일 시스템에 파일을 추가할 때 특정 미디어 파일 스캔 처리를 위해 이미지를 저장 후 갤러리에 업데이트함
                    }
                    camera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}