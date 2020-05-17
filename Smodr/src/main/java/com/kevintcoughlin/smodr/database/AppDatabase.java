package com.kevintcoughlin.smodr.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.kevintcoughlin.smodr.models.PlaybackRecord;
import com.kevintcoughlin.smodr.models.PlaybackRecordDao;

@Database(entities = {PlaybackRecord.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlaybackRecordDao playbackRecordDao();

    public static void insertData(final AppDatabase database, final PlaybackRecord ...records) {
        database.runInTransaction(() -> {
            database.playbackRecordDao().insertAll(records);
        });
    }
}