package ru.khozyainov.splashun.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.khozyainov.splashun.data.repository.OnboardingRepository
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    fun onboardingCompleted() = viewModelScope.launch {
        onboardingRepository.completeOnboarding()
    }
}