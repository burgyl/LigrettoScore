package ch.lburgy.ligrettoscore.ui;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ch.lburgy.ligrettoscore.R;
import ch.lburgy.ligrettoscore.database.Player;

public class RVAdapterPlayersScore extends RecyclerView.Adapter<RVAdapterPlayersScore.ViewHolder> {

    private final ArrayList<Player> players;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtPlayerPosition;
        public final TextView txtPlayerName;
        public final TextView txtPlayerScore;
        public final View cell;

        public ViewHolder(View v) {
            super(v);
            txtPlayerPosition = v.findViewById(R.id.player_position);
            txtPlayerName = v.findViewById(R.id.player_name);
            txtPlayerScore = v.findViewById(R.id.player_score);
            cell = v.findViewById(R.id.player_cell);
        }

        @SuppressLint("DefaultLocale")
        public void bind(final ArrayList<Player> players, final int position) {
            final Player player = players.get(position);
            txtPlayerPosition.setText(String.format("%d.", player.getPosition()));
            txtPlayerName.setText(player.getName());
            txtPlayerScore.setText(String.format("%d", player.getScore()));
            cell.setBackgroundColor(player.getColor());
        }
    }

    public RVAdapterPlayersScore(ArrayList<Player> players) {
        this.players = players;
    }

    @NotNull
    @Override
    public RVAdapterPlayersScore.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_player_score, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(players, position);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
