package models.migrations;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Migration 4 → 5 : recrée la table Lance avec le schéma exact attendu par Room.
 * Corrige l'écart de schéma entre les migrations précédentes et l'entité Java.
 */
public class Migration_4_5 extends Migration {

    public Migration_4_5() { super(4, 5); }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        // Sauvegarde des données existantes
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Lance_backup` AS SELECT * FROM `Lance`"
        );

        // Suppression de l'ancienne table
        database.execSQL("DROP TABLE IF EXISTS `Lance`");

        // Recréation avec le schéma exact que Room attend
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Lance` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "`id_partie` INTEGER NOT NULL, " +
            "`id_joueur` INTEGER NOT NULL, " +
            "`type_jeu` TEXT NOT NULL DEFAULT 'Inconnu', " +
            "`tir_un` INTEGER NOT NULL, " +
            "`tir_deux` INTEGER NOT NULL, " +
            "`tir_trois` INTEGER NOT NULL" +
            ")"
        );

        // Restauration des données (avec type_jeu par défaut pour les anciens enregistrements)
        database.execSQL(
            "INSERT INTO `Lance` (id, id_partie, id_joueur, type_jeu, tir_un, tir_deux, tir_trois) " +
            "SELECT id, id_partie, id_joueur, " +
            "COALESCE(type_jeu, 'Inconnu'), " +
            "tir_un, tir_deux, tir_trois FROM `Lance_backup`"
        );

        database.execSQL("DROP TABLE IF EXISTS `Lance_backup`");
    }
}
