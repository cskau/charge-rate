package com.example.chargingrate;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.util.Log;

import android.content.Intent;
import android.content.IntentFilter;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;


public class ChargingRateActivity extends Activity {

  static final String TAG = ChargingRateActivity.class.getName();

  static final int CHARGING_RATE_NOTIFICATION_ID = 1;
  static final int UPDATE_FREQUENCY_MS = 1000;

  static Context context;
  static TextView statusTextView;


  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    statusTextView = (TextView)findViewById(R.id.status_textview);
    statusTextView.setText("Loading ..");

    context = this;

    installScheduler();
  }


  private void installScheduler() {
    // 1?
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
    ScheduledFuture future = executor.scheduleWithFixedDelay(
        new Runnable() {
          @Override
          public void run() {
            final BatteryState batteryState = BatteryState.getBatteryState(context);
            final String statusText = getStatusString(batteryState);

            runOnUiThread(
                new Runnable() {
                  @Override
                  public void run() {
                    statusTextView.setText(statusText);
                  }
                });

            Util.notify(
                context,
                "Charging rate",
                batteryState.getCurrent() + " mA",
                CHARGING_RATE_NOTIFICATION_ID);
          }
        },
        0,
        UPDATE_FREQUENCY_MS,
        TimeUnit.MILLISECONDS);
  }

  String getStatusString(BatteryState batteryState) {
    return (
        "isCharging = " + batteryState.isCharging()
            + " (" + batteryState.getPluggedString() + ")" + "\n"
            + "Current = " + batteryState.getCurrent() + " (mA?)\n"
            + "Voltage = " + batteryState.getVoltage() + " mV\n"
            + "Temperature = " + batteryState.getTemperature() / 10 + " °C\n"
            + "Level / Scale = " + batteryState.getLevel()
            + " / " + batteryState.getScale()
            + " (" + (batteryState.getCharge() * 100) + "%)" + "\n"
        );
  }
}
