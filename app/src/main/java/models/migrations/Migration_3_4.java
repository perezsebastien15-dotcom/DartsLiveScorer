package models.migrations;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Migration 3 → 4 : ajout de la colonne type_jeu dans Lance.
 * Permet de filtrer les statistiques par type de jeu.
 * Les anciens lancers existants reçoivent la valeur "Inconnu".
 */
public class Migration_3_4 extends Migration {

    public Migration_3_4() { super(3, 4); }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL(
            "ALTER TABLE Lance ADD COLUMN type_jeu TEXT NOT NULL DEFAULT 'Inconnu'"
        );
    }
}
