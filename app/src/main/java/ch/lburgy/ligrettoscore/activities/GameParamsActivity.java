package ch.lburgy.ligrettoscore.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

import ch.lburgy.ligrettoscore.R;
import ch.lburgy.ligrettoscore.database.Game;
import ch.lburgy.ligrettoscore.database.GameDao;
import ch.lburgy.ligrettoscore.database.MyDatabase;
import ch.lburgy.ligrettoscore.database.Player;
import ch.lburgy.ligrettoscore.database.PlayerDao;
import ch.lburgy.ligrettoscore.ui.RVAdapterPlayersGameParams;
import petrov.kristiyan.colorpicker.ColorPicker;

public class GameParamsActivity extends AppCompatActivity implements RVAdapterPlayersGameParams.OnItemClickListener {

    private static final String KEY_SAVED_CHANGES_MADE = "changes_made";
    private static final String KEY_SAVED_GAME_NAME = "game_name";
    private static final String KEY_SAVED_PLAYERS = "players";
    private static final String KEY_SAVED_GAME = "game";

    private ArrayList<Player> players;
    private RVAdapterPlayersGameParams rvAdapterPlayersGameParams;
    private RecyclerView rvPlayers;
    private EditText txtGameName;
    private int[] colors;
    private boolean[] colorsTaken;
    private GameDao gameDao;
    private PlayerDao playerDao;
    private Game game;
    private int resultCode = RESULT_CANCELED;
    private boolean game_mode = false; // false is point, true is turn
    private int colorChoosen;
    private boolean changesMade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_params);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MyDatabase myDatabase = MyDatabase.getInstance(GameParamsActivity.this);
        gameDao = myDatabase.getGameDao();
        playerDao = myDatabase.getPlayerDao();

        txtGameName = findViewById(R.id.txt_game_name);

        if (savedInstanceState == null) {
            players = new ArrayList<>();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final long gameID = gameDao.getGreaterID() + 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtGameName.setText(getString(R.string.default_game_name, gameID));
                            initView();
                        }
                    });
                }
            }).start();
        } else {
            changesMade = savedInstanceState.getBoolean(KEY_SAVED_CHANGES_MADE);
            txtGameName.setText(savedInstanceState.getString(KEY_SAVED_GAME_NAME));
            players = (ArrayList<Player>) savedInstanceState.getSerializable(KEY_SAVED_PLAYERS);
            initView();
        }

        colors = getResources().getIntArray(R.array.ligretto_colors);
        colorsTaken = new boolean[colors.length];

        FloatingActionButton fabAddPlayer = findViewById(R.id.fab_add_player);
        editGameMode();
        fabAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (players.size() < colors.length) {
                    editPlayer(-1);
                } else {
                    Toast.makeText(GameParamsActivity.this, getString(R.string.error_max_players, colors.length), Toast.LENGTH_SHORT).show();
                }
            }
        });

        setupUI(findViewById(R.id.content_root));

    }

    private void initView() {
        rvPlayers = findViewById(R.id.recycler_view_players);
        rvPlayers.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(GameParamsActivity.this);
        rvPlayers.setLayoutManager(layoutManager);
        rvAdapterPlayersGameParams = new RVAdapterPlayersGameParams(players, this);
        rvPlayers.setAdapter(rvAdapterPlayersGameParams);
    }

    private void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                public boolean onTouch(View v, MotionEvent event) {
                    looseFocus();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void looseFocus() {
        View view = this.getCurrentFocus();
        if (view == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager)
                this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        this.getCurrentFocus().clearFocus();
    }

    private void editGameMode(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_game_mode, null);
        dialogBuilder.setView(dialogView);

        final Button button_point_mode = dialogView.findViewById(R.id.button_point_mode);
        final Button button_turn_mode = dialogView.findViewById(R.id.button_turn_mode);
        button_point_mode.setBackgroundResource(R.color.colorPrimary);
        button_turn_mode.setBackgroundResource(R.color.ligretto_blue_dark_grey);


        dialogBuilder.setTitle(getString(R.string.game_mode_dialog_title));
        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (button_point_mode.isSelected()) {
                    game_mode = false;
                }
                if (button_turn_mode.isSelected()) {
                    game_mode = true;
                }
            }
        });
        dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });


        button_point_mode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                button_point_mode.setBackgroundResource(R.color.colorPrimary);
                button_point_mode.setSelected(true);
                button_turn_mode.setBackgroundResource(R.color.ligretto_blue_dark_grey);
                button_turn_mode.setSelected(false);

            }
        });

        button_turn_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_point_mode.setBackgroundResource(R.color.ligretto_blue_dark_grey);
                button_point_mode.setSelected(false);
                button_turn_mode.setBackgroundResource(R.color.colorPrimary);
                button_turn_mode.setSelected(true);
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();




    }

    private void editPlayer(int position) {
        final Player player = position == -1 ? null : players.get(position);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_player, null);
        dialogBuilder.setView(dialogView);

        final EditText txtPlayerName = dialogView.findViewById(R.id.txt_player_name);
        txtPlayerName.requestFocus();

        if (player == null) {
            // take first color available
            for (int i = 0; i < colorsTaken.length; i++) {
                if (!colorsTaken[i]) {
                    colorChoosen = i;
                    break;
                }
            }
        } else {
            int colorID = player.getColor();
            for (int i = 0; i < colors.length; i++) {
                if (colors[i] == colorID) {
                    colorChoosen = i;
                    break;
                }
            }
            txtPlayerName.setText(player.getName());
        }
        final int lastColor = colorChoosen;

        TextView colorView = dialogView.findViewById(R.id.player_color);
        colorView.setBackgroundColor(colors[colorChoosen]);
        colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker(v);
            }
        });

        dialogBuilder.setTitle(getString(R.string.add_player_dialog_title));
        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            public void onClick(DialogInterface dialog, int whichButton) {
                String playerName = txtPlayerName.getText().toString();
                if (!"".equals(playerName)) {
                    if (player == null) {
                        // add a player
                        colorsTaken[colorChoosen] = true;
                        players.add(new Player(playerName, colors[colorChoosen]));
                        rvAdapterPlayersGameParams.notifyDataSetChanged();
                        rvPlayers.scrollToPosition(players.size() - 1);
                    } else {
                        // edit a player
                        colorsTaken[lastColor] = false;
                        colorsTaken[colorChoosen] = true;
                        player.setName(playerName);
                        player.setColor(colors[colorChoosen]);
                        rvAdapterPlayersGameParams.notifyDataSetChanged();
                    }
                    changesMade = true;
                }
            }
        });
        dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void showColorPicker(final View colorView) {
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                if (!colorsTaken[position]) {
                    colorChoosen = position;
                    colorView.setBackgroundColor(colors[position]);
                }
            }

            @Override
            public void onCancel() {
            }
        }).setColors(colors).setColumns(4).setTitle(getString(R.string.dialog_title_color_picker)).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_params, menu);
        return true;
    }

    @Override
    public void finish() {
        Intent i = getIntent();
        setResult(resultCode, i);
        i.putExtra(getString(R.string.extra_game), game);
        super.finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (players.size() >= 2 && !"".equals(txtGameName.getText().toString())) {
                    createGame();
                    return true;
                } else if ("".equals(txtGameName.getText().toString())) {
                    Toast.makeText(this, getString(R.string.error_game_name), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    Toast.makeText(this, getString(R.string.error_not_enough_players), Toast.LENGTH_SHORT).show();
                    return false;
                }
            case android.R.id.home:
                confirmQuit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createGame() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                game = new Game(txtGameName.getText().toString(), new Date(), players.size(), game_mode);
                long gameID = gameDao.insertGame(game);
                game.setId(gameID);
                for (Player player : players)
                    player.setGameID(gameID);

                playerDao.insertPlayers(players);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultCode = RESULT_OK;
                        finish();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        game.setGamemode(game_mode);
        savedInstanceState.putBoolean(KEY_SAVED_CHANGES_MADE, changesMade);
        savedInstanceState.putString(KEY_SAVED_GAME_NAME, txtGameName.getText().toString());
        savedInstanceState.putSerializable(KEY_SAVED_PLAYERS, players);
        savedInstanceState.putSerializable(KEY_SAVED_GAME, game);
        savedInstanceState.putString(KEY_SAVED_GAME_NAME, txtGameName.getText().toString());
    }

    @Override
    public void onItemClick(int position) {
        editPlayer(position);
    }

    @Override
    public void onBackPressed() {
        confirmQuit();
    }

    private void confirmQuit() {
        if (!changesMade) finish();
        else
            new android.app.AlertDialog.Builder(this)
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
    }
}