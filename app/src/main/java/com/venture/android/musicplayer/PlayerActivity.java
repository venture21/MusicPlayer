package com.venture.android.musicplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;


public class PlayerActivity extends AppCompatActivity {

    ViewPager viewPager;
    ImageButton btnRew, btnPlay, btnFf;

    ArrayList<Music> data;
    PlayerAdapter adapter;

    MediaPlayer player;
    SeekBar seekBar;
    TextView txtDuration;
    TextView txtProgress;

    // mediaplayer의 현재상태
    private static final int PLAY=0;
    private static final int PAUSE=1;
    private static final int STOP=2;

    // 현재 플레이어 상태 초기화
    private static int playStatus = STOP;

    int position = 0;  // 현재 음악 위치

    // 핸들러 상태 플래그
    public static final int PROGRESS_SET = 101;


    /**
     * PlayActivity onCreate 메소드
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playStatus = STOP;
        // 볼륨 조절 버튼으로 미디어 음량만 조절하기 위한 설정
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        btnRew      = (ImageButton) findViewById(R.id.btnRew);
        btnPlay     = (ImageButton) findViewById(R.id.btnPlay);
        btnFf       = (ImageButton) findViewById(R.id.btnFf);
        seekBar     = (SeekBar)  findViewById(R.id.seekBar);
        txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtProgress = (TextView) findViewById(R.id.txtProgress);

        btnPlay.setOnClickListener(clickListener);
        btnRew.setOnClickListener(clickListener);
        btnFf.setOnClickListener(clickListener);

        // 0. 데이터 가져오기
        data = DataLoader.get(this);
        // 1. 뷰 페이저 가져오기
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        // 2. 뷰 페이저용 아답터 생성
        PlayerAdapter adapter = new PlayerAdapter(data, this);
        // 3. 뷰페이저 아답터 연결
        viewPager.setAdapter(adapter);
        // 4. 뷰페이지 리스너 연결 (페이지가 바뀌는 경우를 위한 리스너)
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Logger.print("onPageSelected=================","새로운 페이지선택:");
                PlayerActivity.this.position = position;
                init();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 5. 특정 페이지 호출
        Intent intent = getIntent();
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            position = bundle.getInt("position");
            // 실제 페이지 값 계산 처리
            // 페이지 이동
            viewPager.setCurrentItem(position);
            if(position ==0)
                init();
            else
                viewPager.setCurrentItem(position);
        }
    }


    /**
     *  PlayerActivity에서 발생하는 이벤트
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnPlay:
                    play();
                    break;
                case R.id.btnRew:
                    prev();
                    break;
                case R.id.btnFf:
                    next();
                    break;
            }
        }
    };


    /**
     * PlayerActivity에서 MediaPlayer를 사용하기 위한 초기화 메소드
     */
    private void init () {
        // 뷰페이저로 이동할 경우 플레이어에 세팅된 값을 해제한 후 로직으 실행한다.
        if(player != null) {
            player.release();
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
            player.release();
        }
        int timeSec, timeMin;
        Uri musicUri = data.get(position).uri;
        player = MediaPlayer.create(this, musicUri);
        player.setLooping(false);

        // seekBar 길이
        seekBar.setMax(player.getDuration());
        // seekBar 현재값 0으로 설정
        seekBar.setProgress(0);
        //
        timeSec = player.getDuration()/1000;
        timeMin = timeSec/60;
        timeSec = timeSec%60;
        txtDuration.setText(timeMin + ":" + timeSec);
    }

    /**
     *  btnPlay 선택시
     */
    private void play() {
        switch(playStatus) {
            case STOP:

                player.start();
                playStatus = PLAY;
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);

                // sub thread 를 생성해서 mediaplayer 의 현재 포지션 값으로 매 1초마다 seekbar를 변경해준다.
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        while (playStatus < STOP) {
                            if (player != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        seekBar.setProgress(player.getCurrentPosition());
                                        dispProgress();
                                    }
                                });
                            }
                            try { Thread.sleep(1000); } catch (InterruptedException e) {
                            }
                        }
                    }
                };
                thread.start();
                break;
            case PLAY:
                player.pause();
                playStatus = PAUSE;
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
                break;
            case PAUSE:
                //player.seekTo(player.getCurrentPosition());
                player.start();
                playStatus = PLAY;
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                break;
        }
    }

    /**
     * btnRew 선택시
     */
    private void prev() {

    }

    /**
     * btnFf 선택시
     */
    private void next() {

    }

    /**
     * txtProgress의 시간표시를 위한 메소드
     */
    private void dispProgress(){
        int timeSec, timeMin;
        timeSec = player.getCurrentPosition()/1000;
        timeMin = timeSec/60;
        timeSec = timeSec%60;
        if(timeMin<10 && timeSec>9)
            txtProgress.setText("0"+timeMin + ":" + timeSec);
        else if (timeMin>9 && timeSec<10)
            txtProgress.setText(timeMin + ":0" + timeSec);
        else if (timeMin<10 && timeSec<10)
            txtProgress.setText("0"+timeMin + ":0" + timeSec);
        else
            txtProgress.setText(timeMin + ":" + timeSec);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player != null){
            player.release(); // 사용이 끝나면 해제 해야 힌다.
        }
        playStatus = STOP;
    }
}
