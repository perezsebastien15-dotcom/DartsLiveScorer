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

    // ── Joueurs ───────────────────────────────────────────────────────────────
    @Insert  long insertJoueur(Joueur joueur);
    @Update  void updateJoueur(Joueur joueur);
    @Delete  void deleteJoueur(Joueur joueur);
    @Query("SELECT id, nom FROM Joueur") List<Joueur> getAllJoueurs();
    @Query("SELECT id, nom FROM Joueur WHERE id = :id") Joueur getJoueurById(Long id);
    @Query("DELETE FROM Joueur WHERE id = :id") void deleteJoueurById(Long id);

    // ── Parties ───────────────────────────────────────────────────────────────
    @Insert  long insertPartie(Partie partie);
    @Query("UPDATE Partie SET gagnant = :gagnant WHERE id = :idPartie")
    void updateGagnantPartie(long idPartie, String gagnant);
    @Query("SELECT COUNT(*) FROM Partie WHERE gagnant = :nomJoueur AND type = :typeJeu")
    int getNbVictoiresJoueur(String nomJoueur, String typeJeu);

    // ── Lancers : base ────────────────────────────────────────────────────────
    @Insert  long insertLance(Lance lance);

    /** Liste des types de jeux distincts joués par un joueur. */
    @Query("SELECT DISTINCT type_jeu FROM Lance WHERE id_joueur = :idJoueur ORDER BY type_jeu")
    List<String> getTypesJeuxJoues(long idJoueur);

    @Query("SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu")
    int getNbLancersJoueur(long idJoueur, String typeJeu);

    @Query("SELECT COALESCE(SUM(tir_un + tir_deux + tir_trois), 0) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu")
    int getSommeTotaleJoueur(long idJoueur, String typeJeu);

    @Query("SELECT COALESCE(MAX(tir_un + tir_deux + tir_trois), 0) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu")
    int getMeilleurLancerJoueur(long idJoueur, String typeJeu);

    @Query("SELECT COALESCE(MIN(tir_un + tir_deux + tir_trois), 0) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu")
    int getMauvaisLancerJoueur(long idJoueur, String typeJeu);

    @Query("SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu AND (tir_un + tir_deux + tir_trois) = 180")
    int getNb180Joueur(long idJoueur, String typeJeu);

    @Query("SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu AND (tir_un + tir_deux + tir_trois) >= 140")
    int getNb140PlusJoueur(long idJoueur, String typeJeu);

    @Query("SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu AND (tir_un + tir_deux + tir_trois) >= 100")
    int getNb100PlusJoueur(long idJoueur, String typeJeu);

    @Query("SELECT COUNT(DISTINCT id_partie) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu")
    int getNbPartiesJouees(long idJoueur, String typeJeu);

    @Query("SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu AND (tir_un + tir_deux + tir_trois) BETWEEN :min AND :max")
    int getNbLancersAvecScoreEntre(long idJoueur, String typeJeu, int min, int max);

    @Query("SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu AND tir_un > 0 AND tir_deux > 0 AND tir_trois > 0")
    int getNbToursPArfaits(long idJoueur, String typeJeu);

    // Fléchettes individuelles
    @Query("SELECT (SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu AND tir_un   > 0) + " +
           "(SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu AND tir_deux  > 0) + " +
           "(SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu AND tir_trois > 0)")
    int getNbFlechettesTouchees(long idJoueur, String typeJeu);

    @Query("SELECT (SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu AND tir_un   = 0) + " +
           "(SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu AND tir_deux  = 0) + " +
           "(SELECT COUNT(*) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu AND tir_trois = 0)")
    int getNbFlechettesManquees(long idJoueur, String typeJeu);

    @Query("SELECT COALESCE(SUM(tir_un),    0) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu")
    int getSommeFlechette1(long idJoueur, String typeJeu);

    @Query("SELECT COALESCE(SUM(tir_deux),  0) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu")
    int getSommeFlechette2(long idJoueur, String typeJeu);

    @Query("SELECT COALESCE(SUM(tir_trois), 0) FROM Lance WHERE id_joueur = :idJoueur AND type_jeu = :typeJeu")
    int getSommeFlechette3(long idJoueur, String typeJeu);
}
