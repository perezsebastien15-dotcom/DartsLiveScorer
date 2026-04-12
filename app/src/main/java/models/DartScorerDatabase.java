package models;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.commonModels.Joueur;
import models.commonModels.Lance;
import models.commonModels.Partie;
import models.migrations.Migration_1_2;
import models.migrations.Migration_2_3;
import models.migrations.Migration_3_4;

@Database(entities = {Joueur.class, Partie.class, Lance.class}, version = 4, exportSchema = false)
public abstract class DartScorerDatabase extends RoomDatabase {

    private static volatile DartScorerDatabase INSTANCE;

    /** Executor partagé pour toutes les opérations Room hors thread principal. */
    public static final ExecutorService DB_EXECUTOR = Executors.newFixedThreadPool(4);

    public abstract DartScorerDao dartScorerDao();

    /**
     * Retourne l'instance singleton de la base, en la construisant si nécessaire.
     * L'instance est créée de manière thread-safe.
     * IMPORTANT : ne jamais appeler dartScorerDao() directement sur le thread
     * principal — toujours utiliser DB_EXECUTOR ou un executor dédié.
     */
    public static DartScorerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DartScorerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    DartScorerDatabase.class,
                                    "dart_scorer_database")
                            .addMigrations(
                                    new Migration_1_2(),
                                    new Migration_2_3(),
                                    new Migration_3_4()
                            )
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Initialise la DB en background (ouvre la connexion + applique les migrations)
     * puis appelle le callback sur le thread principal une fois prêt.
     * À appeler depuis onCreate() des activités qui ont besoin de la DB.
     */
    public static void initAsync(Context context, Runnable onReady) {
        DB_EXECUTOR.execute(() -> {
            // Forcer l'ouverture réelle de la DB (et donc les migrations) en background
            DartScorerDatabase db = getDatabase(context);
            db.dartScorerDao().getAllJoueurs(); // warm-up : ouvre la connexion
            new Handler(Looper.getMainLooper()).post(onReady);
        });
    }
}
