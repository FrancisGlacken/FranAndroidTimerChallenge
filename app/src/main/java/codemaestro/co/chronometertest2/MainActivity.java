package codemaestro.co.chronometertest2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView timerView;
    private Button startButton, pauseButton, resetButton, commitButton;
    private CountDownTimer timerValue;
    private long startTime, displayTime, currentTime, timeAfterLife;
    private boolean timerRunning;
    private FormatTimer format = new FormatTimer();
    private static final String PREFS_FILE = "SharedPreferences";
    private static final int PREFS_MODE = Context.MODE_PRIVATE;

    //TODO: Remove these views
    private TextView view1, view2, view3, view4, view5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create sharedPrefs reference and get values
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, PREFS_MODE);
        timeAfterLife = prefs.getLong("time_after_life", 0);
        currentTime = prefs.getLong("current_time", 0);
        timerRunning = prefs.getBoolean("timer_running_boolean", false);

        // View references
        startButton = findViewById(R.id.start_button);
        pauseButton = findViewById(R.id.pause_button);
        resetButton = findViewById(R.id.reset_button);
        commitButton = findViewById(R.id.commit_button);
        timerView = findViewById(R.id.timerView);

        // Temp views for showing the relevant variables
        view1 = findViewById(R.id.View1);
        view2 = findViewById(R.id.View2);
        view3 = findViewById(R.id.View3);
        view4 = findViewById(R.id.View4);
        view5 = findViewById(R.id.View5);
        SetTempViews();

        if(timerRunning) { // If timer was running, restart it with the correct values

            // Get the time that the app was destroyed for
            timeAfterLife = SystemClock.elapsedRealtime() - timeAfterLife;

            // Set the startTime to the adjusted time and start the timer
            startTime = SystemClock.elapsedRealtime() - currentTime - timeAfterLife;
            startTimer();
            StartEnabledButtons();

        } else { // If timer was not running, set the timerView and enable the right buttons
            setClock(currentTime);
            if(currentTime > 0){
                PauseEnabledButtons();
            } else {
                DefaultEnabledButtons();
            }
        }

        // Set the clock to the saved time
        //setClock(currentTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeAfterLife = SystemClock.elapsedRealtime();
        saveToSharedPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeAfterLife = SystemClock.elapsedRealtime();
        saveToSharedPreferences();
    }

    /**
     * Timer Logic
     */

    public void startTimer() {
        timerValue = new CountDownTimer(86400000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                displayTime = SystemClock.elapsedRealtime() - startTime;
                currentTime = displayTime;
                setClock(displayTime);
                SetTempViews();
            }
            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    public void startButton(View view) {
        // Get the startTime and start the timer
        startTime = SystemClock.elapsedRealtime() - currentTime;
        startTimer();
        StartEnabledButtons();
        timerRunning = true;
    }

    public void pauseButton(View view) {
        // Get the currentTime and cancel the timer
        currentTime = displayTime;
        timerValue.cancel();
        PauseEnabledButtons();
        timerRunning = false;
        SetTempViews();
    }

    public void resetButton(View view) {
        // Reset timer
        currentTime = 0;
        displayTime = 0;
        setClock(0);
        DefaultEnabledButtons();
        SetTempViews();
    }

    public void commitButton(View view) {
        //Todo: Save your time to the Database or Shared Preferences for use later
        Toast.makeText(this, "This just resets the values to current form", Toast.LENGTH_SHORT).show();
        SetTempViews();
    }

    /**
     * SetEnabled Button Methods - Three states of buttons
     */

    public void StartEnabledButtons() {
        // timer Started
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        resetButton.setEnabled(false);
        commitButton.setEnabled(false);
    }

    public void PauseEnabledButtons() {
        // timer Paused
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(true);
        commitButton.setEnabled(true);
    }

    public void DefaultEnabledButtons() {
        // timer Reset
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);
        commitButton.setEnabled(false);
    }

    /**
     * Shared Preferences method
     */
    public void saveToSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, PREFS_MODE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("current_time", currentTime);
        editor.putLong("time_after_life", timeAfterLife);
        editor.putBoolean("timer_running_boolean", timerRunning);
        editor.apply();
    }

    /**
     * setClock Method
     */
    public void setClock(long displayTime) {
        timerView.setText(format.FormatMillisIntoHMS(displayTime));
    }

    /**
     * Set temp views
     */
    public void SetTempViews() {
        view1.setText("startTime: " + startTime);
        view2.setText("currentTime: " + currentTime);
        view3.setText("displayTime: " + displayTime);
        view4.setText("timeAfterLife: " + timeAfterLife);
        view5.setText("timerRunning: " + timerRunning);
    }

}
