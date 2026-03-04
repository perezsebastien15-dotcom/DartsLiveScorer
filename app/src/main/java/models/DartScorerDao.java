package models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import models.commonModels.Joueur;
import models.commonModels.Lance;
import models.commonModels.Partie;

@Dao
public interface DartScorerDao {
    @Insert
    long insertJoueur(Joueur joueur);
    @Update
    void updateJoueur(Joueur joueur);
    @Delete
    void deleteJoueur(Joueur joueur);
    @Query("SELECT id,nom FROM Joueur")
    List<Joueur> getAllJoueurs();

    @Insert
    long insertPartie(Partie partie);

    @Insert
    long insertLance(Lance lance);
    @Query("DELETE FROM Joueur where id = :id")
    void deleteJoueurById(Long id);
    @Query("select id,nom FROM Joueur where id = :id")
    Joueur getJoueurById(Long id);
}
