package com.kevintcoughlin.smodr.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.models.ItemDao;

import java.util.List;

@Database(entities = {Item.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "smodr-appdatabase";
    private static AppDatabase INSTANCE;

    public static void updateData(Context context, Item ...items) {
        getInstance(context).itemDao().update(items);
    }

    public abstract ItemDao itemDao();

    public static void insertData(final Context context, final List<Item> items) {
        getInstance(context).runInTransaction(() -> {
            // @todo: allocation?
            getInstance(context).itemDao().insertAll(items);
        });
    }

    public static Item[] getData(final Context context) {
        return getInstance(context).itemDao().getAll();
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                DATABASE_NAME
        ).allowMainThreadQueries().build(); // @todo: perf bottleneck
    }

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = create(context);
        }
        return INSTANCE;
    }
}