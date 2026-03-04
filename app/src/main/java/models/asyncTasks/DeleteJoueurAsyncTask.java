package models.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import models.DartScorerDatabase;

public class DeleteJoueurAsyncTask extends AsyncTask<Void, Void, Void> {

    private DartScorerDatabase db;
    private Context context;

    private Long idJoueur;

    // Constructeur prenant le contexte pour afficher un message Toast
    public DeleteJoueurAsyncTask(Context context, DartScorerDatabase database, Long idJoueur) {
        this.context = context;
        this.db = database;
        this.idJoueur = idJoueur;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        db.dartScorerDao().deleteJoueurById(idJoueur);
        return null;
    }
}
