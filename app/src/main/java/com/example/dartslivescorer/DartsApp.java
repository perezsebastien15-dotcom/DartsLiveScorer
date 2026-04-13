package com.example.dartslivescorer;

import android.app.Application;
import android.util.Log;

import java.util.concurrent.Executors;

import models.DartScorerDatabase;

public class DartsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Warm-up de la base en background sur un thread dédié,
        // séparé de tout autre executor pour éviter les conflits.
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                DartScorerDatabase.getDatabase(getApplicationContext())
                                  .dartScorerDao()
                                  .getAllJoueurs();
                Log.d("DartsApp", "DB prête.");
            } catch (Exception e) {
                Log.e("DartsApp", "Erreur warm-up DB", e);
            }
        });
    }
}
