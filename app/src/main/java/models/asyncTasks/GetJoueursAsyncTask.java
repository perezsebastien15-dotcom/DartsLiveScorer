package models.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import models.DartScorerDatabase;
import models.commonModels.Joueur;
import models.gamesModels.PlayerItem;

public class GetJoueursAsyncTask extends AsyncTask<Void, Void, List<PlayerItem>> {

    private DartScorerDatabase db;
    private WeakReference<Context> contextRef;
    private OnJoueursLoadedListener listener;

    public GetJoueursAsyncTask(Context context, DartScorerDatabase database) {
        this.contextRef = new WeakReference<>(context);

        // Assurez-vous que la base de données n'est pas null avant de l'assigner à la variable db
        if (database != null) {
            this.db = database;
        } else {
            // Gérer le cas où la base de données est null (vous pouvez lancer une exception, afficher un message, etc.)
            throw new IllegalArgumentException("La base de données ne peut pas être null");
        }

        // Assurez-vous que l'activité appelante implémente l'interface
        if (context instanceof OnJoueursLoadedListener) {
            this.listener = (OnJoueursLoadedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " doit implémenter OnJoueursLoadedListener");
        }
    }

    // Méthode exécutée en arrière-plan
    @Override
    protected List<PlayerItem> doInBackground(Void... voids) {
        if (db == null) {
            // Gérer le cas où la base de données n'est pas initialisée correctement
            return new ArrayList<>(); // Ou renvoyez une liste vide, selon votre logique
        }

        List<Joueur> joueurs = db.dartScorerDao().getAllJoueurs();

        // Transformer les entités Joueur en objets PlayerItem
        List<PlayerItem> playerItems = new ArrayList<>();
        for (Joueur joueur : joueurs) {
            Log.d("GetJoueursAsyncTask", "joueur: " + joueur.id);
            Log.d("GetJoueursAsyncTask", "joueur.nom: " + joueur.nom);
            PlayerItem playerItem = new PlayerItem(joueur.id, joueur.nom, 0);
            playerItems.add(playerItem);
        }

        return playerItems;
    }

    public interface OnJoueursLoadedListener {
        void onJoueursLoaded(List<PlayerItem> joueurs);
    }

    // Méthode exécutée après l'exécution en arrière-plan
    protected void onPostExecute(List<PlayerItem> joueurs) {
        Context context = contextRef.get();
        if (context != null) {
            // Appel à la méthode de l'interface pour informer l'activité appelante
            if (listener != null) {
                listener.onJoueursLoaded(joueurs);
            }
        }
    }
}
