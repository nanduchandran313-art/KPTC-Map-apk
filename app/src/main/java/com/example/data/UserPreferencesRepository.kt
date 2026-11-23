package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.localization.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kptc_kuwait_prefs")

class UserPreferencesRepository(private val context: Context) {

    private val favoriteRoutesKey = stringSetPreferencesKey("favorite_routes")
    private val livePreviewOutsideHoursKey = booleanPreferencesKey("live_preview_outside_hours")
    private val appLanguageKey = stringPreferencesKey("app_language")

    val favoriteRoutesFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[favoriteRoutesKey] ?: emptySet()
    }

    val livePreviewOutsideHoursFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[livePreviewOutsideHoursKey] ?: true
    }

    val appLanguageFlow: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        when (prefs[appLanguageKey]) {
            AppLanguage.ARABIC.code -> AppLanguage.ARABIC
            else -> AppLanguage.ENGLISH
        }
    }

    suspend fun toggleFavoriteRoute(routeNumber: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[favoriteRoutesKey] ?: emptySet()
            prefs[favoriteRoutesKey] = if (routeNumber in current) {
                current - routeNumber
            } else {
                current + routeNumber
            }
        }
    }

    suspend fun setLivePreviewOutsideHours(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[livePreviewOutsideHoursKey] = enabled
        }
    }

    suspend fun setAppLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[appLanguageKey] = language.code
        }
    }
}
