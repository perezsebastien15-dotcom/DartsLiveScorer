package models.asyncTasks;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.dartslivescorer.commonActivities.ModifyPlayerActivity;

import models.DartScorerDatabase;
import models.commonModels.Joueur;
import models.gamesModels.PlayerItem;

public class GetJoueurAsyncTask {

    private final DartScorerDatabase db;
    private final WeakReference<ModifyPlayerActivity> activityRef;
    private final OnJoueurLoadedListener listener;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface OnJoueurLoadedListener {
        void onJoueurLoaded(PlayerItem joueur);
    }

    public GetJoueurAsyncTask(ModifyPlayerActivity activity, DartScorerDatabase database, OnJoueurLoadedListener listener) {
        this.activityRef = new WeakReference<>(activity);
        this.db = database;
        this.listener = listener;
    }

    public void execute(Long playerId) {
        executor.execute(() -> {
            PlayerItem result = null;
            if (playerId != null) {
                Joueur joueur = db.dartScorerDao().getJoueurById(playerId);
                if (joueur != null)
                    result = new PlayerItem(joueur.id, joueur.nom, 0);
            }
            final PlayerItem finalResult = result;
            mainHandler.post(() -> {
                ModifyPlayerActivity activity = activityRef.get();
                if (activity != null) {
                    if (finalResult != null)
                        listener.onJoueurLoaded(finalResult);
                    else
                        Log.e("GetJoueurAsyncTask", "Aucun joueur chargé");
                } else {
                    Log.e("GetJoueurAsyncTask", "Activité n'est plus disponible");
                }
            });
        });
    }
}
