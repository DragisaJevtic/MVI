package com.example.supertestapplication.repository.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    fun getAll(): List<User>

    @Insert
    fun insertAll(vararg user: User)

    @Query("DELETE FROM user")
    fun nukeTable()
}