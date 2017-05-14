package com.example.chargingrate;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.os.BatteryManager;

import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;


public class BatteryState {

  static final String TAG = BatteryState.class.getName();


  private static final String [] currentFilePaths = {
    // Nexus 5?
    "/sys/class/power_supply/battery/batt_current",
    // FreeTel FT142A
    "/sys/class/power_supply/battery/BatteryAverageCurrent",
  };


  private int status;
  private int plugged;
  private int voltage;
  private int temperature;
  private int level;
  private int scale;


  private BatteryState(Intent batteryState) {
    status = batteryState.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    plugged = batteryState.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
    voltage = batteryState.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
    temperature = batteryState.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
    level = batteryState.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    scale = batteryState.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
  }

  public static BatteryState getBatteryState(Context context) {
    return new BatteryState(
        context.registerReceiver(
            null,
            new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
  }


  public int getStatus() {
    return status;
  }

  public int getPlugged() {
    return plugged;
  }

  public int getVoltage() {
    return voltage;
  }

  public int getTemperature() {
    return temperature;
  }

  public int getLevel() {
    return level;
  }

  public int getScale() {
    return scale;
  }


  public boolean isCharging() {
    // Are we charging / charged?
    int status = getStatus();
    boolean isCharging = (
        status == BatteryManager.BATTERY_STATUS_CHARGING ||
        status == BatteryManager.BATTERY_STATUS_FULL);

    return isCharging;
  }

  public String getPluggedString() {
    // How are we charging?
    int chargePlug = getPlugged();
    boolean usbCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_USB);
    boolean acCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_AC);

    if (usbCharge) {
      return "USB";
    } else if (acCharge) {
      return "AC";
    }

    return "";
  }

  public float getCharge() {
    // Range: [0.0, 1.0]
    return getLevel() / (float)getScale();
  }


  public String getCurrent() {
    for (String currentFilePath : currentFilePaths) {
      File f = new File(currentFilePath);
      if (f.exists() && f.isFile() && f.canRead()) {
        try {
          // TODO: some handling of different formats is needed.
          BufferedReader buf = new BufferedReader(new FileReader(f));
          return buf.readLine();
        } catch(FileNotFoundException e) {
          // TODO!!
          Log.e(TAG, e.getMessage(), e);
        } catch(IOException e) {
          // TODO!!
          Log.e(TAG, e.getMessage(), e);
        }
      }
    }

    return "";
  }
}
