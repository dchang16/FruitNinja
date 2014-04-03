/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery
 */
package com.example.a4;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import com.example.a4complete.R;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private Model model;
    private MainView mainView;
    private TitleView titleView;
    public static Point displaySize;
    public static boolean gameRunning = true;
    static public Handler timerHandler = new Handler();
    static public Handler fruitHandler = new Handler();
    
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setTitle("CS349 A4 Demo");
        // save display size
        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);

        // initialize model
        model = new Model();

        // set view
        setContentView(R.layout.main);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // create the views and add them to the main activity
        titleView = new TitleView(this.getApplicationContext(), model);
        ViewGroup v1 = (ViewGroup) findViewById(R.id.main_1);
        v1.addView(titleView);

        mainView = new MainView(this.getApplicationContext(), model);
        ViewGroup v2 = (ViewGroup) findViewById(R.id.main_2);
        v2.addView(mainView);
        
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Play Again?");
        
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	MainView.lives = 5;
            	TitleView.score = 0;
                finish();
                startActivity(getIntent());
            }
         });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	MainView.lives = 5;
            	TitleView.score = 0;
                finish();
            }
        });
        Runnable timerRunnable = new Runnable() {
        	@Override
        	public void run() {
        		if (MainView.lives <= 0) {
        			alert.setMessage("Your score is : " + TitleView.score);
        			AlertDialog myAlert = alert.create();
        			myAlert.show();
            	} else {
            		mainView.moveFruit();
            		mainView.updateLives();
            		mainView.invalidate();
            		timerHandler.postDelayed(this, 30);
            	}
        		
        	}
        };
        Runnable fruitRunnable = new Runnable() {
        	@Override
        	public void run() {
        		if (MainView.lives <= 0) {

            	} else {
    	    		mainView.clearFruits();
    	    		mainView.createFruits();
    	    		fruitHandler.postDelayed(this, 7000);
            	}
        	}
        };
          
	    timerHandler.postDelayed(timerRunnable, 0);
	    fruitHandler.postDelayed(fruitRunnable, 0);

        // notify all views
        model.initObservers();
    }
    
    
}
