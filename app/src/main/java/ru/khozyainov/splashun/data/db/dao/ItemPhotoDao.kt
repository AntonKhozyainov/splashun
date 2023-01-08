package ru.khozyainov.splashun.data.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.CREATED_AT
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.ID
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.SEARCH
import ru.khozyainov.splashun.data.db.entities.ItemPhotoEntity
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.TABLE_NAME

@Dao
interface ItemPhotoDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE :query IS NULL OR $SEARCH = :query ORDER BY $CREATED_AT")
    fun getPagingSource(
        query: String?
    ): PagingSource<Int, ItemPhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(photos: List<ItemPhotoEntity>)

    @Update
    suspend fun updatePhoto(photo: ItemPhotoEntity)

    @Query("SELECT * FROM $TABLE_NAME WHERE $ID = :id")
    suspend fun getItemPhotoByID(id: String): ItemPhotoEntity?

    @Query("DELETE FROM $TABLE_NAME WHERE :query IS NULL OR $SEARCH = :query")
    suspend fun clear(query: String?)

    @Transaction
    suspend fun refresh(query: String?, photos: List<ItemPhotoEntity>) {
        clear(query)
        save(photos)
    }

    suspend fun save(photo: ItemPhotoEntity) {
        save(listOf(photo))
    }
}