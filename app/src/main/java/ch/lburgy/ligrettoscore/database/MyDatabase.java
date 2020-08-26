package ch.lburgy.ligrettoscore.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Game.class, Player.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "LigrettoScoreDatabase.db";
    private static volatile MyDatabase instance;

    public static synchronized MyDatabase getInstance(Context context) {
        if (instance == null)
            instance = create(context);
        return instance;
    }

    private static MyDatabase create(final Context context) {
        return Room.databaseBuilder(context, MyDatabase.class, DATABASE_NAME).build();
    }

    public abstract GameDao getGameDao();

    public abstract PlayerDao getPlayerDao();
}
