package ch.lburgy.ligrettoscore.ui;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ch.lburgy.ligrettoscore.R;
import ch.lburgy.ligrettoscore.database.Player;

public class RVAdapterPlayersRound extends RecyclerView.Adapter<RVAdapterPlayersRound.ViewHolder> {

    private static final int NIL = -1;

    private final ArrayList<Player> players;
    private int[] cardsLigretto;
    private int[] cardsCenter;
    private int indexInput;

    private LayoutInflater inflater;
    private Resources resources;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtPlayerName;
        public final LinearLayout layoutInputs;
        public final View cell;

        public ViewHolder(View v) {
            super(v);
            txtPlayerName = v.findViewById(R.id.player_name);
            layoutInputs = v.findViewById(R.id.layout_inputs);
            cell = v.findViewById(R.id.player_cell);
        }

        public void bind(Resources resources, LayoutInflater inflater, final ArrayList<Player> players, final int[] cardsLigretto, final int[] cardsCenter, final int indexInput, final int position) {
            final Player player = players.get(position);
            txtPlayerName.setText(player.getName());

            if (layoutInputs.getChildCount() == 0) {
                // Create the views for the cards
                if (indexInput == -1 || indexInput == 0) {
                    View viewCardsLigretto = inflater.inflate(R.layout.input_round, layoutInputs, false);
                    TextView titleCardsLigretto = viewCardsLigretto.findViewById(R.id.title);
                    titleCardsLigretto.setText(resources.getString(R.string.cards_ligretto));
                    layoutInputs.addView(viewCardsLigretto);
                }

                if (indexInput == -1 || indexInput == 1) {
                    View viewCardsCenter = inflater.inflate(R.layout.input_round, layoutInputs, false);
                    TextView titleCardsCenter = viewCardsCenter.findViewById(R.id.title);
                    titleCardsCenter.setText(resources.getString(R.string.cards_center));
                    layoutInputs.addView(viewCardsCenter);
                }
            }

            if (indexInput == -1 || indexInput == 0) {
                ExtendedEditText inputCardsLigretto = layoutInputs.getChildAt(0).findViewById(R.id.input);
                inputCardsLigretto.clearTextChangedListeners();
                inputCardsLigretto.setSelectAllOnFocus(true);

                if (cardsLigretto[position] != NIL)
                    inputCardsLigretto.setText(String.format("%d", cardsLigretto[position]));
                else
                    inputCardsLigretto.setText("");

                inputCardsLigretto.addTextChangedListener(new TextWatcher() {
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
            }

            if (indexInput == -1 || indexInput == 1) {
                ExtendedEditText inputCardsCenter;

                if (indexInput == -1)
                    inputCardsCenter = layoutInputs.getChildAt(1).findViewById(R.id.input);
                else
                    inputCardsCenter = layoutInputs.getChildAt(0).findViewById(R.id.input);

                inputCardsCenter.clearTextChangedListeners();

                if (cardsCenter[position] != NIL)
                    inputCardsCenter.setText(String.format("%d", cardsCenter[position]));
                else
                    inputCardsCenter.setText("");

                inputCardsCenter.addTextChangedListener(new TextWatcher() {
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
            }

            cell.setBackgroundColor(player.getColor());
        }
    }

    public RVAdapterPlayersRound(Resources resources, ArrayList<Player> players, int[] cardsLigretto, int[] cardsCenter, int indexInput) {
        this.resources = resources;
        this.players = players;
        this.cardsLigretto = cardsLigretto;
        this.cardsCenter = cardsCenter;
        this.indexInput = indexInput;
    }

    @NotNull
    @Override
    public RVAdapterPlayersRound.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cell_player_round, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(resources, inflater, players, cardsLigretto, cardsCenter, indexInput, position);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
