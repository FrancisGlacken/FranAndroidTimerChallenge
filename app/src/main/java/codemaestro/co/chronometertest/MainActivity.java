package codemaestro.co.chronometertest;

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

import java.text.Format;

public class MainActivity extends AppCompatActivity {
    private CustomChronometer customChronometer;
    private TextView timerView;
    private boolean timerRunning;
    private CountDownTimer timerValue;
    private int seconds;
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

        seconds = prefs.getInt("seconds", 0);
        timerRunning = prefs.getBoolean("Timer Running Boolean", false);

        setClock();

        if(timerRunning) {
            startTimer();
            startButton.setEnabled(false);
            resetButton.setEnabled(false);
        } else {
            pauseButton.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveToSharedPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveToSharedPreferences();
    }

    public void startTimer() {
        timerValue = new CountDownTimer(86400000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                seconds++;
                setClock();
            }
            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    public void Start(View view) {
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
        seconds = 0;
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
        editor.putInt("seconds", seconds);
        editor.putBoolean("Timer Running Boolean", timerRunning);
        editor.apply();
    }

    public void setClock() {
        timerView.setText(format.FormatSeconds(seconds));
    }
}
