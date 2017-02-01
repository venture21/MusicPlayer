package com.venture.android.musicplayer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by parkheejin on 2017. 2. 1..
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {
    ArrayList<Music> data;
    Context context;

    public MusicAdapter(ArrayList<Music> data, Context context) {
        this.data = data;
        this.context = context;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Music music = data.get(position);
        holder.txtTitle.setText(music.title);
        holder.txtArtist.setText(music.artist);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtTitle, txtArtist;
        ImageView image;

        public Holder (View itemView) {
            super(itemView);
            txtTitle  = (TextView) itemView.findViewById(R.id.txtTitle);
            txtArtist = (TextView) itemView.findViewById(R.id.txtArtist);
            image     = (ImageView) itemView.findViewById(R.id.image);
            cardView  = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}
