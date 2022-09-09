package ch.lburgy.ligrettoscore.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import ch.lburgy.ligrettoscore.R;
import ch.lburgy.ligrettoscore.database.Game;
import ch.lburgy.ligrettoscore.database.GameDao;
import ch.lburgy.ligrettoscore.database.MyDatabase;
import ch.lburgy.ligrettoscore.database.Player;
import ch.lburgy.ligrettoscore.database.PlayerDao;
import ch.lburgy.ligrettoscore.preferences.PrefManager;
import ch.lburgy.ligrettoscore.ui.RVAdapterPlayersScore;

public class ScoreboardActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ROUND = 0;
    private static final String KEY_SAVED_GAME = "game";
    private static final String KEY_SAVED_PLAYERS = "players";

    private PrefManager prefManager;
    private Game game;
    private ArrayList<Player> players;
    private RVAdapterPlayersScore rvAdapterPlayersScore;
    private GameDao gameDao;
    private PlayerDao playerDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        prefManager = new PrefManager(this);

        FloatingActionButton fabNextRound = findViewById(R.id.fab_next_round);
        fabNextRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ScoreboardActivity.this, RoundActivity.class);
                i.putExtra(getString(R.string.extra_game), game);
                i.putExtra(getString(R.string.extra_players), players);
                startActivityForResult(i, REQUEST_CODE_ROUND);
            }
        });

        MyDatabase myDatabase = MyDatabase.getInstance(getApplicationContext());
        gameDao = myDatabase.getGameDao();
        playerDao = myDatabase.getPlayerDao();

        if (savedInstanceState == null) {
            game = (Game) getIntent().getSerializableExtra(getString(R.string.extra_game));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    players = new ArrayList<>(playerDao.getPlayers(game.getId()));
                    Collections.sort(players, Player.PLAYER_COMPARATOR_SCORE);
                    initView();
                }
            }).start();
        } else {
            game = (Game) savedInstanceState.getSerializable(KEY_SAVED_GAME);
            players = (ArrayList<Player>) savedInstanceState.getSerializable(KEY_SAVED_PLAYERS);
            initView();
        }
    }

    private void initView() {
        boolean gm = game.getGamemode();
        String display = "";
        if(!gm){
            display = "Point mode";
        }
        else{
            display = "Turn mode";
        }

        setTitle(getString(R.string.activity_scoreboard, display));


        RecyclerView recyclerView = findViewById(R.id.recycler_view_players);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        rvAdapterPlayersScore = new RVAdapterPlayersScore(players);
        recyclerView.setAdapter(rvAdapterPlayersScore);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(KEY_SAVED_GAME, game);
        savedInstanceState.putSerializable(KEY_SAVED_PLAYERS, players);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ROUND && resultCode == RESULT_OK) {
            assert data != null;
            game = (Game) data.getSerializableExtra(getString(R.string.extra_game));
            assert game != null;
            game.setLastEdit(new Date());
            players.clear();
            players.addAll((ArrayList<Player>) Objects.requireNonNull(data.getSerializableExtra(getString(R.string.extra_players))));
            Collections.sort(players, Player.PLAYER_COMPARATOR_SCORE);
            rvAdapterPlayersScore.notifyDataSetChanged();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    gameDao.updateGame(game);
                    playerDao.updatePlayers(players);
                }
            }).start();
            PrefManager.setActualTurn(PrefManager.getActualTurn() + 1);
            if(!game.getGamemode()){
                // POINT MODE

                if (players.get(0).getScore() >= prefManager.getGamePoints()) {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.dialog_title_player_won, players.get(0).getName()))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //
                                }
                            })
                            .show();
                }
            }
            else{
                // TURN MODE
                Toast.makeText(this,  getString(R.string.toast_turn) + " " + PrefManager.getActualTurn() + " / " + prefManager.getGameTurns(), Toast.LENGTH_SHORT).show();
                if (PrefManager.getActualTurn() >= prefManager.getGameTurns()) {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.dialog_title_player_won, players.get(0).getName()))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //
                                }
                            })
                            .show();
                    PrefManager.setActualTurn(0);
                }
            }
        }
    }
}