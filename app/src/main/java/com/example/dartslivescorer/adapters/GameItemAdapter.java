package com.example.dartslivescorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dartslivescorer.R;

import java.util.List;

import models.commonModels.GameItem;

public class GameItemAdapter extends BaseAdapter {

    private Context context;
    private List<GameItem> gameList;
    private LayoutInflater inflater;

    public GameItemAdapter(Context context,List<GameItem> gameList)
    {
        this.context = context;
        this.gameList = gameList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return gameList.size();
    }

    @Override
    public Object getItem(int position) {
        return gameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.grid_game_item,null);

        GameItem currentItem = (GameItem) getItem(position);
        String name = currentItem.getName();
        Integer color = currentItem.getTours();

        TextView gameNameView = convertView.findViewById(R.id.item_game);
        gameNameView.setText(name);
        //gameNameView.setTextColor(color);

        return convertView;
    }
}
