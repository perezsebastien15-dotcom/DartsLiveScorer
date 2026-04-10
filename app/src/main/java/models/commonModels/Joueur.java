package models.commonModels;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "joueur")
public class Joueur {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nom")
    public String nom;

    // url_photo non présente en base (pas de migration) — ignorée par Room
    @Ignore
    public String url_photo;

    /**
     * Constructeur sans argument : constructeur principal utilisé par Room
     * pour l'insertion et la reconstruction depuis le curseur.
     */
    public Joueur() {}

    @Ignore
    public Joueur(int id, String nom) {
        this.id  = id;
        this.nom = nom;
    }

    @Ignore
    public Joueur(int id, String nom, String url_photo) {
        this.id        = id;
        this.nom       = nom;
        this.url_photo = url_photo;
    }
}
