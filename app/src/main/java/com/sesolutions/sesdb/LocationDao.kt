package com.sesolutions.sesdb

import androidx.room.*

@Dao
interface LocationDao {

    @Insert
    fun insert(location: Location)

    @Update
    fun update(location: Location)

    @Delete
    fun delete(location: Location)

    @Query("SELECT * FROM location")
    fun getAll() : List<Location>

    @Query("delete from location")
    fun deleteAll() : Unit

}