package models.commonModels;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Partie")
public class Partie {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String type;
    public String gagnant;
}
