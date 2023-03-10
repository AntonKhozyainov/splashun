package ru.khozyainov.splashun.presentation.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import ru.khozyainov.splashun.data.repository.OnboardingRepository
import ru.khozyainov.splashun.data.repository.AuthRepository
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LauncherViewModel @Inject constructor(
    onboardingRepository: OnboardingRepository,
    authRepository: AuthRepository
) : ViewModel() {

    //Получаем признак просмотрел ли онбординг
    private val onboardingCompleted = onboardingRepository.onboardingCompleted()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    //Проверяем есть ли токен, если есть то логиниться не нужно
    private val loginCompleted = authRepository.getTokenFlow()
        .map { it != null }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val loginAndOnboardingCompleted = combine(
        onboardingCompleted,
        loginCompleted
    ){ onboarding, login ->
        onboarding to login
    }.mapLatest { (onboardingCompleted, loginCompleted) ->
        when{
            (onboardingCompleted && loginCompleted) -> LaunchNavigatonAction.NavigateToMainActivityAction
            (onboardingCompleted && !loginCompleted) -> LaunchNavigatonAction.NavigateToLoginActivityAction
            else -> LaunchNavigatonAction.NavigateToOnboardingAction
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    sealed class LaunchNavigatonAction {
        object NavigateToOnboardingAction : LaunchNavigatonAction()
        object NavigateToLoginActivityAction : LaunchNavigatonAction()
        object NavigateToMainActivityAction : LaunchNavigatonAction()
    }
}