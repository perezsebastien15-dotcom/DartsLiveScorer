package models.commonModels;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Joueur")
public class Joueur {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String nom;
    public String url_photo;
}
