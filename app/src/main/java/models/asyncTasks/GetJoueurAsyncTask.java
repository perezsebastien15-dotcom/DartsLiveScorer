package models.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.dartslivescorer.commonActivities.ModifyPlayerActivity;

import java.lang.ref.WeakReference;

import models.DartScorerDatabase;
import models.commonModels.Joueur;
import models.gamesModels.PlayerItem;

public class GetJoueurAsyncTask extends AsyncTask<Long, Void, PlayerItem> {

    private DartScorerDatabase db;
    private WeakReference<ModifyPlayerActivity> modifyPlayerActivity;
    private OnJoueurLoadedListener listener;

    public interface OnJoueurLoadedListener {
        void onJoueurLoaded(PlayerItem joueur);
    }

    public GetJoueurAsyncTask(ModifyPlayerActivity modifyPlayerActivity, DartScorerDatabase database, OnJoueurLoadedListener listener) {
        this.modifyPlayerActivity = new WeakReference<>(modifyPlayerActivity);
        this.db = database;
        this.listener = listener;
    }

    @Override
    protected PlayerItem doInBackground(Long... playerIds) {
        if (playerIds.length > 0 && playerIds[0] != null) {
            long playerId = playerIds[0];
            // Charger le joueur depuis la base de données en utilisant l'ID
            Joueur joueur = db.dartScorerDao().getJoueurById(playerId);

            // Vérifier si le joueur est null avant de créer PlayerItem
            if (joueur != null) {

                // Transformer l'entité Joueur en objet PlayerItem
                PlayerItem playerItem = new PlayerItem(joueur.id, joueur.nom, 0);
                return playerItem;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(PlayerItem joueur) {
        ModifyPlayerActivity activity = modifyPlayerActivity.get();
        if (activity != null) {
            if (joueur != null) {
                // Notifier l'activité du joueur chargé
                listener.onJoueurLoaded(joueur);
            } else {
                Log.e("GetJoueurAsyncTask", "Aucun joueur chargé");
            }
        } else {
            Log.e("GetJoueurAsyncTask", "Activité de modification de joueur n'est plus disponible");
        }
    }
}
