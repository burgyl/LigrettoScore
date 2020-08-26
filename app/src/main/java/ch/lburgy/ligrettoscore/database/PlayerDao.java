package ch.lburgy.ligrettoscore.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlayerDao {

    @Insert
    long insertPlayer(Player player);

    @Insert
    void insertPlayers(List<Player> players);

    @Update
    void updatePlayers(List<Player> players);

    @Delete
    void deletePlayer(Player player);

    @Query("SELECT * FROM Player WHERE gameID=:gameID")
    List<Player> getPlayers(long gameID);
}
