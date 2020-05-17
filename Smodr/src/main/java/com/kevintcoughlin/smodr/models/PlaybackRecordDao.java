package com.kevintcoughlin.smodr.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaybackRecordDao {
    @Query("SELECT * FROM playbackrecord")
    List<PlaybackRecord> getAll();

    @Query("SELECT * FROM playbackrecord WHERE uid IN (:ids)")
    List<PlaybackRecord> loadAllByIds(int[] ids);

    @Insert
    void insertAll(PlaybackRecord ...records);

//    @Delete
//    void delete(PlaybackRecord record);
}
