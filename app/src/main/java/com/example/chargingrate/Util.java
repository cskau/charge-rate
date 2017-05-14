package com.example.chargingrate;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import android.support.v4.app.NotificationCompat;


public class Util {
  public static void notify(
      Context context,
      String title,
      String text,
      int notificationId
      ) {
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(title)
        .setContentText(text)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(
        Context.NOTIFICATION_SERVICE);

    notificationManager.notify(
        notificationId,
        notificationBuilder.build());
  }
}
