package com.cominatyou.batterytile.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.HashMap;
import java.util.List;

/**
 * Class for user-configurable scanf-style formatting of tile text.
 */
public class TileTextFormatter {
    private final HashMap<String, Integer> formatters = new HashMap<>();
    private final List<String> formats = List.of("c", "a", "t", "f", "v", "l");

    public TileTextFormatter(Context context) {
        final BatteryManager bm = context.getSystemService(BatteryManager.class);
        final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        assert batteryIntent != null;

        // CURRENT_NOW and CURRENT_AVERAGE are positive when charging and negative when discharging
        formatters.put("c", bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW));
        formatters.put("a", bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE));

        // Temperature of the battery
        formatters.put("t", batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)); // Celsius
        formatters.put("f", (int) (batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) * 1.8 + 32)); // Fahrenheit

        // Voltage of the battery
        formatters.put("v", batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0));

        // Battery level
        formatters.put("l", batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0));
    }

    public String format(String format) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < format.length(); i++) {
            if (format.charAt(i) == '%') {
                if (i + 1 < format.length()) {
                    final String formatter = format.substring(i + 1, i + 2);
                    if (formats.contains(formatter)) {
                        formatted.append(formatters.get(formatter));
                        i++;
                    }
                    else {
                        formatted.append('%');
                    }
                }
                else {
                    formatted.append('%');
                }
            }
            else {
                formatted.append(format.charAt(i));
            }
        }

        return formatted.toString();
    }
}