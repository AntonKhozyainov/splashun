package ru.khozyainov.splashun.data.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.khozyainov.splashun.data.db.dao.ItemPhotoDao
import ru.khozyainov.splashun.data.db.entities.ItemPhotoEntity
import ru.khozyainov.splashun.data.network.api.PhotoAPI
import kotlin.random.Random

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator @AssistedInject constructor(
    private val itemPhotoDao: ItemPhotoDao,
    private val photoAPI: PhotoAPI,
    @Assisted private val query: String
) : RemoteMediator<Int, ItemPhotoEntity>() {

    private var pageIndex = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ItemPhotoEntity>
    ): MediatorResult {

        pageIndex =
            getPageIndex(loadType) ?: return MediatorResult.Success(endOfPaginationReached = true)

        return try {
            //todo
//            val listItemPhoto =
//                if (query.isEmpty()) photoAPI.getPhotos(pageIndex, state.config.pageSize)
//                else photoAPI.searchPhotos(query, pageIndex, state.config.pageSize).listItemPhotoEntity
//                    .map { it.copy(search = query) }

            val listItemPhoto = getTestList()

            if (loadType == LoadType.REFRESH) {
                itemPhotoDao.refresh(query, listItemPhoto)
            } else {
                itemPhotoDao.save(listItemPhoto)
            }
            MediatorResult.Success(
                endOfPaginationReached = listItemPhoto.size < state.config.pageSize
            )

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }

    }

    private fun getTestList(): List<ItemPhotoEntity> {
        val list = mutableListOf<ItemPhotoEntity>()
        repeat(1) {
            list.add(
                ItemPhotoEntity(
                    id = "id_${Random.nextInt()}",
                    //placeholder = "LEHLh[WB2yk8pyoJadR*.7kCMdnj",
                    image = "this.image",
                    like = Random.nextBoolean(),
                    likes = Random.nextInt(0, 10000000).toLong(),
                    author = "@author",
                    authorFullName = "Ivan Petrov",
                    authorImage = "this.authorImage",
                    authorAbout = "123",
                    search = null,
                    width = 300,
                    height = 450,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        return list
    }

    private fun getPageIndex(loadType: LoadType): Int? {
        pageIndex = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return null
            LoadType.APPEND -> ++pageIndex
        }
        return pageIndex
    }

    @AssistedFactory
    interface Factory {
        fun create(query: String?): PhotoRemoteMediator
    }
}