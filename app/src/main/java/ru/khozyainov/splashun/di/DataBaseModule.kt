package ru.khozyainov.splashun.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.khozyainov.splashun.data.db.SplashunDataBase
import ru.khozyainov.splashun.data.db.dao.ItemPhotoDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Singleton
    fun providesDatabase(context: Application): SplashunDataBase = Room.databaseBuilder(
        context,
        SplashunDataBase::class.java,
        SplashunDataBase.DB_NAME
    ).build()

    @Provides
    fun providesItemPhotoDAO(db: SplashunDataBase): ItemPhotoDao = db.itemPhotoDao()
}