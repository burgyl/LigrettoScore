package ch.lburgy.ligrettoscore.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ch.lburgy.ligrettoscore.R;
import ch.lburgy.ligrettoscore.database.Player;

public class RVAdapterPlayersRound extends RecyclerView.Adapter<RVAdapterPlayersRound.ViewHolder> {

    private static final int NIL = -1;

    private final ArrayList<Player> players;
    private int[] cardsCenter;
    private int[] cardsLigretto;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtPlayerName;
        public final EditText editCardsCenter;
        public final EditText editCardsLigretto;
        public final View cell;

        public ViewHolder(View v) {
            super(v);
            txtPlayerName = v.findViewById(R.id.player_name);
            editCardsCenter = v.findViewById(R.id.cards_center);
            editCardsLigretto = v.findViewById(R.id.cards_ligretto);
            cell = v.findViewById(R.id.player_cell);
        }

        public void bind(final ArrayList<Player> players, final int[] cardsCenter, final int[] cardsLigretto, final int position) {
            final Player player = players.get(position);
            txtPlayerName.setText(player.getName());
            if (cardsCenter[position] != NIL)
                editCardsCenter.setText(String.format("%d", cardsCenter[position]));
            if (cardsLigretto[position] != NIL)
                editCardsLigretto.setText(String.format("%d", cardsLigretto[position]));
            editCardsCenter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        cardsCenter[position] = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        cardsCenter[position] = NIL;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            editCardsLigretto.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        cardsLigretto[position] = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        cardsLigretto[position] = NIL;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            cell.setBackgroundColor(player.getColor());
        }
    }

    public RVAdapterPlayersRound(ArrayList<Player> players, int[] cardsCenter, int[] cardsLigretto) {
        this.players = players;
        this.cardsCenter = cardsCenter;
        this.cardsLigretto = cardsLigretto;
    }

    @NotNull
    @Override
    public RVAdapterPlayersRound.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_player_round, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(players, cardsCenter, cardsLigretto, position);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
