package com.example.sort.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensão para criar o DataStore
private val Context.dataStore by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {
    companion object {
        private val JWT_TOKEN = stringPreferencesKey("jwt_token")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: "Usuário"
    }
    // Salvar os dados
    suspend fun saveSession(token: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN] = token
            preferences[USER_NAME] = name
        }
    }

    // Ler o Token (Flow para observar mudanças)
    val jwtToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[JWT_TOKEN]
    }
}