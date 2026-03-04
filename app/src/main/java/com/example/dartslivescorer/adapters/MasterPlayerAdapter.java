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

import models.gamesModels.CricketPlayerItem;
import models.commonModels.LanceItem;

public class MasterPlayerAdapter extends BaseAdapter {
    private Context context;
    private List<CricketPlayerItem> playerList;

    public MasterPlayerAdapter(Context context, List<CricketPlayerItem> playerList) {
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
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.grid_cricket_player_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.playerIdTextView = convertView.findViewById(R.id.playerIdTextView);
            viewHolder.dartsTextViews = new TextView[3];
            viewHolder.dartsTextViews[0] = convertView.findViewById(R.id.flechette_une);
            viewHolder.dartsTextViews[1] = convertView.findViewById(R.id.flechette_deux);
            viewHolder.dartsTextViews[2] = convertView.findViewById(R.id.flechette_trois);
            viewHolder.valuesImageViews = new ImageView[7];
            viewHolder.valuesImageViews[0] = convertView.findViewById(R.id.valeur_une);
            viewHolder.valuesImageViews[1] = convertView.findViewById(R.id.valeur_deux);
            viewHolder.valuesImageViews[2] = convertView.findViewById(R.id.valeur_trois);
            viewHolder.valuesImageViews[3] = convertView.findViewById(R.id.valeur_quatre);
            viewHolder.valuesImageViews[4] = convertView.findViewById(R.id.valeur_cinq);
            viewHolder.valuesImageViews[5] = convertView.findViewById(R.id.valeur_six);
            viewHolder.valuesImageViews[6] = convertView.findViewById(R.id.valeur_sept);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CricketPlayerItem playerItem = playerList.get(position);

        viewHolder.playerIdTextView.setText(playerItem.getInfos());

        for (int i = 0; i < viewHolder.valuesImageViews.length; i++) {
            int value = playerItem.getValues().get(i + 1);
            boolean active = playerItem.isSelected();

            int drawableId = getDrawableId(value, active);
            if (drawableId != 0)
                viewHolder.valuesImageViews[i].setImageResource(drawableId);
            else
                viewHolder.valuesImageViews[i].setImageResource(0); // Réinitialiser l'image si non valide
        }

        LanceItem lance = playerItem.getLance();
        if (lance != null) {
            updateDartTextViews(viewHolder.dartsTextViews, lance);
        } else {
            // Si aucune lance n'est enregistrée pour ce joueur, masquer les TextViews des fléchettes
            for (TextView dartTextView : viewHolder.dartsTextViews) {
                dartTextView.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    private int getDrawableId(int value,boolean active) {
        if(active) {
            switch (value) {
                case 1:
                    return R.drawable.puce_un_jaune;
                case 2:
                    return R.drawable.puce_deux_jaune;
                case 3:
                    return R.drawable.puce_trois_jaune;
            }
        }
        else
        {
            switch (value) {
                case 1:
                    return R.drawable.puce_un_blanc;
                case 2:
                    return R.drawable.puce_deux_blanc;
                case 3:
                    return R.drawable.puce_trois_blanc;
            }
        }
        return 0;
    }

    private void updateDartTextViews(TextView[] dartsTextViews, LanceItem lance) {
        for (int i = 0; i < dartsTextViews.length; i++) {
            TextView textView = dartsTextViews[i];
            int dartValue;
            String dartStr;
            switch (i) {
                case 0:
                    dartValue = lance.tir_un;
                    dartStr = lance.str_tir_un;
                    break;
                case 1:
                    dartValue = lance.tir_deux;
                    dartStr = lance.str_tir_deux;
                    break;
                case 2:
                default:
                    dartValue = lance.tir_trois;
                    dartStr = lance.str_tir_trois;
                    break;
            }
            updateDartTextView(textView, dartValue, dartStr);
        }
    }

    private void updateDartTextView(TextView textView, int dartValue, String dartStr) {
        if (dartValue != -1) {
            textView.setText(dartStr);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
    }

    static class ViewHolder {
        TextView playerIdTextView;
        TextView[] dartsTextViews;
        ImageView[] valuesImageViews;
    }
}
