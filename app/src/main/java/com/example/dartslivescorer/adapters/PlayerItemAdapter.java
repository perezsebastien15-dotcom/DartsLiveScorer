package com.example.dartslivescorer.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dartslivescorer.R;

import java.util.ArrayList;
import java.util.List;

import models.gamesModels.PlayerItem;

public class PlayerItemAdapter extends BaseAdapter {

    private Context context;
    private List<PlayerItem> playerList;
    private List<PlayerItem> selectedPlayers;
    private LayoutInflater inflater;
    private OnPlayerItemLongClickListener playerItemLongClickListener;
    private OnPlayerItemClickListener playerItemClickListener;

    public PlayerItemAdapter(Context context, List<PlayerItem> playerList, OnPlayerItemLongClickListener listenerLongClick, OnPlayerItemClickListener listenerClick) {
        this.context = context;
        this.playerList = playerList;
        this.selectedPlayers = new ArrayList<>();  // Initialisation de la liste des joueurs sélectionnés
        this.inflater = LayoutInflater.from(context);
        this.playerItemLongClickListener = listenerLongClick;
        this.playerItemClickListener = listenerClick;
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_player_item, parent, false);
            holder = new ViewHolder();
            holder.playerNameTextView = convertView.findViewById(R.id.playerIdTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PlayerItem joueur = playerList.get(position);

        // Afficher le nom du joueur
        holder.playerNameTextView.setText(joueur.getName());

        // Gestion du clic long seulement si le listener est défini
        if (playerItemLongClickListener != null) {
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Que voulez-vous faire ?");
                    builder.setNeutralButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Annuler la suppression, si nécessaire
                        }
                    });
                    builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Supprimer le joueur ici
                            if (playerItemLongClickListener != null) {
                                playerItemLongClickListener.onPlayerItemLongClick(playerList.get(position), "Suppr");
                                Toast.makeText(context, "Joueur supprimé avec succès", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Modifier", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (playerItemLongClickListener != null) {
                                playerItemLongClickListener.onPlayerItemLongClick(playerList.get(position), "Modif");
                            }
                        }
                    });

                    builder.show();
                    return true; // Indique que l'événement a été consommé (long clic traité)
                }
            });
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerItemClickListener != null) {
                    playerItemClickListener.onPlayerItemClick(playerList.get(position));
                }
            }
        });

        // Mettez en surbrillance le joueur sélectionné
        if (selectedPlayers.contains(joueur)) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.selectedPlayerColor));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        return convertView;
    }

    public void updatePlayerList(List<PlayerItem> newPlayerList) {
        playerList.clear();
        if (newPlayerList != null) {
            playerList.addAll(newPlayerList);
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView playerNameTextView;
    }

    public interface OnPlayerItemLongClickListener {
        void onPlayerItemLongClick(PlayerItem playerItem, String type);
    }

    public interface OnPlayerItemClickListener {
        void onPlayerItemClick(PlayerItem playerItem);
    }

    public void updateSelectedPlayers(List<PlayerItem> selectedPlayers) {
        this.selectedPlayers = selectedPlayers;
        notifyDataSetChanged();
    }
}
