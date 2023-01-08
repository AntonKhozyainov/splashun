package ru.khozyainov.splashun.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.AUTHOR
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.AUTHOR_ABOUT
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.AUTHOR_AVATAR
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.AUTHOR_FULL_NAME
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.ID
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.LIKE
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.LIKES
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.PHOTO_RAW
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.SEARCH
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.CREATED_AT
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.HEIGHT
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.Columns.WIDTH
import ru.khozyainov.splashun.data.db.entities.ItemPhotoContract.TABLE_NAME
import ru.khozyainov.splashun.models.Author
import ru.khozyainov.splashun.models.ItemPhoto

@Entity(
    tableName = TABLE_NAME,
    indices = [
        androidx.room.Index(CREATED_AT)
    ]
)
data class ItemPhotoEntity(
    @PrimaryKey
    @ColumnInfo(name = ID) val id: String,
    @ColumnInfo(name = PHOTO_RAW) val image: String,
    @ColumnInfo(name = LIKE) val like: Boolean,
    @ColumnInfo(name = LIKES) val likes: Long,
    @ColumnInfo(name = AUTHOR) val author: String,
    @ColumnInfo(name = AUTHOR_ABOUT) val authorAbout: String,
    @ColumnInfo(name = AUTHOR_FULL_NAME) val authorFullName: String,
    @ColumnInfo(name = AUTHOR_AVATAR) val authorImage: String,
    @ColumnInfo(name = SEARCH) val search: String?,
    @ColumnInfo(name = CREATED_AT) val createdAt: Long,
    @ColumnInfo(name = HEIGHT) val height: Int,
    @ColumnInfo(name = WIDTH) val width: Int
) {
    fun toItemPhoto(): ItemPhoto {

//        val height = (this.height.toDouble() / this.width.toDouble() * IMAGE_WIDTH).toInt()

        return ItemPhoto(
            id = this.id,
            image = this.image,
            like = this.like,
            likes = this.likes,
            author = Author(
                name = "@${this.author}",
                fullName = this.authorFullName,
                image = this.authorImage,
                about = this.authorAbout
            ),
            width = width,
            height = height
        )
    }

//    private companion object {
//        const val IMAGE_WIDTH = 265
//    }
}
