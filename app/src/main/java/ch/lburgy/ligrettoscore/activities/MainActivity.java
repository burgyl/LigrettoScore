package ch.lburgy.ligrettoscore.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import ch.lburgy.ligrettoscore.R;
import ch.lburgy.ligrettoscore.database.Game;
import ch.lburgy.ligrettoscore.database.GameDao;
import ch.lburgy.ligrettoscore.database.MyDatabase;
import ch.lburgy.ligrettoscore.preferences.PrefManager;
import ch.lburgy.ligrettoscore.ui.RVAdapterGames;

public class MainActivity extends AppCompatActivity implements RVAdapterGames.OnItemClickListener {

    private static final int REQUEST_CODE_GAME_PARAMS = 0;
    private static final int REQUEST_CODE_SCOREBOARD = 1;
    private static final String KEY_SAVED_GAMES = "games";

    private static final int VIBRATION_LENGTH_SHORT = 100;

    private ArrayList<Game> games;
    private GameDao gameDao;
    private RVAdapterGames rvAdapterGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PrefManager prefManager = new PrefManager(this);
        AppCompatDelegate.setDefaultNightMode(prefManager.getTheme());

        gameDao = MyDatabase.getInstance(this).getGameDao();

        if (savedInstanceState == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    games = new ArrayList<>(gameDao.getGames());
                    Collections.sort(games, Game.GAME_COMPARATOR);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView();
                        }
                    });
                }
            }).start();
        } else {
            games = (ArrayList<Game>) savedInstanceState.getSerializable(KEY_SAVED_GAMES);
            initView();
        }

        FloatingActionButton fabAddGame = findViewById(R.id.fab_add_game);
        fabAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, GameParamsActivity.class), REQUEST_CODE_GAME_PARAMS);
            }
        });
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_games);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rvAdapterGames = new RVAdapterGames(games, getResources(), this);
        recyclerView.setAdapter(rvAdapterGames);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GAME_PARAMS && resultCode == RESULT_OK) {
            Intent i = new Intent(this, ScoreboardActivity.class);
            String extraGame = getString(R.string.extra_game);
            assert data != null;
            Game newGame = (Game) data.getSerializableExtra(getString(R.string.extra_game));
            i.putExtra(extraGame, newGame);
            startActivityForResult(i, REQUEST_CODE_SCOREBOARD);
            if(games != null) {
                games.add(newGame);
            }
            assert games != null;
            Collections.sort(games, Game.GAME_COMPARATOR);
            rvAdapterGames.notifyDataSetChanged();
        } else if (requestCode == REQUEST_CODE_SCOREBOARD) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    games.clear();
                    games.addAll(gameDao.getGames());
                    Collections.sort(games, Game.GAME_COMPARATOR);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rvAdapterGames.notifyDataSetChanged();
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(KEY_SAVED_GAMES, games);
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(this, ScoreboardActivity.class);
        i.putExtra(getString(R.string.extra_game), games.get(position));
        startActivityForResult(i, REQUEST_CODE_SCOREBOARD);
    }

    @Override
    public void onLongItemClick(final int position) {
        final Game game = games.get(position);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(VIBRATION_LENGTH_SHORT, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(VIBRATION_LENGTH_SHORT);
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_delete_game))
                .setMessage(getString(R.string.dialog_delete_game, game.getName()))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        games.remove(game);
                        rvAdapterGames.notifyDataSetChanged();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                gameDao.deleteGame(game);
                            }
                        }).start();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing
                    }
                })
                .show();
    }
}