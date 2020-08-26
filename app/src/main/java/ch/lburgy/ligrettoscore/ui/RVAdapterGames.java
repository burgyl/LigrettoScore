package ch.lburgy.ligrettoscore.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ch.lburgy.ligrettoscore.R;
import ch.lburgy.ligrettoscore.database.Game;

public class RVAdapterGames extends RecyclerView.Adapter<RVAdapterGames.ViewHolder> {

    private final ArrayList<Game> games;
    private final Resources resources;
    private final OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtGameName;
        public final TextView txtGameDate;
        public final TextView txtGameNumberPlayers;
        public final View cell;

        public ViewHolder(View v) {
            super(v);
            txtGameName = v.findViewById(R.id.game_name);
            txtGameDate = v.findViewById(R.id.game_date);
            txtGameNumberPlayers = v.findViewById(R.id.game_number_players);
            cell = v.findViewById(R.id.game_cell);
        }

        public void bind(final ArrayList<Game> games, final Resources resources, final OnItemClickListener listener, final int position) {
            final Game game = games.get(position);
            txtGameName.setText(game.getName());
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            txtGameDate.setText(df.format(game.getLastEdit()));
            txtGameNumberPlayers.setText(resources.getString(R.string.nb_players, game.getNbPlayers()));
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
            cell.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongItemClick(position);
                    return false;
                }
            });
        }
    }

    public RVAdapterGames(ArrayList<Game> games, Resources resources, OnItemClickListener listener) {
        this.games = games;
        this.resources = resources;
        this.listener = listener;
    }

    @NotNull
    @Override
    public RVAdapterGames.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_game, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(games, resources, listener, position);
    }

    @Override
    public int getItemCount() {
        if (games == null) return 0;
        return games.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onLongItemClick(int position);
    }
}
