package com.kevintcoughlin.smodr.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.kevintcoughlin.smodr.models.Item
import com.kevintcoughlin.smodr.models.ItemDao

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        private const val DATABASE_NAME = "smodr-db"

        // @todo: reported leak
        private var INSTANCE: AppDatabase? = null

        fun updateData(context: Context, vararg items: Item?) {
            getInstance(context).itemDao().update(*items)
        }

        fun insertData(context: Context, items: List<Item?>?) {
            getInstance(context).runInTransaction {
                getInstance(
                    context
                ).itemDao().insertAll(items)
            }
        }

        fun getData(context: Context): Array<Item> {
            return getInstance(context).itemDao().all
        }

        private fun create(context: Context): AppDatabase {
            return databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            ).allowMainThreadQueries().build() // @todo: render perf
        }

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = create(context)
            }
            return INSTANCE!!
        }
    }
}