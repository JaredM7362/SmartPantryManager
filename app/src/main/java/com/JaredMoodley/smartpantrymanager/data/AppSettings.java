package com.JaredMoodley.smartpantrymanager.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Reads and writes the user's preferences.
 *
 * SharedPreferences is used rather than SQLite because these are individual
 * values that are never sorted, filtered or related to anything else. A table
 * would add structure that nothing here needs.
 *
 * Wrapping the keys in one class means no Activity has to know the preference
 * file's name or spell a key correctly - a mistyped key string would silently
 * read back a default rather than failing.
 */
public class AppSettings {

    private static final String PREFS_NAME = "smart_pantry_settings";

    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_EXPIRY_DAYS = "expiry_warning_days";
    private static final String KEY_METRIC_UNITS = "use_metric_units";

    private static final boolean DEFAULT_EXPIRY_ALERTS = true;
    private static final int DEFAULT_EXPIRY_DAYS = 3;
    private static final boolean DEFAULT_METRIC_UNITS = true;

    private final SharedPreferences prefs;

    public AppSettings(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isExpiryAlertsEnabled() {
        return prefs.getBoolean(KEY_EXPIRY_ALERTS, DEFAULT_EXPIRY_ALERTS);
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        // apply() writes in the background. commit() would block the UI thread
        // until the write finished, which is unnecessary for a single flag.
        prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    /** How many days before expiry an item is flagged as expiring soon. */
    public int getExpiryWarningDays() {
        return prefs.getInt(KEY_EXPIRY_DAYS, DEFAULT_EXPIRY_DAYS);
    }

    public void setExpiryWarningDays(int days) {
        prefs.edit().putInt(KEY_EXPIRY_DAYS, days).apply();
    }

    /** True for grams and millilitres, false for ounces and cups. */
    public boolean isMetricUnits() {
        return prefs.getBoolean(KEY_METRIC_UNITS, DEFAULT_METRIC_UNITS);
    }

    public void setMetricUnits(boolean metric) {
        prefs.edit().putBoolean(KEY_METRIC_UNITS, metric).apply();
    }
}