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
    private boolean timerRunning;
    private CountDownTimer timerValue;
    private long startTime, currentTime, timeAfterLife;
    private Button startButton, pauseButton, resetButton, commitButton;
    FormatTimer format = new FormatTimer();


    private static final String PREFS_FILE = "SharedPreferences";
    private static final int PREFS_MODE = Context.MODE_PRIVATE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences prefs = getSharedPreferences(PREFS_FILE, PREFS_MODE);

        startButton = findViewById(R.id.start_button);
        pauseButton = findViewById(R.id.pause_button);
        resetButton = findViewById(R.id.reset_button);
        commitButton = findViewById(R.id.commit_button);
        timerView = findViewById(R.id.timerView);

        timeAfterLife = prefs.getLong("time_after_life", 0);
        currentTime = prefs.getLong("current_time", 0);
        timerRunning = prefs.getBoolean("timer_running_boolean", false);

        setClock();



        if(timerRunning) {

            timeAfterLife = SystemClock.elapsedRealtime() - timeAfterLife;
            startTime = SystemClock.elapsedRealtime() - currentTime - timeAfterLife;

            startTimer();
            startButton.setEnabled(false);
            resetButton.setEnabled(false);
        } else {
            pauseButton.setEnabled(false);
            resetButton.setEnabled(false);
            commitButton.setEnabled(false);
        }

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

    public void startTimer() {
        timerValue = new CountDownTimer(86400000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentTime = SystemClock.elapsedRealtime() - startTime;
                setClock();
            }
            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    public void Start(View view) {
        startTime = SystemClock.elapsedRealtime();
        startTimer();
        pauseButton.setEnabled(true);
        startButton.setEnabled(false);
        resetButton.setEnabled(false);
        resetButton.setEnabled(false);
        timerRunning = true;
    }

    public void pauseButton(View view) {
        timerValue.cancel();
        pauseButton.setEnabled(false);
        startButton.setEnabled(true);
        resetButton.setEnabled(true);
        commitButton.setEnabled(true);
        timerRunning = false;
        setClock();
    }

    public void resetButton(View view) {
        setClock();

        resetButton.setEnabled(false);
        commitButton.setEnabled(false);
    }

    public void commitButton(View view) {
        saveToSharedPreferences();
        commitButton.setEnabled(false);
    }

    public void saveToSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, PREFS_MODE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("current_time", currentTime);
        editor.putLong("time_after_life", timeAfterLife);
        editor.putBoolean("timer_running_boolean", timerRunning);
        editor.apply();
    }

    public void setClock() {
        timerView.setText(format.FormatMillisIntoHMS(currentTime));
    }
}
