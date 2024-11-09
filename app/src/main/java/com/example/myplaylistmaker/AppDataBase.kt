package com.example.myplaylistmaker

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myplaylistmaker.db.TracksDao
import com.example.myplaylistmaker.db.TracksEntity

@Database(
    entities = [TracksEntity::class],
    version = 3
)

abstract class AppDataBase : RoomDatabase() {
    abstract fun tracksDao(): TracksDao

}