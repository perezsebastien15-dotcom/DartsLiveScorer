package models.asyncTasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.DartScorerDatabase;
import models.commonModels.Joueur;

public class UpdateJoueurAsyncTask {

    private final DartScorerDatabase db;
    private final Context context;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UpdateJoueurAsyncTask(Context context, DartScorerDatabase database) {
        this.context = context;
        this.db = database;
    }

    public void execute(Joueur joueur) {
        executor.execute(() -> {
            db.dartScorerDao().updateJoueur(joueur);
            mainHandler.post(() ->
                Toast.makeText(context, "Joueur mis à jour avec succès", Toast.LENGTH_SHORT).show()
            );
        });
    }
}
