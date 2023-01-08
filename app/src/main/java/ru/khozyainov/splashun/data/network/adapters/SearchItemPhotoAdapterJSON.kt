package ru.khozyainov.splashun.data.network.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import ru.khozyainov.splashun.data.db.entities.SearchPhotoItems
import ru.khozyainov.splashun.data.network.models.SearchItemPhotoRemote

class SearchItemPhotoAdapterJSON(
  private val itemPhotoListAdapterJSON: ItemPhotoListAdapterJSON
) {
    @FromJson
    fun fromJSON(searchItemPhotoRemote: SearchItemPhotoRemote): SearchPhotoItems =
        SearchPhotoItems(itemPhotoListAdapterJSON.fromJSON(searchItemPhotoRemote.itemPhotoRemoteList))

    @ToJson
    fun toJSON(searchPhotoItems: SearchPhotoItems): SearchItemPhotoRemote =
        SearchItemPhotoRemote(listOf())
}