package com.cascadiacollections.smodr

import android.app.Application
import com.cascadiacollections.smodr.database.AppDatabase

/**
 * The Smodr [Application].
 *
 * @author kevincoughlin
 */
class SmodrApplication : Application() {
    fun getDatabase(): AppDatabase {
        return AppDatabase.getInstance(this)
    }
}