package models.asyncTasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.DartScorerDatabase;
import models.commonModels.Joueur;

public class InsertJoueurAsyncTask {

    private final DartScorerDatabase db;
    private final Context context;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public InsertJoueurAsyncTask(Context context, DartScorerDatabase database) {
        this.context = context;
        this.db = database;
    }

    public void execute(Joueur joueur) {
        executor.execute(() -> {
            long joueurId = db.dartScorerDao().insertJoueur(joueur);
            mainHandler.post(() -> {
                if (joueurId > 0)
                    Toast.makeText(context, "Joueur inséré avec succès", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Échec de l'insertion du joueur", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
