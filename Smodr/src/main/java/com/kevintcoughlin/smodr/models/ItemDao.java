package com.kevintcoughlin.smodr.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM item")
    Item[] getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Item> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Item ...items);

    @Delete
    void delete(Item ...items);
}
