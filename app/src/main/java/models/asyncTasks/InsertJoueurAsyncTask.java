package models.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import models.DartScorerDatabase;
import models.commonModels.Joueur;

public class InsertJoueurAsyncTask extends AsyncTask<Joueur, Void, Long> {

    private DartScorerDatabase db;
    private Context context;

    // Constructeur prenant le contexte pour afficher un message Toast
    public InsertJoueurAsyncTask(Context context, DartScorerDatabase database) {
        this.context = context;
        this.db = database;
    }

    // Méthode exécutée en arrière-plan
    @Override
    protected Long doInBackground(Joueur... joueurs) {
        // Insérer le joueur dans la base de données et renvoyer l'ID inséré
        return db.dartScorerDao().insertJoueur(joueurs[0]);
    }

    // Méthode exécutée après l'exécution en arrière-plan
    @Override
    protected void onPostExecute(Long joueurId) {
        if (joueurId > 0) {
            // L'insertion a réussi, afficher un message Toast par exemple
            Toast.makeText(context, "Joueur inséré avec succès", Toast.LENGTH_SHORT).show();
        } else {
            // Gérer le cas où l'insertion a échoué
            Toast.makeText(context, "Échec de l'insertion du joueur", Toast.LENGTH_SHORT).show();
        }
    }
}
