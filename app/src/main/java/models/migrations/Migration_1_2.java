package models.migrations;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Migration 1 → 2 : ajout de la table Partie
 */
public class Migration_1_2 extends Migration {

    public Migration_1_2() {
        super(1, 2);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Partie` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "`type` TEXT, " +
            "`gagnant` TEXT" +
            ")"
        );
    }
}
