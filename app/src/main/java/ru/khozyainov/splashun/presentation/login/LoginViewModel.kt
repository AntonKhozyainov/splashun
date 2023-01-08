package ru.khozyainov.splashun.presentation.login

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import net.openid.appauth.TokenRequest
import ru.khozyainov.splashun.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val loginIsDone = authRepository.getTokenFlow()
        .map { it != null }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    //Неблокирующий примитив для связи между отправителем (через SendChannel) и получателем (через ReceiveChannel)
    private val openAuthPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    val openAuthPageFlow: Flow<Intent>
        //receiveAsFlow() - получает канал как горячий поток
        get() = openAuthPageEventChannel.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d("LoginViewModelErr", throwable.message.toString())
    }

    //Открываем страницу авторизации
    fun openLoginPage() = openAuthPageEventChannel.trySendBlocking(
        authRepository.getLoginPageIntent()
    )

    fun onAuthFailed(exception: Throwable) = viewModelScope.launch(errorHandler) {
        throw exception
    }

    fun getTokenByRequest(tokenRequest: TokenRequest) = viewModelScope.launch(errorHandler) {
        authRepository.getTokenByRequest(tokenRequest)
    }
}