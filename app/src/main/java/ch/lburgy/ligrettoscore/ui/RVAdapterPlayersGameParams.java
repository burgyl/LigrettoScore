package ch.lburgy.ligrettoscore.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ch.lburgy.ligrettoscore.R;
import ch.lburgy.ligrettoscore.database.Player;

public class RVAdapterPlayersGameParams extends RecyclerView.Adapter<RVAdapterPlayersGameParams.ViewHolder> {

    private final ArrayList<Player> players;
    private final OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtPlayerName;
        public final View cell;

        public ViewHolder(View v) {
            super(v);
            txtPlayerName = v.findViewById(R.id.player_name);
            cell = v.findViewById(R.id.player_cell);
        }

        public void bind(final ArrayList<Player> players, final OnItemClickListener listener, final int position) {
            final Player player = players.get(position);
            txtPlayerName.setText(player.getName());
            cell.setBackgroundColor(player.getColor());
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    public RVAdapterPlayersGameParams(ArrayList<Player> players, OnItemClickListener listener) {
        this.players = players;
        this.listener = listener;
    }

    @NotNull
    @Override
    public RVAdapterPlayersGameParams.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_player, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(players, listener, position);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
