package ru.khozyainov.splashun.data.network.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import ru.khozyainov.splashun.data.db.entities.ItemPhotoEntity
import ru.khozyainov.splashun.data.network.models.ItemPhotoRemote

class ItemPhotoListAdapterJSON {

    @FromJson
    fun fromJSON(listPhotoRemote: List<ItemPhotoRemote>): List<ItemPhotoEntity> =
        listPhotoRemote.map { itemPhotoRemote ->
            ItemPhotoEntity(
                id = itemPhotoRemote.id,
                image = itemPhotoRemote.images.imageRaw,
                like = itemPhotoRemote.likeByUser,
                likes = itemPhotoRemote.likes,
                author = itemPhotoRemote.author.name,
                authorFullName = itemPhotoRemote.author.fullName,
                authorImage = itemPhotoRemote.author.images.image,
                authorAbout = itemPhotoRemote.author.about ?: "",
                search = null,
                createdAt = System.currentTimeMillis(),
                width = itemPhotoRemote.width,
                height = itemPhotoRemote.height,
            )
        }


    @ToJson
    fun toJSON(listPhotoEntity: List<ItemPhotoEntity>): List<ItemPhotoRemote> =
        listOf()
}