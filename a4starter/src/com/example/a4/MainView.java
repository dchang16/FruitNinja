/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery & Michael Terry
 */
package com.example.a4;

import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/*
 * View of the main game area.
 * Displays pieces of fruit, and allows players to slice them.
 */
public class MainView extends View implements Observer {
    private final Model model;
    private final MouseDrag drag = new MouseDrag();
    public static int lives = 5;

    // Constructor
    MainView(Context context, Model m) {
        super(context);

        model = m;
        model.addObserver(this);


        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Log.d(getResources().getString(R.string.app_name), "Touch down");
                        drag.start(event.getX(), event.getY());
                        break;

                    case MotionEvent.ACTION_UP:
                        // Log.d(getResources().getString(R.string.app_name), "Touch release");
                        drag.stop(event.getX(), event.getY());

                        // find intersected shapes
                        Iterator<Fruit> i = model.getShapes().iterator();
                        while(i.hasNext()) {
                            Fruit s = i.next();
                            if (s.intersects(drag.getStart(), drag.getEnd()) && !s.sliced) {
                                try {
                                    Fruit[] newFruits = s.split(drag.getStart(), drag.getEnd());

                                    newFruits[0].translate(0, -10);
                                    newFruits[1].translate(0, +10);
                                    model.add(newFruits[0]);
                                    model.add(newFruits[1]);
                                    model.remove(s);
                                } catch (Exception ex) {
                                    Log.e("fruit_ninja", "Error: " + ex.getMessage());
                                }
                                TitleView.score++;
                            } else {
                                s.setFillColor(Color.BLUE);
                            }
                            invalidate();
                        }
                        break;
                }
                return true;
            }
        });
    }
    
    // inner class to track mouse drag
    // a better solution *might* be to dynamically track touch movement
    // in the controller above
    class MouseDrag {
        private float startx, starty;
        private float endx, endy;

        protected PointF getStart() { return new PointF(startx, starty); }
        protected PointF getEnd() { return new PointF(endx, endy); }

        protected void start(float x, float y) {
            this.startx = x;
            this.starty = y;
        }

        protected void stop(float x, float y) {
            this.endx = x;
            this.endy = y;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw background
        setBackgroundColor(Color.WHITE);

        // draw all pieces of fruit
        for (Fruit s : model.getShapes()) {
            s.draw(canvas);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        invalidate();
    }
    
    public void moveFruit() {
    	Iterator<Fruit> i = model.getShapes().iterator();
        while(i.hasNext()) {
            Fruit s = i.next();
            if(s.posx > MainActivity.displaySize.x / 2) {
                s.translate(-1 * s.velocity, s.gravity * -1);
            }
            else {
            	s.translate(s.velocity, s.gravity * -1);
            }
            s.posy = s.posy + s.gravity;
            s.gravity = s.gravity - 2;
        }
    }
    
    public void updateLives() {
    	Iterator<Fruit> i = model.getShapes().iterator();
        while(i.hasNext()) {
        	Fruit s = i.next();
        	if (s.posy < -1) {
        		lives = lives -1;
        		model.remove(s);
        	}
        }
    }
    
    public void createFruits() {
    	for(int i = 0; i < new Random().nextInt(5 - 2) + 2; i++) {
    		Fruit f = new Fruit(new float[] {0, 20, 20, 0, 40, 0, 60, 20, 60, 40, 40, 60, 20, 60, 0, 40});
    		f.translate(f.posx, MainActivity.displaySize.y);
            model.add(f);
    	}
    }
    
    public void clearFruits() {
    	model.clearFruits();
    }
    
}
