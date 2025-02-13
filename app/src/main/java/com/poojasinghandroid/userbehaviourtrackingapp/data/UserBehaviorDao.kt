package com.poojasinghandroid.userbehaviourtrackingapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserBehaviorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(behavior: UserBehavior)

    @Query("SELECT * FROM user_behavior ORDER BY id DESC LIMIT 10")
    suspend fun getLastSessions(): List<UserBehavior>
}