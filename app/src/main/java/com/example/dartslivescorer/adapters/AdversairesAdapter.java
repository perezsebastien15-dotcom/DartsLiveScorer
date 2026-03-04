package com.example.dartslivescorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dartslivescorer.R;

import java.util.List;

import models.commonModels.AdversaireItem;

public class AdversairesAdapter extends BaseAdapter {
    private Context context;
    private List<AdversaireItem> advList;

    public AdversairesAdapter(Context context, List<AdversaireItem> advList) {
        this.context = context;
        this.advList = advList;
    }

    @Override
    public int getCount() {
        return advList.size();
    }

    @Override
    public Object getItem(int position) {
        return advList.get(position);
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
            convertView = inflater.inflate(R.layout.grid_adv_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.gridItemTextView = convertView.findViewById(R.id.playerIdTextView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AdversaireItem item = advList.get(position);
        viewHolder.gridItemTextView.setText(item.getInfos());

        return convertView;
    }

    // ViewHolder pattern class to hold the views for efficient recycling
    private static class ViewHolder {
        TextView gridItemTextView;
    }

}
