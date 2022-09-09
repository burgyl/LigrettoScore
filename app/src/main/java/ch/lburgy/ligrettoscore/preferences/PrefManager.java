package ch.lburgy.ligrettoscore.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

public class PrefManager {
    public static final String DEFAULT_ROUND_VIEW_STRING = "TOGETHER";
    private static final boolean DEFAULT_ROUND_VIEW = true;
    private static final int DEFAULT_GAME_POINTS = 100;
    private static final int DEFAULT_GAME_TURNS = 10;

    private static final String PREF_NAME = "ch.lburgy.ligrettoscore";

    private static final String KEY_THEME = "theme";
    private static final String KEY_ROUND_VIEW = "round_view";
    private static final String KEY_GAME_POINTS = "game_points";
    private static final String KEY_GAME_TURNS = "turns";

    public static int getActualTurn() {
        return actualTurn;
    }

    public static void setActualTurn(int actualTurn) {
        PrefManager.actualTurn = actualTurn;
    }

    private static int actualTurn = 0;

    

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public PrefManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setTheme(int theme) {
        editor.putInt(KEY_THEME, theme);
        editor.commit();
    }

    public int getTheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return pref.getInt(KEY_THEME, MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            return pref.getInt(KEY_THEME, MODE_NIGHT_AUTO_BATTERY);
        }
    }

    public void setRoundViewTogether(boolean together) {
        editor.putBoolean(KEY_ROUND_VIEW, together);
        editor.commit();
    }

    public boolean getRoundViewTogether() {
        return pref.getBoolean(KEY_ROUND_VIEW, DEFAULT_ROUND_VIEW);
    }

    public void setGamePoints(int points) {
        editor.putInt(KEY_GAME_POINTS, points);
        editor.commit();
    }

    public void setGameTurns(int turns) {
        editor.putInt(KEY_GAME_TURNS, turns);
        editor.commit();
    }

    public int getGamePoints() {
        return pref.getInt(KEY_GAME_POINTS, DEFAULT_GAME_POINTS);
    }

    public int getGameTurns() {
        return pref.getInt(KEY_GAME_TURNS, DEFAULT_GAME_TURNS);
    }
}