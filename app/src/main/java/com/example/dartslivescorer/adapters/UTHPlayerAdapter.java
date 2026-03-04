package com.example.dartslivescorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dartslivescorer.R;

import java.util.List;

import com.example.dartslivescorer.enums.eHats;
import models.gamesModels.UTHPlayerItem;

public class UTHPlayerAdapter extends BaseAdapter {

    private Context context;
    private List<UTHPlayerItem> playerList;

    public UTHPlayerAdapter(Context context, List<UTHPlayerItem> playerList) {
        this.context = context;
        this.playerList = playerList;
    }

    @Override
    public int getCount() {
        return playerList.size();
    }

    @Override
    public Object getItem(int position) {
        return playerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UTHPlayerAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.grid_uth_player_item, parent, false);

            viewHolder = new UTHPlayerAdapter.ViewHolder();
            viewHolder.playerIdTextView = convertView.findViewById(R.id.playerIdTextView);
            viewHolder.valuesImageViews = new ImageView[5];
            viewHolder.valuesImageViews[0] = convertView.findViewById(R.id.valeur_une);
            viewHolder.valuesImageViews[1] = convertView.findViewById(R.id.valeur_deux);
            viewHolder.valuesImageViews[2] = convertView.findViewById(R.id.valeur_trois);
            viewHolder.valuesImageViews[3] = convertView.findViewById(R.id.valeur_quatre);
            viewHolder.valuesImageViews[4] = convertView.findViewById(R.id.valeur_cinq);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (UTHPlayerAdapter.ViewHolder) convertView.getTag();
        }

        UTHPlayerItem playerItem = playerList.get(position);

        viewHolder.playerIdTextView.setText(playerItem.getInfos());

        int drawableId = getDrawableId(playerItem.getChapeau());
        int value = playerItem.getNb_chapeaux();

        for (int i = 0; i < viewHolder.valuesImageViews.length; i++) {
            if (i <= value-1)
                viewHolder.valuesImageViews[i].setImageResource(drawableId);
            else {
                viewHolder.valuesImageViews[i].setImageResource(0);
                viewHolder.valuesImageViews[i].setBackgroundColor(0);
            }
        }

        return convertView;
    }

    private int getDrawableId(eHats value) {
        switch (value) {
            case chapeau_hp:
                return R.drawable.chapeau_hp;
            case chapeau_dv:
                return R.drawable.chapeau_dv;
            case chapeau_pi:
                return R.drawable.chapeau_pi;
            case chapeau_wt:
                return R.drawable.chapeau_wt;
            case chapeau_re:
                return R.drawable.chapeau_re;
            case chapeau_ij:
                return R.drawable.chapeau_ij;
            case chapeau_mk:
                return R.drawable.chapeau_mk;
            case chapeau_ma:
                return R.drawable.chapeau_ma;
            case chapeau_ba:
                return R.drawable.chapeau_ba;
        }

        return 0;
    }

    static class ViewHolder {
        TextView playerIdTextView;
        ImageView[] valuesImageViews;
    }

}
