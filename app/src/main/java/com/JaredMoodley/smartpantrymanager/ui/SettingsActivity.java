package com.JaredMoodley.smartpantrymanager.ui;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.JaredMoodley.smartpantrymanager.R;
import com.JaredMoodley.smartpantrymanager.data.AppSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * Lets the user change app preferences.
 *
 * Every control writes to SharedPreferences the moment it changes, so there is
 * no Save button: a settings screen that could be left in an unsaved state is
 * a source of confusion, and each value here is independent of the others.
 */
public class SettingsActivity extends AppCompatActivity {

    private AppSettings settings;
    private TextView textExpiryDaysLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsRoot),
                (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });

        settings = new AppSettings(this);

        MaterialSwitch switchAlerts = findViewById(R.id.switchExpiryAlerts);
        MaterialSwitch switchMetric = findViewById(R.id.switchMetric);
        SeekBar seekDays = findViewById(R.id.seekExpiryDays);
        textExpiryDaysLabel = findViewById(R.id.textExpiryDaysLabel);

        // Load the stored values into the controls before attaching listeners,
        // so that restoring state does not fire a save of the same value back.
        switchAlerts.setChecked(settings.isExpiryAlertsEnabled());
        switchMetric.setChecked(settings.isMetricUnits());

        // The SeekBar runs 0-13 and represents 1-14 days, because a SeekBar
        // always starts at zero and zero days of warning is not useful.
        seekDays.setProgress(settings.getExpiryWarningDays() - 1);
        updateDaysLabel(settings.getExpiryWarningDays());
        seekDays.setEnabled(settings.isExpiryAlertsEnabled());

        switchAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settings.setExpiryAlertsEnabled(isChecked);
            // The day count is meaningless with alerts off, so grey it out.
            seekDays.setEnabled(isChecked);
        });

        switchMetric.setOnCheckedChangeListener((buttonView, isChecked) ->
                settings.setMetricUnits(isChecked));

        seekDays.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                updateDaysLabel(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) { }

            /**
             * Saved on release rather than on every pixel of movement, which
             * would write to preferences dozens of times per drag.
             */
            @Override
            public void onStopTrackingTouch(SeekBar bar) {
                settings.setExpiryWarningDays(bar.getProgress() + 1);
            }
        });

        setUpBottomNavigation();
    }

    private void updateDaysLabel(int days) {
        textExpiryDaysLabel.setText(
                getString(R.string.settings_expiry_days) + ": " + days);
    }

    private void setUpBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottomNavSettings);
        nav.setSelectedItemId(R.id.nav_settings);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_settings) {
                return true;   // already here
            }
            if (id == R.id.nav_pantry) {
                finish();      // returns to the pantry list beneath this screen
                return true;
            }
            if (id == R.id.nav_recipes) {
                startActivity(new android.content.Intent(
                        this, SuggestedRecipesActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}