package com.sesolutions.sesdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ThemeDao {

    @Insert
    fun savetheme(theme : Theme)

    @Query("SELECT * FROM Theme")
    fun getTheme() : List<Theme>
}