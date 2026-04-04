package models.migrations;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Migration 2 → 3 : ajout de la table Lance
 */
public class Migration_2_3 extends Migration {

    public Migration_2_3() {
        super(2, 3);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Lance` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "`id_partie` INTEGER NOT NULL, " +
            "`id_joueur` INTEGER NOT NULL, " +
            "`tir_un` INTEGER NOT NULL, " +
            "`tir_deux` INTEGER NOT NULL, " +
            "`tir_trois` INTEGER NOT NULL" +
            ")"
        );
    }
}
