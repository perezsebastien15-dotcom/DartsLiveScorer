package models.commonModels;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Lance")
public class Lance {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "id_partie")
    public long idPartie;

    @ColumnInfo(name = "id_joueur")
    public long idJoueur;

    public int tir_un;
    public int tir_deux;
    public int tir_trois;
}
