package com.example.astrotrack.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class NotificationPreferences(private val context: Context) {

    companion object {
        private val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    }

    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[NOTIFICATION_ENABLED] ?: true }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATION_ENABLED] = enabled
        }
    }
}
