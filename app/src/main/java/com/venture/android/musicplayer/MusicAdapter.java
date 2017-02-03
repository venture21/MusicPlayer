package com.venture.android.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by parkheejin on 2017. 2. 1..
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {
    ArrayList<Music> data;
    Context context;
    Intent intent = null;

    public MusicAdapter(ArrayList<Music> data, Context context) {
        this.data = data;
        this.context = context;
        intent = new Intent(context, PlayerActivity.class);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    View.OnClickListener clickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, PlayerActivity.class);
            context.startActivity(intent);
        }
    };

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Music music = data.get(position);
        holder.txtTitle.setText(music.title);
        holder.txtArtist.setText(music.artist);
        holder.position = position;
        //holder.image.setImageURI(music.album_image);
        Glide.with(context)
                .load(music.album_image) // 1. 로드할 대상 Uri
                .into(holder.image);     // 2. 입력될 이미지뷰
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtTitle, txtArtist;
        ImageView image;
        int position;

        public Holder (View itemView) {
            super(itemView);
            txtTitle  = (TextView) itemView.findViewById(R.id.txtTitle);
            txtArtist = (TextView) itemView.findViewById(R.id.txtArtist);
            image     = (ImageView) itemView.findViewById(R.id.image);
            cardView  = (CardView) itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });
        }
    }
}
