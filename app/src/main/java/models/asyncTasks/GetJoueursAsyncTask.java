package models.asyncTasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.DartScorerDatabase;
import models.commonModels.Joueur;
import models.gamesModels.PlayerItem;

public class GetJoueursAsyncTask {

    private final DartScorerDatabase db;
    private final WeakReference<Context> contextRef;
    private final OnJoueursLoadedListener listener;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface OnJoueursLoadedListener {
        void onJoueursLoaded(List<PlayerItem> joueurs);
    }

    public GetJoueursAsyncTask(Context context, DartScorerDatabase database) {
        this.contextRef = new WeakReference<>(context);
        if (database == null)
            throw new IllegalArgumentException("La base de données ne peut pas être null");
        this.db = database;
        if (context instanceof OnJoueursLoadedListener)
            this.listener = (OnJoueursLoadedListener) context;
        else
            throw new ClassCastException(context + " doit implémenter OnJoueursLoadedListener");
    }

    public void execute() {
        executor.execute(() -> {
            List<PlayerItem> playerItems = new ArrayList<>();
            for (Joueur joueur : db.dartScorerDao().getAllJoueurs()) {
                Log.d("GetJoueursAsyncTask", "id=" + joueur.id + " nom=" + joueur.nom);
                playerItems.add(new PlayerItem(joueur.id, joueur.nom, 0));
            }
            mainHandler.post(() -> {
                Context ctx = contextRef.get();
                if (ctx != null && listener != null)
                    listener.onJoueursLoaded(playerItems);
            });
        });
    }
}
