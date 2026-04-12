package models.gamesModels;

/** Représente une tentative (un tour d'un joueur) dans le MasterMind. */
public class MMTryItem {
    public final int    tour;
    public final String joueur;
    public final String f1, f2, f3;
    /** 0=rien, 1=bonne pos (vert), 2=bon chiffre mauvaise pos (orange) */
    public final int    r1, r2, r3;

    public MMTryItem(int tour, String joueur,
                     String f1, String f2, String f3,
                     int r1, int r2, int r3) {
        this.tour   = tour;
        this.joueur = joueur;
        this.f1 = f1; this.f2 = f2; this.f3 = f3;
        this.r1 = r1; this.r2 = r2; this.r3 = r3;
    }
}
