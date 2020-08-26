package ch.lburgy.ligrettoscore.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import ch.lburgy.ligrettoscore.R;
import ch.lburgy.ligrettoscore.database.Game;
import ch.lburgy.ligrettoscore.database.Player;
import ch.lburgy.ligrettoscore.ui.RVAdapterPlayersRound;

public class RoundActivity extends AppCompatActivity {

    private static final String KEY_SAVED_GAME = "game";
    private static final String KEY_SAVED_PLAYERS = "players";
    private static final int NIL = -1;

    private Game game;
    private ArrayList<Player> players;
    private int resultCode = RESULT_CANCELED;
    private RecyclerView recyclerView;
    private int[] cardsCenter;
    private int[] cardsLigretto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            String extraGame = getString(R.string.extra_game);
            game = (Game) getIntent().getSerializableExtra(extraGame);
            String extraPlayers = getString(R.string.extra_players);
            players = (ArrayList<Player>) getIntent().getSerializableExtra(extraPlayers);
        } else {
            game = (Game) savedInstanceState.getSerializable(KEY_SAVED_GAME);
            players = (ArrayList<Player>) savedInstanceState.getSerializable(KEY_SAVED_PLAYERS);
        }

        setTitle(getString(R.string.activity_round, game.getName()));
        Collections.sort(players, Player.PLAYER_COMPARATOR_ID);

        cardsCenter = new int[players.size()];
        cardsLigretto = new int[players.size()];

        for (int i = 0; i < players.size(); i++) {
            cardsCenter[i] = NIL;
            cardsLigretto[i] = NIL;
        }

        recyclerView = findViewById(R.id.recycler_view_players);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        RVAdapterPlayersRound rvAdapterPlayersRound = new RVAdapterPlayersRound(players, cardsCenter, cardsLigretto);
        recyclerView.setAdapter(rvAdapterPlayersRound);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_round, menu);
        return true;
    }

    private void done() {
        // check all scores have been added
        for (int i = 0; i < players.size(); i++)
            if (cardsCenter[i] == NIL || cardsLigretto[i] == NIL) {
                Toast.makeText(this, getString(R.string.error_enter_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }
        // add scores
        for (int i = 0; i < players.size(); i++) {
            players.get(i).scoreAdd(cardsCenter[i] - 2 * cardsLigretto[i]);
        }
        // update positions
        Collections.sort(players, Player.PLAYER_COMPARATOR_SCORE);
        int lastScore = Integer.MAX_VALUE;
        int crtPos = 0;
        for (Player player : players) {
            if (player.getScore() < lastScore) {
                lastScore = player.getScore();
                crtPos++;
            }
            player.setPosition(crtPos);
        }
        resultCode = RESULT_OK;
        finish();
    }

    private void showInfos() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_infos))
                .setMessage(getString(R.string.dialog_infos, getString(R.string.cards_center), getString(R.string.cards_ligretto)))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing
                    }
                })
                .show();
    }

    @Override
    public void finish() {
        Intent i = getIntent();
        setResult(resultCode, i);
        i.putExtra(getString(R.string.extra_game), game);
        i.putExtra(getString(R.string.extra_players), players);
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                confirmQuit();
                return true;
            case R.id.action_infos:
                showInfos();
                return true;
            case R.id.action_done:
                done();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(KEY_SAVED_GAME, game);
        savedInstanceState.putSerializable(KEY_SAVED_PLAYERS, players);
    }

    @Override
    public void onBackPressed() {
        confirmQuit();
    }

    private void confirmQuit() {
        for (int i = 0; i < players.size(); i++) {
            if (cardsCenter[i] != NIL || cardsLigretto[i] != NIL) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.dialog_title_quit_round))
                        .setMessage(getString(R.string.dialog_quit_round))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return;
            }
        }
        finish();
    }
}