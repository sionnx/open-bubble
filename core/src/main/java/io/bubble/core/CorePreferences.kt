package io.bubble.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private const val CORE_PREFERENCES_NAME = "bubble_preferences"
private const val CONSULT_KEY_PREFIX = "consult_key_"

val Context.bubbleCorePreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = CORE_PREFERENCES_NAME,
)

object CorePreferenceKeys {
    fun consultKey(mac: String) = stringPreferencesKey("$CONSULT_KEY_PREFIX$mac")
}
