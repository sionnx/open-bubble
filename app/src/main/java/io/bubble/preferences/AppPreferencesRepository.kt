package io.bubble.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.bubble.core.scan.EbadgeMacParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val APP_PREFERENCES_NAME = "bubble_app_preferences"

private val Context.bubbleAppPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_PREFERENCES_NAME,
)

private object AppPreferenceKeys {
    val LAST_CONNECTED_MAC = stringPreferencesKey("last_connected_mac")
    val LAST_CONNECTED_NAME = stringPreferencesKey("last_connected_name")
}

/**
 * 应用级偏好（上次连接设备等），基于 Jetpack DataStore Preferences。
 */
class AppPreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val lastConnectedMac: Flow<String?> = dataStore.data.map { prefs ->
        prefs[AppPreferenceKeys.LAST_CONNECTED_MAC]
    }

    val lastConnectedName: Flow<String?> = dataStore.data.map { prefs ->
        prefs[AppPreferenceKeys.LAST_CONNECTED_NAME]
    }

    val lastConnectedLabel: Flow<String?> = dataStore.data.map { prefs ->
        val mac = prefs[AppPreferenceKeys.LAST_CONNECTED_MAC] ?: return@map null
        val name = prefs[AppPreferenceKeys.LAST_CONNECTED_NAME]
        if (name.isNullOrBlank()) mac else "$name ($mac)"
    }

    suspend fun setLastConnected(mac: String, displayName: String? = null) {
        val normalizedMac = EbadgeMacParser.normalizeMac(mac)
        dataStore.edit { prefs ->
            prefs[AppPreferenceKeys.LAST_CONNECTED_MAC] = normalizedMac
            if (displayName.isNullOrBlank()) {
                prefs.remove(AppPreferenceKeys.LAST_CONNECTED_NAME)
            } else {
                prefs[AppPreferenceKeys.LAST_CONNECTED_NAME] = displayName
            }
        }
    }

    suspend fun clearLastConnected() {
        dataStore.edit { prefs ->
            prefs.remove(AppPreferenceKeys.LAST_CONNECTED_MAC)
            prefs.remove(AppPreferenceKeys.LAST_CONNECTED_NAME)
        }
    }

    companion object {
        @Volatile
        private var instance: AppPreferencesRepository? = null

        fun getInstance(context: Context): AppPreferencesRepository {
            return instance ?: synchronized(this) {
                instance ?: AppPreferencesRepository(
                    context.applicationContext.bubbleAppPreferencesDataStore,
                ).also { instance = it }
            }
        }
    }
}
