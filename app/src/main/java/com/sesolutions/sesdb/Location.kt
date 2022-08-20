package com.sesolutions.sesdb

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


//Entity annotation to specify the table's name
@Entity(tableName = "location")
//Parcelable annotation to make parcelable object
@Parcelize
data class Location(
    //PrimaryKey annotation to declare primary key with auto increment value
    //ColumnInfo annotation to specify the column's name
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "longitude") var longitude: String = "",
    @ColumnInfo(name = "lattitude") var lattitude: String = ""
) : Parcelable
