package ru.khozyainov.splashun.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.khozyainov.splashun.data.network.models.AbbreviatedPhotoRemote
import ru.khozyainov.splashun.data.repository.PhotoRepository
import ru.khozyainov.splashun.models.ItemPhoto
import ru.khozyainov.splashun.models.PhotoDetails
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    var itemPhotoFlow: Flow<PagingData<ItemPhoto>> = MutableStateFlow(PagingData.empty())

    //Для отмены корутины, сохраним корутину в job
    private var currentJob: Job? = null

    private val mutableUIState = MutableStateFlow<UiState>(UiState.Loading(false))
    val uiState: StateFlow<UiState> = mutableUIState

//    private val photoFlowChannel = Channel<PhotoDetails>(Channel.BUFFERED)
//    val photoFlow: Flow<PhotoDetails>
//        get() = photoFlowChannel.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        exceptionHandler(throwable)
    }

    private fun exceptionHandler(throwable: Throwable) {
        //Log.d("HomeViewModelLog", throwable.message.toString())
        mutableUIState.value = UiState.Loading(false)
        mutableUIState.value = UiState.Error(throwable.message.toString())
    }

    fun bind(queryFlow: Flow<String>) {
        itemPhotoFlow = queryFlow
            //debounce - отфильтровывает значения которые пришли в короткий промежуток времени
            //если 1000 милисекунд не приходили эмиты, то последний эмит пройдет дальше по цепочке
            .debounce(1000)
            .flatMapLatest { query ->
                photoRepository.getPhotos(query)
            }
            .catch { e ->
                exceptionHandler(e)
            }
            .flowOn(Dispatchers.IO)
            .cachedIn(viewModelScope)
    }

    fun getPhoto(photoId: String) {
        mutableUIState.value = UiState.Loading(true)
        currentJob = photoRepository.getPhotoById(photoId)
            .onEach { photo ->
                mutableUIState.value = UiState.Loading(false)
                mutableUIState.value = UiState.Success(photo)
            }
            .flowOn(Dispatchers.IO)
            .catch { e ->
                exceptionHandler(e)
            }
            .launchIn(viewModelScope)
    }

    fun setLike(photo: ItemPhoto, like: Boolean) =
        if (like) {
            photoRepository.setLike(
                photo,
                {
                    setRefreshPhoto(it)
                }, { error ->
                    exceptionHandler(error)
                }
            )
        } else {
            photoRepository.deleteLike(
                photo,
                {
                    setRefreshPhoto(it)
                }, { error ->
                    exceptionHandler(error)
                }
            )
        }


    fun setLikeForDetailPhoto() {
        if (uiState.value !is UiState.Success) return
        val photo = (uiState.value as UiState.Success).photo
        try {
            if (!photo.like) {
                photoRepository.setLike(
                    photo,
                    { abbreviatedPhoto ->
                        setRefreshPhoto(abbreviatedPhoto)
                        mutableUIState.value = UiState.Success(
                            photo.copy(
                                likes = abbreviatedPhoto.likes,
                                like = abbreviatedPhoto.like
                            )
                        )
                    }, { error ->
                        exceptionHandler(error)
                    }
                )
            } else {
                photoRepository.deleteLike(
                    photo,
                    { abbreviatedPhoto ->
                        setRefreshPhoto(abbreviatedPhoto)
                        mutableUIState.value = UiState.Success(
                            photo.copy(
                                likes = abbreviatedPhoto.likes,
                                like = abbreviatedPhoto.like
                            )
                        )
                    }, { error ->
                        exceptionHandler(error)
                    }
                )
            }
        } catch (e: Exception) {
            exceptionHandler(e)
        }

    }

    private fun setRefreshPhoto(abbreviatedPhotoRemote: AbbreviatedPhotoRemote) =
        viewModelScope.launch(errorHandler) {
            photoRepository.setRefreshPhoto(abbreviatedPhotoRemote)
        }

    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
    }

    sealed class UiState {
        data class Success(val photo: PhotoDetails) : UiState()
        data class Error(val exceptionMessage: String) : UiState()
        data class Loading(val loading: Boolean) : UiState()
    }
}