package com.example.sleeptracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sleeptracker.util.DBHelper;
import com.example.sleeptracker.util.PreferenceManagerUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final long MIN_SLEEP_DURATION = 3600000; // in milli-seconds - 1 hr
    public static final long NUM_INTERRUPTS = 3;
    public static final long MAX_INTERRUPT_DURATION = 300000; // in milli-seconds - 5 mins

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStartButton();
        setResetButton();
        setDisplayButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setStartButton() {
        final Button startButton = (Button) findViewById(R.id.btn_start);
        if (PreferenceManagerUtil.getIsStarted(getApplicationContext())) {
            startButton.setText(R.string.stop);
        } else {
            startButton.setText(R.string.start);
        }
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManagerUtil.setIsStarted(
                        !PreferenceManagerUtil.getIsStarted(getApplicationContext()), getApplicationContext());
                if (PreferenceManagerUtil.getIsStarted(getApplicationContext())) {
                    startButton.setText(R.string.stop);
                    Intent intent = new Intent(getApplicationContext(), UpdateService.class);
                    startService(intent);
                } else {
                    startButton.setText(R.string.start);
                    Intent intent = new Intent(getApplicationContext(), UpdateService.class);
                    stopService(intent);
                }
            }
        });
    }

    private void setResetButton() {
        final Button resetButton = (Button) findViewById(R.id.btn_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper.getInstance(getApplicationContext()).deleteAll();
                PreferenceManagerUtil.setSleeping(false, getApplicationContext());
                PreferenceManagerUtil.setLastStartTime(0, getApplicationContext());
                PreferenceManagerUtil.setLastStopTime(0, getApplicationContext());
            }
        });
    }

    private void setDisplayButton() {
        final Button displayButton = (Button) findViewById(R.id.btn_display);
        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView textView = (TextView) findViewById(R.id.display_data);
                textView.setVisibility(View.VISIBLE);
                List<String> records = DBHelper.getInstance(getApplicationContext()).getAll();
                if (records == null || records.isEmpty()) {
                    textView.setText("No data captured");
                    return;
                }
                String output = "";
                for (String entry : records) {
                    output += entry + "\n";
                }
                textView.setText(output);
            }
        });
    }

}
