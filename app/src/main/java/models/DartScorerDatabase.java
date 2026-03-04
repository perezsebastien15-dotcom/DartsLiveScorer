package models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import models.commonModels.Joueur;
import models.commonModels.Lance;
import models.commonModels.Partie;

// DartScorerDatabase.java
@Database(entities = {Joueur.class, Partie.class, Lance.class}, version = 3)
public abstract class DartScorerDatabase extends RoomDatabase {
    public abstract DartScorerDao dartScorerDao();

    private static volatile DartScorerDatabase INSTANCE;

    public static DartScorerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DartScorerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DartScorerDatabase.class, "DartScorer_database")
                            .fallbackToDestructiveMigration() // Cette ligne permet la migration destructrice
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
