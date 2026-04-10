package models;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
 * Base de données Room — version 1 sur un fichier neuf "darts_v5.db".
 * On repart d'une base propre sans migration pour éviter tout écart de schéma.
 */
@Database(entities = {Joueur.class, Partie.class, Lance.class}, version = 1, exportSchema = false)
public abstract class DartScorerDatabase extends RoomDatabase {

    private static final String TAG     = "DartScorerDB";
    private static final String DB_NAME = "darts_v5.db"; // Nouveau nom = nouvelle base propre

    private static volatile DartScorerDatabase INSTANCE;

    public static final ExecutorService DB_EXECUTOR = Executors.newFixedThreadPool(4);

    public abstract DartScorerDao dartScorerDao();

    public static DartScorerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DartScorerDatabase.class) {
                if (INSTANCE == null) {
                    Log.d(TAG, "Création base " + DB_NAME);
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

    public static void initAsync(Context context, Runnable onReady) {
        DB_EXECUTOR.execute(() -> {
            try {
                getDatabase(context).dartScorerDao().getAllJoueurs();
            } catch (Exception e) {
                Log.e(TAG, "Erreur init", e);
            }
            new Handler(Looper.getMainLooper()).post(onReady);
        });
    }
}
