package com.venture.android.musicplayer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


public class MainActivity extends AppCompatActivity {
    private final int REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            init();
        }

    }

    // Runtime Permission 관련 부분은 안드로이드 6.0 마시멜로 버전 이후부터 사용 가능
    // 따라서 6.0 이전 버전들은 @TargetApi코드를 통해 컴파일을 하더라도 무시하는 것으로 처리된다.
    // 1. 권한체크
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        // 1.1 런타임 권한체크
        if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 1.2 요청할 권한 목록 작성
            String permArr[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
            // 1.3 시스템에 권한요청
            requestPermissions(permArr, REQ_CODE);
        }
        else {
            init();
        }
    }

    // 2. 권한체크 후 콜백 < 사용자가 확인후 시스템이 호출하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQ_CODE){
            // 배열에 넘긴 런타임권한을 체크해서 승인이 됐으면 프로그램 실행.
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                init();
            } else {
                Message.show("권한을 허용하지 않으시면 프로그램을 실행할 수 없습니다.",this);
                finish();
            }
        }
    }
    // 데이터를 로드할 함수
    private void init() {
        Message.show("프로그램을 실행합니다.",this);
        listInit();
    }

    private void listInit() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MusicAdapter adapter = new MusicAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}