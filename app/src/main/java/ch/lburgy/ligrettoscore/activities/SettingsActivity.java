package ch.lburgy.ligrettoscore.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import ch.lburgy.ligrettoscore.R;
import ch.lburgy.ligrettoscore.preferences.PrefManager;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String keyTheme9;
    private String keyTheme10;
    private String keyRoundView;

    private PrefManager prefManager;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        keyTheme9 = getResources().getString(R.string.settings_key_theme_9);
        keyTheme10 = getResources().getString(R.string.settings_key_theme_10);
        keyRoundView = getResources().getString(R.string.settings_key_round_view);

        prefManager = new PrefManager(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (keyTheme9.equals(key)) {
            String themeString9 = sharedPreferences.getString(keyTheme9, "MODE_NIGHT_AUTO_BATTERY");
            changeTheme(themeString9);
        } else if (keyTheme10.equals(key)) {
            String themeString10 = sharedPreferences.getString(keyTheme10, "MODE_NIGHT_FOLLOW_SYSTEM");
            changeTheme(themeString10);
        } else if (keyRoundView.equals(key)) {
            String roundViewChoosen = sharedPreferences.getString(keyRoundView, "TOGETHER");
            prefManager.setRoundViewTogether("TOGETHER".equals(roundViewChoosen));
        }
    }

    private void changeTheme(String themeString) {
        int theme = -1;
        switch (themeString) {
            case "MODE_NIGHT_FOLLOW_SYSTEM":
                theme = MODE_NIGHT_FOLLOW_SYSTEM;
                break;
            case "MODE_NIGHT_AUTO_BATTERY":
                theme = MODE_NIGHT_AUTO_BATTERY;
                break;
            case "MODE_NIGHT_NO":
                theme = MODE_NIGHT_NO;
                break;
            case "MODE_NIGHT_YES":
                theme = MODE_NIGHT_YES;
                break;
        }
        AppCompatDelegate.setDefaultNightMode(theme);
        prefManager.setTheme(theme);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            String keyTheme9 = getResources().getString(R.string.settings_key_theme_9);
            String keyTheme10 = getResources().getString(R.string.settings_key_theme_10);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                findPreference(keyTheme10).setVisible(true);
            } else {
                findPreference(keyTheme9).setVisible(true);
            }

            findPreference(getString(R.string.settings_key_see_github)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/burgyl/LigrettoScore"));
                    startActivity(browserIntent);
                    return true;
                }
            });
            findPreference(getString(R.string.settings_key_developer)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:contact@lburgy.ch"));
                    startActivity(browserIntent);
                    return true;
                }
            });
            findPreference(getString(R.string.settings_key_license_colorpicker)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kristiyanP/colorpicker"));
                    startActivity(browserIntent);
                    return true;
                }
            });
        }
    }
}