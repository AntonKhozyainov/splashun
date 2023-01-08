package ru.khozyainov.splashun.data.network.api

import retrofit2.Call
import retrofit2.http.*
import ru.khozyainov.splashun.data.db.entities.ItemPhotoEntity
import ru.khozyainov.splashun.data.db.entities.SearchPhotoItems
import ru.khozyainov.splashun.data.network.models.AbbreviatedPhotoParentRemote
import ru.khozyainov.splashun.data.network.models.AbbreviatedPhotoRemote
import ru.khozyainov.splashun.models.PhotoDetails


interface PhotoAPI {

    @GET("/photos")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") limit: Int
    ): List<ItemPhotoEntity>

    @GET("/photos/{id}")
    suspend fun getPhotoByID(
        @Path("id") id: String
    ): PhotoDetails

    @GET("/search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") limit: Int
    ): SearchPhotoItems

    @POST("/photos/{id}/like")
    fun likePhoto(
        @Path("id") id: String
    ): Call<AbbreviatedPhotoParentRemote>

    @DELETE("/photos/{id}/like")
    fun deleteLikePhoto(
        @Path("id") id: String
    ): Call<AbbreviatedPhotoParentRemote>



//    @POST("/photos/{id}/like")
//    suspend fun likePhoto(
//        @Path("id") id: String
//    ): AbbreviatedPhotoParentRemote
//
//    @DELETE("/photos/{id}/like")
//    suspend fun deleteLikePhoto(
//        @Path("id") id: String
//    ): AbbreviatedPhotoParentRemote

}