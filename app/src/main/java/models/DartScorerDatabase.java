package models;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import models.commonModels.Joueur;
import models.commonModels.Partie;
import models.commonModels.Lance;
import models.migrations.Migration_1_2;
import models.migrations.Migration_2_3;

// ✅ exportSchema = false supprime le warning "Schema export directory not provided"
@Database(entities = {Joueur.class, Partie.class, Lance.class}, version = 3, exportSchema = false)
public abstract class DartScorerDatabase extends RoomDatabase {

    private static volatile DartScorerDatabase INSTANCE;

    // ✅ Un seul DAO pour toute l'application
    public abstract DartScorerDao dartScorerDao();

    public static DartScorerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DartScorerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    DartScorerDatabase.class,
                                    "dart_scorer_database")
                            .addMigrations(new Migration_1_2(), new Migration_2_3())
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
