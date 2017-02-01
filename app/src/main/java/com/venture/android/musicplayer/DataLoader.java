package com.venture.android.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;



/**
 * Created by parkheejin on 2017. 2. 1..
 */

public class DataLoader {

    private ArrayList<Music> data = new ArrayList<>();
    private Context context;

    public DataLoader(Context context) {
        this.context = context;
    }


    public ArrayList<Music> get() {
        return data;
    }


    public void load() {
        // 1. 데이터에 접근하기위해 ContentResolver 를 불러온다.
        ContentResolver resolver = context.getContentResolver();

        // 2. 데이터 컨텐츠 URI 정의
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // 3. 데이터에서 가져올 데이터 컬럼명을 String 배열에 담는다.
        //    데이터컬럼명은 Content Uri의 패키지에 들어있다.
        String proj[] = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        // 4. Content Resolver 로 쿼리한 데이터를 Cursor 에 담는다.
        Cursor cursor = resolver.query(uri, proj, null, null, null);

        // 5. Cursor 에 담긴 데이터를 반복문을 돌면서 꺼낸다
        if(cursor != null) {
            while(cursor.moveToNext()){
                Music music = new Music();
                // 데이터
                int idx = cursor.getColumnIndex(proj[0]);
                music.id = cursor.getString(idx);
                idx = cursor.getColumnIndex(proj[1]);
                music.album_id = cursor.getString(idx);
                idx = cursor.getColumnIndex(proj[2]);
                music.title = cursor.getString(idx);
                idx = cursor.getColumnIndex(proj[3]);
                music.artist = cursor.getString(idx);
                data.add(music);

            }
            // 6. 처리후 커서를 닫아준다
            cursor.close();
        }

    }
}

