package ru.khozyainov.splashun.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.khozyainov.splashun.data.db.SplashunDataBase.Companion.DB_VERSION
import ru.khozyainov.splashun.data.db.dao.ItemPhotoDao
import ru.khozyainov.splashun.data.db.entities.ItemPhotoEntity

@Database(
    entities =[
        ItemPhotoEntity::class
    ],
    version = DB_VERSION
)
abstract class SplashunDataBase: RoomDatabase() {

    abstract fun itemPhotoDao(): ItemPhotoDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "splashun-database"
    }
}