package com.example.cyrus.chapter4;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.Handler;


public class TimerActivity extends Activity {

    //any use for privates?
    static String CLASS_NAME;
    protected TextView counter;
    protected Button start;
    protected Button stop;
    protected boolean timerRunning;
    protected long startedAt;
    protected long lastStopped;
    private static long UPDATE_EVERY = 200;//use 200 rather than 1000 because you will start missing seconds...
    protected Handler handler;
    protected UpdateTimer updateTimer;
    protected Vibrator vibrate;//no that's not dirty at all XD
    protected long lastSeconds;


    public TimerActivity()
    {
        CLASS_NAME = getClass().getName();
    }


    @Override//called when the activity is created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //TextView hello = (TextView) findViewById(R.id.timer);
        if(BuildConfig.DEBUG)//enable strict mode for better debugging in early dev
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());//ya....... Idk either

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());//these detect all issues with threading and displays them to the log
            //it also detects simple memory leaks, and will stop the application These are an example of function chaining (notice they both end in builder.
        }

        //hello.setText("On your Bike!");//find text and sets it to said text this is done through the Id system
        //see above textView hello

        //findViewById must be called after setContentView or RTE
        counter = (TextView) findViewById(R.id.timer);
        start = (Button) findViewById(R.id.start_button);
        stop = (Button) findViewById(R.id.stop_button);

        enableButtons();
    }

    public void clickedStart(View view)//see the little red button? CLICK THE BUTTON
    {
        Log.d(CLASS_NAME, "Clicked Start button.");//logs

        startedAt = System.currentTimeMillis();//start the timer
        timerRunning = true;//true/false to enable buttons

        enableButtons();
        setTimeDisplay();

        handler = new Handler();//class runner
        updateTimer = new UpdateTimer();//class runner
        handler.postDelayed(updateTimer, UPDATE_EVERY);//just like C#
    }

    public void clickedStop(View view)//see the little red button? CLICK THE BUTTON
    {
        Log.d(CLASS_NAME, "Clicked Stop button");//logs

        lastStopped = System.currentTimeMillis();//stop the timer
        timerRunning = false;//true false to enable buttons

        enableButtons();
        setTimeDisplay();

        handler.removeCallbacks(updateTimer);//stop any pending call to run the method
        handler = null;
    }

    //start activity life cycle learning
    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(CLASS_NAME, "onStart");

        vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (vibrate == null)
        {
            Log.w(CLASS_NAME, "No Vibration Service exists.");
        }

        if(timerRunning)
        {
            handler = new Handler();
            updateTimer = new UpdateTimer();
            handler.postDelayed(updateTimer, UPDATE_EVERY);
        }
    }


    public void enableButtons()
    {
        Log.d(CLASS_NAME, "Set buttons enabled/disabled");//logs
        start.setEnabled(!timerRunning);//swap
        stop.setEnabled(timerRunning);//swap
    }

    protected void setTimeDisplay()
    {
        String display;
        long timeNow, diff, seconds, minutes, hours;

        //Log.d(CLASS_NAME,"Setting Time Display.");//omg LOGS =D

        if(timerRunning)//if timer is running.....
        {
            timeNow = System.currentTimeMillis();//grab current time
        }//end if
        else
        {
            timeNow = lastStopped;
        }//end else

        diff = timeNow - startedAt;//find the difference

        if(diff < 0)//negative time doesn't actually exist
        {
            diff = 0;
        }//end if

        seconds = diff/1000;//find seconds
        minutes = seconds/60;//find minutes
        hours = minutes/60;//find hours
        seconds = seconds % 60;//
        minutes = minutes % 60;//

        display = String.format("%d", hours) + ":"
                + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds);//setting up the display like 00:00:00

        counter.setText(display);//set the display
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(CLASS_NAME, "onResume");

        enableButtons();
        setTimeDisplay();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(CLASS_NAME, "onPause");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(CLASS_NAME, "onStop");

        if(timerRunning)
        {
            handler.removeCallbacks(updateTimer);
            updateTimer = null;
            handler = null;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(CLASS_NAME, "onDestroy");
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        Log.d(CLASS_NAME, "onRestart");
    }

    //end activity lifecycle area fun things to play with in order to understand where each thing happens

    class UpdateTimer implements Runnable//classes inside classes OH BOY!
    {
        public void run()
        {
            //Log.d(CLASS_NAME,"RUN!!!");//you can enable this if you want... I don't recommend it
            setTimeDisplay();

            if(handler != null)//null check and run every 200 millis
            {
                handler.postDelayed(this, UPDATE_EVERY);
            }//end if
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(CLASS_NAME, "Showing Menu.");//Log statements to show what's going on in the back end Great for tracking variables.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

