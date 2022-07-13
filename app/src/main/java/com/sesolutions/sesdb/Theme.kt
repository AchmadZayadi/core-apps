package com.sesolutions.sesdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sesolutions.utils.Constant

@Entity
data class Theme (
    @PrimaryKey
    val uid: Int,
    @ColumnInfo(name = Constant.KEY_THEME_STYLE) val colorTheme: String?
)