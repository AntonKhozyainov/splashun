package ru.khozyainov.splashun.data.repository

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.openid.appauth.*
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class AuthRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val authService: AuthorizationService,
    private val authRequest: AuthorizationRequest.Builder
)  {

    //Получаем сохраненный токен авторизации
    fun getTokenFlow(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }

    //Сохраняем полученный токен
    private suspend fun setToken(token: String)= dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }

    //ЗАПРОС НА Авторизацию кладем в Intent customTabsIntent
    fun getLoginPageIntent(): Intent = authService.getAuthorizationRequestIntent(
        authRequest
            //Сервер unsplash не поддерживает pkce по этому установим setCodeVerifier(null)
            .setCodeVerifier(null)
            .build(),
        //Создаем Intent кастомную таблицу
        CustomTabsIntent.Builder().build()
    )

    suspend fun getTokenByRequest(tokenRequest: TokenRequest): String {
        val token = performTokenRequestSuspend(tokenRequest)
        setToken(token)
        return token
    }

    private suspend fun performTokenRequestSuspend(
        tokenRequest: TokenRequest
    ): String = suspendCoroutine { continuation ->
        authService.performTokenRequest(
            tokenRequest,
            //Если сервис не требует client_secret, то можно использовать ClientSecretBasic("")
            ClientSecretPost(CLIENT_SECRET)
        ) { response, exception ->
            when {
                response != null -> {
                    continuation.resumeWith(Result.success(response.accessToken.orEmpty()))
                }
                //получение токенов произошло неуспешно, показываем ошибку
                exception != null -> {
                    continuation.resumeWith(Result.failure(exception))
                }
                else -> error("unreachable")
            }
        }
    }

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
        private const val CLIENT_SECRET = "GBXwQijIUl9T5I96-5HHZxQmuBCi949O6NbshiT3Fjg"
    }
}