package com.venture.android.musicplayer;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by parkheejin on 2017. 2. 8..
 */

public class Message {
    public static void show(String msg, Context context){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
