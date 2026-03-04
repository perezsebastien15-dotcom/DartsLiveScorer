package models.asyncTasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.DartScorerDatabase;

public class DeleteJoueurAsyncTask {

    private final DartScorerDatabase db;
    private final Long idJoueur;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DeleteJoueurAsyncTask(Context context, DartScorerDatabase database, Long idJoueur) {
        this.db = database;
        this.idJoueur = idJoueur;
    }

    public void execute() {
        executor.execute(() -> db.dartScorerDao().deleteJoueurById(idJoueur));
    }
}
