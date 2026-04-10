package com.example.dartslivescorer.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Graphique circulaire (donut) dessiné entièrement en Canvas.
 * Affiche le label + pourcentage sur chaque tranche, et une légende en bas.
 */
public class PieChartView extends View {

    public static class Slice {
        public final String label;
        public final float  value;
        public final int    color;
        public Slice(String label, float value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
    }

    private List<Slice> slices;
    private String centerText = "";
    private String titleText  = "";

    // Paints
    private final Paint piePaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pctPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);  // % sur tranche
    private final Paint labelPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);  // label sur tranche
    private final Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint titlePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint boxPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint legPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval        = new RectF();

    public PieChartView(Context ctx) { this(ctx, null); }
    public PieChartView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);

        borderPaint.setColor(0xFF111122);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3f);

        pctPaint.setColor(Color.WHITE);
        pctPaint.setTextSize(30f);
        pctPaint.setTypeface(Typeface.DEFAULT_BOLD);
        pctPaint.setTextAlign(Paint.Align.CENTER);
        pctPaint.setShadowLayer(6f, 1f, 1f, 0xAA000000);

        labelPaint.setColor(0xFFEEEEEE);
        labelPaint.setTextSize(22f);
        labelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setShadowLayer(4f, 1f, 1f, 0xAA000000);

        centerPaint.setColor(Color.WHITE);
        centerPaint.setTextSize(38f);
        centerPaint.setTypeface(Typeface.DEFAULT_BOLD);
        centerPaint.setTextAlign(Paint.Align.CENTER);

        titlePaint.setColor(0xFF00FFEA);
        titlePaint.setTextSize(30f);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setShadowLayer(14f, 0, 0, 0xFF00FFEA);

        legPaint.setColor(Color.WHITE);
        legPaint.setTextSize(24f);
        legPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void setData(List<Slice> slices, String centerText, String title) {
        this.slices     = slices;
        this.centerText = centerText;
        this.titleText  = title;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (slices == null || slices.isEmpty()) return;

        float w = getWidth(), h = getHeight();
        float titleH = 48f;
        float margin = 12f;

        // ── Titre ──────────────────────────────────────────────────────────
        canvas.drawText(titleText, w / 2f, titleH, titlePaint);

        // ── Dimensions du donut ────────────────────────────────────────────
        float legendLineH = 36f;
        float legendLines = (float) Math.ceil(slices.size() / 2.0);
        float legendH     = legendLines * legendLineH + 16f;
        float availH      = h - titleH - margin - legendH;
        float pieSize     = Math.min(w * 0.82f, availH * 0.95f);
        float cx = w / 2f;
        float cy = titleH + margin + pieSize / 2f;
        float r  = pieSize / 2f;

        oval.set(cx - r, cy - r, cx + r, cy + r);

        // ── Calcul total ───────────────────────────────────────────────────
        float total = 0;
        for (Slice s : slices) total += s.value;
        if (total == 0) return;

        // ── Dessin des tranches ────────────────────────────────────────────
        float startAngle = -90f;
        for (Slice s : slices) {
            float sweep = 360f * (s.value / total);
            float pct   = s.value / total * 100f;

            // Tranche colorée
            piePaint.setColor(s.color);
            canvas.drawArc(oval, startAngle, sweep, true, piePaint);

            // Bordure entre tranches
            canvas.drawArc(oval, startAngle, sweep, true, borderPaint);

            // Labels (% + nom) si la tranche est assez grande
            if (sweep > 15f) {
                double midRad = Math.toRadians(startAngle + sweep / 2f);
                float dist = r * (sweep > 50f ? 0.62f : 0.70f);
                float lx = cx + dist * (float) Math.cos(midRad);
                float ly = cy + dist * (float) Math.sin(midRad);

                // Pourcentage en grand
                String pctStr = String.format("%.0f%%", pct);
                canvas.drawText(pctStr, lx, ly, pctPaint);

                // Nom en petit en dessous (si tranche assez large)
                if (sweep > 30f) {
                    canvas.drawText(s.label, lx, ly + 26f, labelPaint);
                }
            }
            startAngle += sweep;
        }

        // ── Cercle intérieur (effet donut) ─────────────────────────────────
        piePaint.setColor(0xFF1A1A2E);
        float holeR = r * 0.30f;
        canvas.drawCircle(cx, cy, holeR, piePaint);

        // Anneau de bordure intérieur
        borderPaint.setStrokeWidth(2f);
        canvas.drawCircle(cx, cy, holeR, borderPaint);

        // ── Texte central ──────────────────────────────────────────────────
        if (!centerText.isEmpty()) {
            // Texte sur 2 lignes si contient \n
            String[] lines = centerText.split("\n");
            float lineH = centerPaint.getTextSize();
            float startY = cy - (lines.length - 1) * lineH / 2f + lineH * 0.35f;
            for (String line : lines) {
                canvas.drawText(line, cx, startY, centerPaint);
                startY += lineH;
            }
        }

        // ── Légende en bas ─────────────────────────────────────────────────
        float boxSize  = 20f;
        float legY     = cy + r + 20f;
        float colW     = w / 2f;
        int   col      = 0;

        for (Slice s : slices) {
            float legX = margin + col * colW;

            boxPaint.setColor(s.color);
            canvas.drawRect(legX, legY, legX + boxSize, legY + boxSize, boxPaint);

            float pct = s.value / total * 100f;
            String legLabel = s.label + "  " + String.format("%.0f%%", pct);
            canvas.drawText(legLabel, legX + boxSize + 8f, legY + boxSize - 3f, legPaint);

            col++;
            if (col >= 2) { col = 0; legY += legendLineH; }
        }
    }
}
