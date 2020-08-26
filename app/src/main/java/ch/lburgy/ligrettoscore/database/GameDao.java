package ch.lburgy.ligrettoscore.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GameDao {

    @Insert
    long insertGame(Game game);

    @Update
    void updateGame(Game game);

    @Delete
    void deleteGame(Game game);

    @Query("SELECT * FROM Game WHERE id=:id")
    Game getGame(long id);

    @Query("SELECT * FROM Game")
    List<Game> getGames();

    @Query("SELECT max(id) FROM Game")
    long getGreaterID();
}
