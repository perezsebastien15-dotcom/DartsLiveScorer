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

    // ✅ @Ignore : ce champ n'est pas retourné par toutes les requêtes (ex: SELECT id, nom)
    // Room ne lèvera plus le warning CURSOR_MISMATCH pour ces requêtes
    @Ignore
    @ColumnInfo(name = "url_photo")
    public String url_photo;

    // Constructeur principal utilisé par Room (sans url_photo)
    public Joueur(int id, String nom) {
        this.id  = id;
        this.nom = nom;
    }

    // Constructeur complet utilisé lors de l'insertion avec photo
    @Ignore
    public Joueur(int id, String nom, String url_photo) {
        this.id        = id;
        this.nom       = nom;
        this.url_photo = url_photo;
    }

    // Constructeur vide requis par Room
    @Ignore
    public Joueur() {}
}
