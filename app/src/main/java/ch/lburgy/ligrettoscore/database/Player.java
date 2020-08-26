package ch.lburgy.ligrettoscore.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Comparator;

@Entity
public class Player implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private int position;
    private int score;
    private int color;
    private long gameID;

    public Player(String name, int color) {
        this.name = name;
        this.color = color;
        position = 1;
        score = 0;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public int getScore() {
        return score;
    }

    public int getColor() {
        return color;
    }

    public long getGameID() {
        return gameID;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setGameID(long gameID) {
        this.gameID = gameID;
    }

    public void scoreAdd(int delta) {
        score += delta;
    }

    public static final Comparator<Player> PLAYER_COMPARATOR_ID = new Comparator<Player>() {
        @Override
        public int compare(Player o1, Player o2) {
            return Long.compare(o1.getId(), o2.getId());
        }
    };

    public static final Comparator<Player> PLAYER_COMPARATOR_SCORE = new Comparator<Player>() {
        @Override
        public int compare(Player o1, Player o2) {
            return Integer.compare(o2.getScore(), o1.getScore());
        }
    };
}
