package com.example.notdefterirevize;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class CheckedBattery extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            boolean isCharging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) != 0;
            if (isCharging) {
                System.out.println("Şarj oluyor: " + batteryLevel);
            }else{
                System.out.println("Şarjdan çıkarıldı : "+batteryLevel);
            }
        }
    }
}
