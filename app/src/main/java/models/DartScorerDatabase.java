package models;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.commonModels.Joueur;
import models.commonModels.Lance;
import models.commonModels.Partie;

/**
 * Version 1 sur un fichier neuf "darts_live.db".
 * Pas de migrations : Room crée le schéma correct directement.
 * L'ancienne base "dart_scorer_database" est abandonnée sur l'appareil.
 */
@Database(entities = {Joueur.class, Partie.class, Lance.class}, version = 1, exportSchema = false)
public abstract class DartScorerDatabase extends RoomDatabase {

    private static final String TAG     = "DartScorerDB";
    private static final String DB_NAME = "darts_live.db";

    private static volatile DartScorerDatabase INSTANCE;

    public static final ExecutorService DB_EXECUTOR = Executors.newFixedThreadPool(4);

    public abstract DartScorerDao dartScorerDao();

    public static DartScorerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DartScorerDatabase.class) {
                if (INSTANCE == null) {
                    Log.d(TAG, "Création " + DB_NAME);
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    DartScorerDatabase.class,
                                    DB_NAME)
                            .build();
                    Log.d(TAG, "Base prête.");
                }
            }
        }
        return INSTANCE;
    }
}
