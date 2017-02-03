package com.venture.android.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by parkheejin on 2017. 2. 1..
 */

public class DataLoader {

    // data를 두개의 activity에서 공유하기 위해 static 형태로 변경
    private static ArrayList<Music> data = new ArrayList<>();

    // static 변수인 data를 체크해서 null이면 load를 실행
    public static ArrayList<Music> get(Context context) {
        if (data == null || data.size() == 0) {
            load(context);
        }
        return data;
    }

    public static void load(Context context) {
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

                music.album_image  = getAlbumImageSimple(music.album_id);
                music.uri = getMusicUri(music.id);

                //music.bitmap_image = getAlbumImageBitmap(music.album_id);

                data.add(music);
            }
            // 6. 처리후 커서를 닫아준다
            cursor.close();
        }

    }
    // 음악 id로 uri를 가져오는 함수
    private static Uri getMusicUri(String music_id) {
        Uri content_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return Uri.withAppendedPath(content_uri,music_id);
    }

    // 가장 간단하게 앨범이미지를 가져오는 방법
    // 문제점 : 실제 앨범데이터만 있어서 이미지를 불러오지 못하는 경우가 있다.

    private static Uri getAlbumImageSimple(String album_id) {
        return Uri.parse("content://media/external/audio/albumart/" + album_id);
    }

    private Bitmap getAlbumImageBitmap(Context context, String album_id) {
        // 1. 앨범아이디로 Uri생성
        Uri uri = getAlbumImageSimple(album_id);
        // 2. 컨텐트 리졸버 가져오기
        ContentResolver resolver = context.getContentResolver();
        try {
            // 3. 리졸버에서 스트림열기
            InputStream is = resolver.openInputStream(uri);
            // 4. BitmapFactory를 통해 이미지 데이터를 가져온다.
            Bitmap image = BitmapFactory.decodeStream(is);
            // 5. 가져온 이미지를 리턴한다.
            return image;
        } catch(FileNotFoundException e){
            Logger.print(e.toString(),"로그위치");
        }
        return null;
    }

}

