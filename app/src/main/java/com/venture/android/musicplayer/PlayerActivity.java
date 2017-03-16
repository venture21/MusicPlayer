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

import java.util.List;


public class PlayerActivity extends AppCompatActivity {

    ViewPager viewPager;
    ImageButton btnRew, btnPlay, btnFf;

    List<Music> data;
    PlayerAdapter adapter;

    MediaPlayer player;
    SeekBar seekBar;
    TextView txtDuration;
    TextView txtProgress;

    //Intent intent;

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

        mediaButtonInit();
        seekBarInit();
        viewPagerInit();
        callSelectPage();
    }

    private void settingInit(){
        playStatus = STOP;
        // 볼륨 조절 버튼으로 미디어 음량만 조절하기 위한 설정
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void mediaButtonInit() {
        btnRew      = (ImageButton) findViewById(R.id.btnRew);
        btnPlay     = (ImageButton) findViewById(R.id.btnPlay);
        btnFf       = (ImageButton) findViewById(R.id.btnFf);

        btnPlay.setOnClickListener(clickListener);
        btnRew.setOnClickListener(clickListener);
        btnFf.setOnClickListener(clickListener);
    }

    private void seekBarInit() {
        seekBar     = (SeekBar)  findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtProgress = (TextView) findViewById(R.id.txtProgress);
    }

    private void viewPagerInit() {
        // 0. 데이터 가져오기
        data = DataLoader.get(this);
        // 1. 뷰 페이저 가져오기
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        // 2. 뷰 페이저용 아답터 생성
        PlayerAdapter adapter = new PlayerAdapter(data, this);
        // 3. 뷰페이저 아답터 연결
        viewPager.setAdapter(adapter);
        // 4. 뷰페이지 리스너 연결 (페이지가 바뀌는 경우를 위한 리스너)
        viewPager.addOnPageChangeListener(viewPagerListener);

        viewPager.setPageTransformer(false, pageTransformer);
    }

    private void callSelectPage() {
        // 5. 특정 페이지 호출
        Intent intent = getIntent();
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            position = bundle.getInt("position");
            // 실제 페이지 값 계산 처리
            // 페이지 이동
            viewPager.setCurrentItem(position);
            // 첫 페이지 일 경우만 init 호출
            //  이유 : 첫페이지가 아닐 경우 위의 setCurrentItem에 의해서 ViewPager의 onPageSelected가 호출된다.
            if(position ==0)
                init();
            else // 0 페이지가 아닐경우 해당페이지로 이동한다 이동후 listener에서 init이 자동으로 호출된다.
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
            // 플레이어 상태를 STOP으로 변경
            playStatus = STOP;
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
            player.release();
        }

        playerInit();
        controllerInit();
        play();

    }

    private void playerInit() {
        Uri musicUri = data.get(position).uri;
        player = MediaPlayer.create(this, musicUri);
        player.setLooping(false);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
    }

    private void controllerInit(){
        // seekBar 길이
        seekBar.setMax(player.getDuration());
        // seekBar 현재값 0으로 설정
        seekBar.setProgress(0);
        // 곡의 전체 시간을 표시
        txtDuration.setText(convertMiliToTime(player.getDuration()));
        // 미디어 플레이어에 완료체크 리스너를 등록한다.
    }


    /**
     *  btnPlay 선택시
     */
    private void play() {
        switch(playStatus) {

            case STOP:
                playStop();
                break;

            case PLAY:
                playPlay();
                break;

            case PAUSE:
                playPause();
                break;
        }
    }

    private void playStop(){
        player.start();
        playStatus = PLAY;
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        // 새로운 쓰레드로 스타트
        Thread thread = new TimerThread();
        thread.start();
    }

    private void playPlay(){
        player.pause();
        playStatus = PAUSE;
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
    }

    private void playPause(){
        //player.seekTo(player.getCurrentPosition());
        player.start();
        playStatus = PLAY;
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
    }


    /**
     * btnRew 선택시
     */
    private void prev() {
        if(position>0)
            viewPager.setCurrentItem(position-1);
    }


    /**
     * btnFf 선택시
     */
    private void next() {
        if(position < data.size())
            viewPager.setCurrentItem(position + 1);
    }


    private String convertMiliToTime(long mili){
        long min = mili/ 1000 / 60;
        long sec = mili/ 1000 % 60;
        return String.format("%02d",min) + ":" + String.format("%02d",sec);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player != null){
            player.release(); // 사용이 끝나면 해제 해야 힌다.
        }
        playStatus = STOP;
    }


    /**
     * ViewPageChangeListener
     */
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Logger.print("onPageSelected=================", "새로운 페이지선택:");
            PlayerActivity.this.position = position;
            init();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };


    /**
     * seekBarChangeListener
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(player != null && fromUser)  {
                player.seekTo(progress);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    class TimerThread extends Thread {
        @Override
        public void run() {
            while (playStatus < STOP) {
                if(player != null) {
                    // 이 부분은 메인쓰레드에서 동작하도록 Runnable 객체를 메인쓰레드에 던져준다
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 플레이어가 도중에 종료되면(onDestroy메소드 호출 등...) 예외가 발생하면
                            try {
                                seekBar.setProgress(player.getCurrentPosition());
                                txtProgress.setText(convertMiliToTime(player.getCurrentPosition()));
                            } catch (Exception e) {
                                Logger.print("Inner play method thread : try catch Number 1", "Venture Thread catch ");
                            }
                        }
                    });
                }

                try { Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.print("Inner play method thread : try catch Number 2", "Venture Thread catch ");
                }
            }
        }
    };

    ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
        @Override
        public void transformPage(View page, float position) {
            float normalizedposition = Math.abs( 1 - Math.abs(position) );
            page.setAlpha(normalizedposition);  //View의 투명도 조절
            page.setScaleX(normalizedposition/2 + 0.5f); //View의 x축 크기조절
            page.setScaleY(normalizedposition/2 + 0.5f); //View의 y축 크기조절
            page.setRotationY(position * 80); //View의 Y축(세로축) 회전 각도
        }
    };
}
