package com.example.dartslivescorer.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.dartslivescorer.enums.eGames;

import java.util.ArrayList;
import java.util.List;

public class TargetView extends View {

    private Paint greyPaint;
    private Paint darkGreyPaint;
    private Paint lowGreyPaint;
    private Paint lowDarkGreyPaint;
    private Paint greenPaint;
    private Paint redPaint;
    private Paint beigePaint;
    private Paint blackPaint;
    private Path[] greenPaths;
    private Path[] redPaths;
    private Path[] beigePaths;
    private Path[] blackPaths;
    private Paint borderPaint;
    private Paint centerPointPaint;
    private Paint outerCirclePaint;
    private Paint blackCirclePaint;
    private Paint textPaint;
    private Paint yellowPaint;
    private Paint yellowBorderPaint;
    private Paint innerCircle;
    private Paint outerCircleBorder;

    private int numSlices = 20;
    private List<Integer> touched = new ArrayList<>();
    private List<Integer> closed = new ArrayList<>();
    private List<Integer> cricket = new ArrayList<>();
    private List<Integer> closedSolo = new ArrayList<>();
    private eGames game;

    private int width;
    private int height;
    private int radius;
    private int centerX;
    private int centerY;
    private int innerRadius;
    private float sliceAngle;
    private float textRadius;
    private int[] orderedNumbers = {10, 15, 2, 17, 3, 19, 7, 16, 8, 11, 14, 9, 12, 5,20, 1, 18, 4, 13, 6};


    public TargetView(Context context) {
        super(context);
        init();
    }

    public TargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TargetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);

        blackCirclePaint = new Paint();
        blackCirclePaint.setColor(Color.BLACK);
        blackCirclePaint.setStyle(Paint.Style.STROKE);
        blackCirclePaint.setStrokeWidth(95);

        yellowPaint = new Paint();
        yellowPaint.setColor(Color.argb(80, 255, 248, 0));
        yellowPaint.setStyle(Paint.Style.FILL);

        yellowBorderPaint = new Paint();
        yellowBorderPaint.setColor(Color.argb(255, 255, 248, 0));
        yellowBorderPaint.setStyle(Paint.Style.STROKE);
        yellowBorderPaint.setStrokeWidth(2);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);

        greenPaint = new Paint();
        greenPaint.setColor(Color.rgb(0, 168, 21));
        greenPaint.setStyle(Paint.Style.FILL);

        redPaint = new Paint();
        redPaint.setColor(Color.rgb(168, 0, 0));
        redPaint.setStyle(Paint.Style.FILL);

        centerPointPaint = new Paint();
        centerPointPaint.setColor(Color.rgb(168, 0, 0));
        centerPointPaint.setStyle(Paint.Style.FILL);

        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(Color.rgb(0, 168, 21));
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setStrokeWidth(15); // Adjust the width as needed

        beigePaint = new Paint();
        beigePaint.setColor(Color.rgb(245, 245, 220)); // Beige color
        beigePaint.setStyle(Paint.Style.FILL);

        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.FILL);

        greyPaint = new Paint();
        greyPaint.setColor(Color.GRAY);
        greyPaint.setStyle(Paint.Style.FILL);

        darkGreyPaint = new Paint();
        darkGreyPaint.setColor(Color.DKGRAY);
        darkGreyPaint.setStyle(Paint.Style.FILL);

        lowGreyPaint = new Paint();
        lowGreyPaint.setColor(Color.argb(255, 161, 178, 175));
        lowGreyPaint.setStyle(Paint.Style.FILL);

        lowDarkGreyPaint = new Paint();
        lowDarkGreyPaint.setColor(Color.argb(255, 127, 173, 187));
        lowDarkGreyPaint.setStyle(Paint.Style.FILL);

        innerCircle = new Paint();
        innerCircle.setColor(Color.argb(255, 161, 178, 175));
        innerCircle.setStyle(Paint.Style.FILL);

        outerCircleBorder = new Paint();
        outerCircleBorder.setColor(Color.argb(255, 127, 173, 187));
        outerCircleBorder.setStyle(Paint.Style.STROKE);
        outerCircleBorder.setStrokeWidth(15);

        greenPaths = new Path[numSlices];
        redPaths = new Path[numSlices];
        beigePaths = new Path[numSlices];
        blackPaths = new Path[numSlices];

        for (int i = 0; i < numSlices; i++) {
            greenPaths[i] = new Path();
            redPaths[i] = new Path();
            beigePaths[i] = new Path();
            blackPaths[i] = new Path();
        }
    }

    private void InitVars(boolean externe)
    {
        if(externe) {
            width = getWidth();
            height = getHeight();
            radius = Math.min(width, height) / 2 - 85;

            centerX = width / 2;
            centerY = height / 2;
            innerRadius = Math.min(width, height) / 2 - 45;
            sliceAngle = 360f / numSlices;

            textRadius = innerRadius;
        }
        else
        {
            width = getWidth() / 2;
            height = getHeight() / 2;
            radius = Math.min(width, height) / 2;
        }
    }
    private void Slices(boolean externe, Canvas canvas)
    {
        for (int i = 0; i < numSlices; i++) {
            int number = orderedNumbers[i];
            greenPaths[i].reset();
            redPaths[i].reset();
            beigePaths[i].reset();
            blackPaths[i].reset();

            greenPaths[i].moveTo(centerX, centerY);
            greenPaths[i].arcTo(new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius), i * sliceAngle, sliceAngle);
            greenPaths[i].lineTo(centerX, centerY);

            greenPaths[i].close();

            redPaths[i].set(greenPaths[i]);

            beigePaths[i].moveTo(centerX, centerY);
            beigePaths[i].lineTo(centerX + (float) ((radius - 20) * Math.cos(Math.toRadians(i * sliceAngle))),
                    centerY + (float) ((radius - 20) * Math.sin(Math.toRadians(i * sliceAngle))));
            beigePaths[i].lineTo(centerX + (float) ((radius - 20) * Math.cos(Math.toRadians((i + 1) * sliceAngle))),
                    centerY + (float) ((radius - 20) * Math.sin(Math.toRadians((i + 1) * sliceAngle))));

            beigePaths[i].close();

            blackPaths[i].moveTo(centerX, centerY);
            blackPaths[i].lineTo(centerX + (float) ((radius - 20) * Math.cos(Math.toRadians(i * sliceAngle))),
                    centerY + (float) ((radius - 20) * Math.sin(Math.toRadians(i * sliceAngle))));
            blackPaths[i].lineTo(centerX + (float) ((radius - 20) * Math.cos(Math.toRadians((i + 1) * sliceAngle))),
                    centerY + (float) ((radius - 20) * Math.sin(Math.toRadians((i + 1) * sliceAngle))));
            blackPaths[i].close();

            if(externe)
            {
                canvas.save();
                canvas.rotate(-9, centerX, centerY);

                String text = String.valueOf(number);
                float textWidth = textPaint.measureText(text);

                // Calculer les coordonnées de dessin du texte en tenant compte de la rotation
                float textAngle = (i * sliceAngle + sliceAngle / 2 + 9) % 360; // Ajouter 9 degrés pour compenser la rotation
                float textX = (float) (centerX + textRadius * Math.cos(Math.toRadians(textAngle)));
                float textY = (float) (centerY + textRadius * Math.sin(Math.toRadians(textAngle)));

                // Centrer le texte horizontalement et verticalement
                textX -= textWidth / 2;
                Paint.FontMetrics fm = textPaint.getFontMetrics();
                float textHeight = fm.descent - fm.ascent;
                textY += textHeight / 2 - fm.descent;

                canvas.drawText(text, textX, textY, textPaint);
                canvas.restore();
            }
        }
    }
    private void ColoriseSlices(boolean externe, Canvas canvas) {
        if (this.game.equals(eGames.ShootOut)) {
            /*Dessin des zones externes*/
            for (int i = 0; i < numSlices; i++) {
                if (this.touched.contains(orderedNumbers[i])) {
                    canvas.drawPath(greenPaths[i], darkGreyPaint);
                    canvas.drawPath(beigePaths[i], greyPaint);
                    canvas.drawPath(greenPaths[i], darkGreyPaint);
                    canvas.drawPath(beigePaths[i], greyPaint);
                    canvas.drawPath(greenPaths[i], borderPaint);
                    canvas.drawPath(beigePaths[i], borderPaint);
                } else {
                    if (i % 2 == 0) {
                        canvas.drawPath(greenPaths[i], greenPaint);
                        canvas.drawPath(beigePaths[i], beigePaint);
                        canvas.drawPath(greenPaths[i], borderPaint);
                        canvas.drawPath(beigePaths[i], borderPaint);
                    } else {
                        canvas.drawPath(redPaths[i], redPaint);
                        canvas.drawPath(blackPaths[i], blackPaint);
                        canvas.drawPath(redPaths[i], borderPaint);
                        canvas.drawPath(blackPaths[i], borderPaint);
                    }
                }
            }
            if (!externe) {
                /*Dessin du Bull*/
                if (this.touched.contains(21)) {
                    // Draw the center point
                    canvas.drawCircle(centerX, centerY, 20, greyPaint); // Adjust the radius as needed
                    // Draw the outer circle
                    canvas.drawCircle(centerX, centerY, 20, darkGreyPaint);
                } else {
                    // Draw the center point
                    canvas.drawCircle(centerX, centerY, 20, centerPointPaint); // Adjust the radius as needed
                    // Draw the outer circle
                    canvas.drawCircle(centerX, centerY, 20, outerCirclePaint); // Adjust the radius and stroke width as needed
                }
            }
        }
        if (this.game.equals(eGames.OriginalCricket) || this.game.equals(eGames.RandomCricket)) {
            for (int i = 0; i < numSlices; i++) {
                /*Si le slice n'est pas un item cricket*/
                if (!this.cricket.contains(orderedNumbers[i])) {
                    if (i % 2 == 0) {
                        canvas.drawPath(greenPaths[i], darkGreyPaint);
                        canvas.drawPath(beigePaths[i], greyPaint);
                        canvas.drawPath(greenPaths[i], darkGreyPaint);
                        canvas.drawPath(beigePaths[i], greyPaint);
                        canvas.drawPath(greenPaths[i], borderPaint);
                        canvas.drawPath(beigePaths[i], borderPaint);
                    } else {
                        canvas.drawPath(redPaths[i], darkGreyPaint);
                        canvas.drawPath(blackPaths[i], greyPaint);
                        canvas.drawPath(redPaths[i], darkGreyPaint);
                        canvas.drawPath(blackPaths[i], greyPaint);
                        canvas.drawPath(redPaths[i], borderPaint);
                        canvas.drawPath(beigePaths[i], borderPaint);
                    }
                }
                /*Si le slice est un item cricket*/
                else {
                    /*Si cet item cricket est fermé*/
                    if (this.closed.contains(orderedNumbers[i])) {
                        if (i % 2 == 0) {
                            canvas.drawPath(greenPaths[i], greenPaint);
                            canvas.drawPath(beigePaths[i], beigePaint);
                            canvas.drawPath(greenPaths[i], lowDarkGreyPaint);
                            canvas.drawPath(beigePaths[i], lowGreyPaint);
                            canvas.drawPath(greenPaths[i], borderPaint);
                            canvas.drawPath(beigePaths[i], borderPaint);
                        } else {
                            canvas.drawPath(redPaths[i], redPaint);
                            canvas.drawPath(blackPaths[i], blackPaint);
                            canvas.drawPath(redPaths[i], lowDarkGreyPaint);
                            canvas.drawPath(blackPaths[i], lowGreyPaint);
                            canvas.drawPath(redPaths[i], borderPaint);
                            canvas.drawPath(blackPaths[i], borderPaint);
                        }
                    }
                    //Si cet item cricket est actif
                    else if (!this.closedSolo.contains(orderedNumbers[i])) {
                        if (i % 2 == 0) {
                            canvas.drawPath(greenPaths[i], greenPaint);
                            canvas.drawPath(beigePaths[i], beigePaint);
                            canvas.drawPath(greenPaths[i], borderPaint);
                            canvas.drawPath(beigePaths[i], borderPaint);
                        } else {
                            canvas.drawPath(redPaths[i], redPaint);
                            canvas.drawPath(blackPaths[i], blackPaint);
                            canvas.drawPath(redPaths[i], borderPaint);
                            canvas.drawPath(blackPaths[i], borderPaint);
                        }
                    }
                    //Si cet item cricket est actif mais fermé par le joueur
                    else {
                        if (i % 2 == 0) {
                            canvas.drawPath(greenPaths[i], greenPaint);
                            canvas.drawPath(beigePaths[i], beigePaint);
                            canvas.drawPath(greenPaths[i], yellowPaint);
                            canvas.drawPath(beigePaths[i], yellowPaint);
                            canvas.drawPath(greenPaths[i], yellowBorderPaint);
                            canvas.drawPath(beigePaths[i], yellowBorderPaint);
                        } else {
                            canvas.drawPath(redPaths[i], redPaint);
                            canvas.drawPath(blackPaths[i], blackPaint);
                            canvas.drawPath(redPaths[i], yellowPaint);
                            canvas.drawPath(blackPaths[i], yellowPaint);
                            canvas.drawPath(redPaths[i], yellowBorderPaint);
                            canvas.drawPath(blackPaths[i], yellowBorderPaint);
                        }
                    }
                }
            }
            if (!externe) {
                if (!this.cricket.contains(21)) {
                    canvas.drawCircle(centerX, centerY, 20, greyPaint);
                    canvas.drawCircle(centerX, centerY, 20, darkGreyPaint);
                }
                else {
                    if (this.closed.contains(21)) {
                        canvas.drawCircle(centerX, centerY, 20, innerCircle);
                        canvas.drawCircle(centerX, centerY, 20, outerCircleBorder);
                    } else {
                        if (this.closedSolo.contains(21)) {
                            canvas.drawCircle(centerX, centerY, 20, centerPointPaint);
                            canvas.drawCircle(centerX, centerY, 20, outerCirclePaint);
                            canvas.drawCircle(centerX, centerY, 30, yellowBorderPaint);
                            canvas.drawCircle(centerX, centerY, 30, yellowPaint);
                        } else {
                            canvas.drawCircle(centerX, centerY, 20, centerPointPaint);
                            canvas.drawCircle(centerX, centerY, 20, outerCirclePaint);
                        }
                    }
                }
            }
        }
        if (this.game.equals(eGames.HiddenCricket)) {
            for (int i = 0; i < numSlices; i++) {
                /*Si le slice n'est pas un item cricket*/
                if (!this.cricket.contains(orderedNumbers[i])) {
                    if (i % 2 == 0) {
                        canvas.drawPath(greenPaths[i], darkGreyPaint);
                        canvas.drawPath(beigePaths[i], greyPaint);
                        canvas.drawPath(greenPaths[i], darkGreyPaint);
                        canvas.drawPath(beigePaths[i], greyPaint);
                        canvas.drawPath(greenPaths[i], borderPaint);
                        canvas.drawPath(beigePaths[i], borderPaint);
                    } else {
                        canvas.drawPath(redPaths[i], darkGreyPaint);
                        canvas.drawPath(blackPaths[i], greyPaint);
                        canvas.drawPath(redPaths[i], darkGreyPaint);
                        canvas.drawPath(blackPaths[i], greyPaint);
                        canvas.drawPath(redPaths[i], borderPaint);
                        canvas.drawPath(beigePaths[i], borderPaint);
                    }
                }
                else {
                    /*Si le slice est un item mais pas decouvert*/
                    if (!this.touched.contains(orderedNumbers[i])) {
                        if (i % 2 == 0) {
                            canvas.drawPath(greenPaths[i], darkGreyPaint);
                            canvas.drawPath(beigePaths[i], greyPaint);
                            canvas.drawPath(greenPaths[i], darkGreyPaint);
                            canvas.drawPath(beigePaths[i], greyPaint);
                            canvas.drawPath(greenPaths[i], borderPaint);
                            canvas.drawPath(beigePaths[i], borderPaint);
                        } else {
                            canvas.drawPath(redPaths[i], darkGreyPaint);
                            canvas.drawPath(blackPaths[i], greyPaint);
                            canvas.drawPath(redPaths[i], darkGreyPaint);
                            canvas.drawPath(blackPaths[i], greyPaint);
                            canvas.drawPath(redPaths[i], borderPaint);
                            canvas.drawPath(beigePaths[i], borderPaint);
                        }
                    } else {
                        /*Si cet item cricket est fermé*/
                        if (this.closed.contains(orderedNumbers[i])) {
                            if (i % 2 == 0) {
                                canvas.drawPath(greenPaths[i], greenPaint);
                                canvas.drawPath(beigePaths[i], beigePaint);
                                canvas.drawPath(greenPaths[i], lowDarkGreyPaint);
                                canvas.drawPath(beigePaths[i], lowGreyPaint);
                                canvas.drawPath(greenPaths[i], borderPaint);
                                canvas.drawPath(beigePaths[i], borderPaint);
                            } else {
                                canvas.drawPath(redPaths[i], redPaint);
                                canvas.drawPath(blackPaths[i], blackPaint);
                                canvas.drawPath(redPaths[i], lowDarkGreyPaint);
                                canvas.drawPath(blackPaths[i], lowGreyPaint);
                                canvas.drawPath(redPaths[i], borderPaint);
                                canvas.drawPath(blackPaths[i], borderPaint);
                            }
                        }
                        //Si cet item cricket est actif
                        else if (!this.closedSolo.contains(orderedNumbers[i])) {
                            if (i % 2 == 0) {
                                canvas.drawPath(greenPaths[i], greenPaint);
                                canvas.drawPath(beigePaths[i], beigePaint);
                                canvas.drawPath(greenPaths[i], borderPaint);
                                canvas.drawPath(beigePaths[i], borderPaint);
                            } else {
                                canvas.drawPath(redPaths[i], redPaint);
                                canvas.drawPath(blackPaths[i], blackPaint);
                                canvas.drawPath(redPaths[i], borderPaint);
                                canvas.drawPath(blackPaths[i], borderPaint);
                            }
                        }
                        //Si cet item cricket est actif mais fermé par le joueur
                        else {
                            if (i % 2 == 0) {
                                canvas.drawPath(greenPaths[i], greenPaint);
                                canvas.drawPath(beigePaths[i], beigePaint);
                                canvas.drawPath(greenPaths[i], yellowPaint);
                                canvas.drawPath(beigePaths[i], yellowPaint);
                                canvas.drawPath(greenPaths[i], yellowBorderPaint);
                                canvas.drawPath(beigePaths[i], yellowBorderPaint);
                            } else {
                                canvas.drawPath(redPaths[i], redPaint);
                                canvas.drawPath(blackPaths[i], blackPaint);
                                canvas.drawPath(redPaths[i], yellowPaint);
                                canvas.drawPath(blackPaths[i], yellowPaint);
                                canvas.drawPath(redPaths[i], yellowBorderPaint);
                                canvas.drawPath(blackPaths[i], yellowBorderPaint);
                            }
                        }
                    }
                }
            }
            if (!externe) {
                if (!this.cricket.contains(21)) {
                    canvas.drawCircle(centerX, centerY, 20, greyPaint);
                    canvas.drawCircle(centerX, centerY, 20, darkGreyPaint);
                } else {
                     if (this.closed.contains(21)) {
                         canvas.drawCircle(centerX, centerY, 20, innerCircle);
                         canvas.drawCircle(centerX, centerY, 20, outerCircleBorder);
                     }
                     else {
                        if (this.closedSolo.contains(21)) {
                                canvas.drawCircle(centerX, centerY, 20, centerPointPaint);
                                canvas.drawCircle(centerX, centerY, 20, outerCirclePaint);
                                canvas.drawCircle(centerX, centerY, 30, yellowPaint);
                                canvas.drawCircle(centerX, centerY, 30, yellowBorderPaint);
                        }
                        else
                        {
                            canvas.drawCircle(centerX, centerY, 20, centerPointPaint);
                            canvas.drawCircle(centerX, centerY, 20, outerCirclePaint);
                        }
                     }
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*On initialise les variables pour construire la moitié exterieur de la cible*/
        InitVars(true);

        /*Création du contours de la cible*/
        canvas.drawCircle(centerX, centerY, innerRadius, blackCirclePaint);
        canvas.rotate(9, centerX, centerY);

        /*On génère les squelettes des tranches de la moitié exterieur de la cible*/
        Slices(true,canvas);
        /*On les colorisent*/
        ColoriseSlices(true, canvas);

        /*On initialise les variables pour construire la moitié interieur de la cible*/
        InitVars(false);
        /*On génère les squelettes des tranches de la moitié interieur de la cible*/
        Slices(false,canvas);
        /*On les colorisent*/
        ColoriseSlices(false, canvas);
    }

    public void setTouched(int[] touched)
    {
        for(int i : touched)
            this.touched.add(i);
    }
    public void setClosed(int[] closed)
    {
        for(int i : closed)
            this.closed.add(i);
    }
    public void setCricketItems(int[] closed)
    {
        for(int i : closed)
            this.cricket.add(i);
    }
    public void setClosedSoloItems(int[] closed)
    {
        for(int i : closed)
            this.closedSolo.add(i);
    }
    public void setGame(eGames game)
    {
        this.game = game;
    }
}
