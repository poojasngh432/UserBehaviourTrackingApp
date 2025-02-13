package com.poojasinghandroid.userbehaviourtrackingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_behavior")
data class UserBehavior(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val inputText: String
)