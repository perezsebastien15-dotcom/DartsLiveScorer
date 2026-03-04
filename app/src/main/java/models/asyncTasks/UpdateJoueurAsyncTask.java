package models.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import models.DartScorerDatabase;
import models.commonModels.Joueur;

public class UpdateJoueurAsyncTask extends AsyncTask<Joueur, Void, Void> {

    private DartScorerDatabase db;
    private Context context;

    public UpdateJoueurAsyncTask(Context context, DartScorerDatabase database) {
        this.context = context;
        this.db = database;
    }

    @Override
    protected Void doInBackground(Joueur... joueur) {
        // Mettre à jour le nom du joueur dans la base de données
        db.dartScorerDao().updateJoueur(joueur[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // Vous pouvez ajouter des actions post-mise à jour ici si nécessaire
        Toast.makeText(context, "Joueur mis à jour avec succès", Toast.LENGTH_SHORT).show();
    }
}