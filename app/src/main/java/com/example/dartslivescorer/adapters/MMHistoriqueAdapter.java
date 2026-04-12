package com.example.dartslivescorer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dartslivescorer.R;

import java.util.List;

import models.gamesModels.MMTryItem;

public class MMHistoriqueAdapter extends BaseAdapter {

    private final Context       context;
    private final List<MMTryItem> items;

    public MMHistoriqueAdapter(Context ctx, List<MMTryItem> items) {
        this.context = ctx;
        this.items   = items;
    }

    @Override public int     getCount()               { return items.size(); }
    @Override public Object  getItem(int pos)         { return items.get(pos); }
    @Override public long    getItemId(int pos)       { return pos; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.grid_mm_try_item, parent, false);

        MMTryItem item = items.get(position);

        ((TextView) convertView.findViewById(R.id.try_tour))  .setText(String.valueOf(item.tour));
        ((TextView) convertView.findViewById(R.id.try_joueur)).setText(item.joueur);
        ((TextView) convertView.findViewById(R.id.try_f1))    .setText(item.f1);
        ((TextView) convertView.findViewById(R.id.try_f2))    .setText(item.f2);
        ((TextView) convertView.findViewById(R.id.try_f3))    .setText(item.f3);

        setIndicateur(convertView.findViewById(R.id.try_ind1), item.r1);
        setIndicateur(convertView.findViewById(R.id.try_ind2), item.r2);
        setIndicateur(convertView.findViewById(R.id.try_ind3), item.r3);

        // Ligne en surbrillance si victoire (tous indicateurs verts)
        if (item.r1 == 1 && item.r2 == 1 && item.r3 == 1)
            convertView.setBackgroundColor(0x2200FF88);
        else
            convertView.setBackgroundColor(Color.TRANSPARENT);

        return convertView;
    }

    private void setIndicateur(View v, int result) {
        switch (result) {
            case 1:  v.setBackgroundColor(0xFF00E676); break; // vert — bonne position
            case 2:  v.setBackgroundColor(0xFFFF9800); break; // orange — bon chiffre
            default: v.setBackgroundColor(Color.TRANSPARENT);  break;
        }
    }
}
