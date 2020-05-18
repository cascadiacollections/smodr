package com.kevintcoughlin.smodr

import android.app.Application
import com.kevintcoughlin.smodr.database.AppDatabase

/**
 * The Smodr [Application].
 *
 * @author kevincoughlin
 */
class SmodrApplication : Application() {
    fun getDatabase(): AppDatabase {
        return AppDatabase.getInstance(this);
    }
}