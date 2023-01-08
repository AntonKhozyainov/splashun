package ru.khozyainov.splashun.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OnboardingRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    //Проверяем просмотрен ли онбординг
    fun onboardingCompleted(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }

    //Сохраняем в dataStore флаг онбординг просмотрен
    suspend fun completeOnboarding() {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = true
        }
    }

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("ONBOARDING_COMPLETED")
    }
}