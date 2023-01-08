package ru.khozyainov.splashun.data.repository

import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.khozyainov.splashun.data.db.dao.ItemPhotoDao
import ru.khozyainov.splashun.data.db.entities.ItemPhotoEntity
import ru.khozyainov.splashun.data.network.PhotoRemoteMediator
import ru.khozyainov.splashun.data.network.api.PhotoAPI
import ru.khozyainov.splashun.data.network.models.AbbreviatedPhotoParentRemote
import ru.khozyainov.splashun.data.network.models.AbbreviatedPhotoRemote
import ru.khozyainov.splashun.models.ItemPhoto
import ru.khozyainov.splashun.models.Photo
import ru.khozyainov.splashun.models.PhotoDetails
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class PhotoRepository @Inject constructor(
    private val itemPhotoDao: ItemPhotoDao,
    private val photoAPI: PhotoAPI,
    private val remoteMediatorFactory: PhotoRemoteMediator.Factory
) {

    fun getPhotos(query: String): Flow<PagingData<ItemPhoto>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE

            ),
            remoteMediator = remoteMediatorFactory.create(query),
            pagingSourceFactory = { itemPhotoDao.getPagingSource(query = if (query == "") null else query) }
        ).flow
            .map { pagingData ->
                pagingData.map { itemPhotoEntity ->
                    itemPhotoEntity.toItemPhoto()
                }
            }

    fun getPhotoById(id: String): Flow<PhotoDetails> = flow {
            emit(photoAPI.getPhotoByID(id))
        }


    suspend fun setRefreshPhoto(abbreviatedPhotoRemote: AbbreviatedPhotoRemote) =
        withContext(Dispatchers.IO) {
            val itemPhotoEntity = itemPhotoDao.getItemPhotoByID(abbreviatedPhotoRemote.id)
                ?: throw Exception("Отсутствует элемент с id = ${abbreviatedPhotoRemote.id}")

            itemPhotoDao.updatePhoto(
                itemPhotoEntity.copy(
                    likes = abbreviatedPhotoRemote.likes,
                    like = abbreviatedPhotoRemote.like
                )
            )
        }

    fun setLike(
        photo: Photo,
        onCompleteCallback: (abbreviatedPhotoRemote: AbbreviatedPhotoRemote) -> Unit,
        onErrorCallback: (error: Throwable) -> Unit
    ) = photoAPI.likePhoto(photo.id).enqueue(object : Callback<AbbreviatedPhotoParentRemote> {
        override fun onResponse(
            call: Call<AbbreviatedPhotoParentRemote>,
            response: Response<AbbreviatedPhotoParentRemote>
        ) {
            if (response.isSuccessful)
                onCompleteCallback(response.body()?.photo ?: throw Exception("TODO")) //TODO
        }

        override fun onFailure(call: Call<AbbreviatedPhotoParentRemote>, t: Throwable) {
            onErrorCallback(t)
        }
    })

    fun deleteLike(
        photo: Photo,
        onCompleteCallback: (abbreviatedPhotoRemote: AbbreviatedPhotoRemote) -> Unit,
        onErrorCallback: (error: Throwable) -> Unit
    ) = photoAPI.deleteLikePhoto(photo.id).enqueue(object : Callback<AbbreviatedPhotoParentRemote> {
        override fun onResponse(
            call: Call<AbbreviatedPhotoParentRemote>,
            response: Response<AbbreviatedPhotoParentRemote>
        ) {
            if (response.isSuccessful)
                onCompleteCallback(response.body()?.photo ?: throw Exception("TODO")) //TODO
        }

        override fun onFailure(call: Call<AbbreviatedPhotoParentRemote>, t: Throwable) {
            onErrorCallback(t)
        }
    })

    private companion object {
        const val PAGE_SIZE = 10
    }
}