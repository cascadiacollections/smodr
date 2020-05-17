package com.kevintcoughlin.smodr.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
    public class PlaybackRecord {

    // @todo
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "guid")
    public String guid;

    @ColumnInfo(name = "title")
    public String title;

    public static PlaybackRecord fromItem(Item item) {
        final PlaybackRecord playback = new PlaybackRecord();

        playback.guid = item.getGuid();
        playback.title = item.getTitle();

        return playback;
    }
}
