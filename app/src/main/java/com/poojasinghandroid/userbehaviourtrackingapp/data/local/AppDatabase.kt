package com.poojasinghandroid.userbehaviourtrackingapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.poojasinghandroid.userbehaviourtrackingapp.data.UserBehavior
import com.poojasinghandroid.userbehaviourtrackingapp.data.UserBehaviorDao

@Database(entities = [UserBehavior::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userBehaviorDao(): UserBehaviorDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "user_behavior_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}