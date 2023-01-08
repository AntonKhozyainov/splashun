package ru.khozyainov.splashun.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object State {

    private val mutableUIState = MutableStateFlow<UiState>(UiState.Loading(false))

    val uiState: StateFlow<UiState> = mutableUIState

    fun changeLoadState(isLoading: Boolean){
        mutableUIState.value = UiState.Loading(isLoading)
    }

    fun changeLoadWait(){
        mutableUIState.value = UiState.DownloadWait
    }

    fun showError(exceptionMessage: String){
        mutableUIState.value = UiState.Error(exceptionMessage)
    }

    fun loadIsSuccessful(){
        mutableUIState.value = UiState.Success
    }

    sealed class UiState {
        object DownloadWait : UiState()
        object Success : UiState()
        data class Error(val exceptionMessage: String) : UiState()
        data class Loading(val loading: Boolean) : UiState()
    }
}