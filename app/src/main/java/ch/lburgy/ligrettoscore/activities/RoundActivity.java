package ch.lburgy.ligrettoscore.activities;

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

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import ch.lburgy.ligrettoscore.R;
import ch.lburgy.ligrettoscore.database.Game;
import ch.lburgy.ligrettoscore.database.Player;
import ch.lburgy.ligrettoscore.preferences.PrefManager;
import ch.lburgy.ligrettoscore.ui.RVAdapterPlayersRound;

public class RoundActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ROUND = 0;
    private static final String KEY_SAVED_GAME = "game";
    private static final String KEY_SAVED_PLAYERS = "players";
    private static final int NIL = -1;

    private Game game;
    private ArrayList<Player> players;
    private int resultCode = RESULT_CANCELED;
    private int[] cardsCenter;
    private int[] cardsLigretto;
    private int indexInput;

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

        String extraIndexInput = getString(R.string.extra_index_input);
        indexInput = getIntent().getIntExtra(extraIndexInput, -1);
        PrefManager prefManager = new PrefManager(this);
        if (!prefManager.isRoundViewTogether()) indexInput++;

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

        RecyclerView recyclerView = findViewById(R.id.recycler_view_players);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        RVAdapterPlayersRound rvAdapterPlayersRound = new RVAdapterPlayersRound(getResources(), players, cardsLigretto, cardsCenter, indexInput);
        recyclerView.setAdapter(rvAdapterPlayersRound);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_round, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (indexInput == 0) {
            menu.findItem(R.id.action_next).setVisible(true);
        } else {
            menu.findItem(R.id.action_done).setVisible(true);
        }
        return true;
    }

    private boolean checkScoresEntered() {
        // check all scores have been added
        for (int i = 0; i < players.size(); i++)
            if (((indexInput == -1 || indexInput == 0) && cardsLigretto[i] == NIL) || ((indexInput == -1 || indexInput == 1) && cardsCenter[i] == NIL)) {
                Toast.makeText(this, getString(R.string.error_enter_all_fields), Toast.LENGTH_SHORT).show();
                return false;
            }
        return true;
    }

    private boolean checkNoScoresEntered() {
        // check none of the scores have been added
        for (int i = 0; i < players.size(); i++)
            if (((indexInput == -1 || indexInput == 0) && cardsLigretto[i] != NIL) || ((indexInput == -1 || indexInput == 1) && cardsCenter[i] != NIL)) {
                return false;
            }
        return true;
    }

    private void done() {
        // add scores
        System.out.println("indexInput: " + indexInput);
        for (int i = 0; i < players.size(); i++) {
            if (indexInput == -1 || indexInput == 0) {
                System.out.println("ligretto " + i + " : " + cardsLigretto[i]);
                System.out.println("before : " + players.get(i).getScore());
                players.get(i).scoreAdd(-2 * cardsLigretto[i]);
                System.out.println("after : " + players.get(i).getScore());
            }
            if (indexInput == -1 || indexInput == 1) {
                System.out.println("center " + i + " : " + cardsCenter[i]);
                System.out.println("before : " + players.get(i).getScore());
                players.get(i).scoreAdd(cardsCenter[i]);
                System.out.println("after : " + players.get(i).getScore());
            }
        }

        if (indexInput <= 0) {
            // update positions
            Collections.sort(players, Player.PLAYER_COMPARATOR_SCORE);
            int lastScore = Integer.MAX_VALUE;
            int lastPos = 1;
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                if (player.getScore() == lastScore) {
                    player.setPosition(lastPos);
                } else {
                    lastScore = player.getScore();
                    lastPos = i + 1;
                    player.setPosition(lastPos);
                }
            }
        }
        resultCode = RESULT_OK;
        finish();
    }

    private void showInfos() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_infos))
                .setMessage(getString(R.string.dialog_infos, getString(R.string.cards_ligretto), getString(R.string.cards_center)))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing
                    }
                })
                .show();
    }

    private void next() {
        Intent i = new Intent(RoundActivity.this, RoundActivity.class);
        i.putExtra(getString(R.string.extra_game), game);
        i.putExtra(getString(R.string.extra_players), players);
        i.putExtra(getString(R.string.extra_index_input), indexInput);
        startActivityForResult(i, REQUEST_CODE_ROUND);
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
            case R.id.action_next:
                if (checkScoresEntered()) {
                    next();
                }
                return true;
            case R.id.action_done:
                if (checkScoresEntered()) {
                    done();
                }
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
        if (!checkNoScoresEntered())
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
        else
            finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ROUND && resultCode == RESULT_OK && indexInput == 0) {
            players.clear();
            players.addAll((ArrayList<Player>) data.getSerializableExtra(getString(R.string.extra_players)));
            done();
        }
    }
}