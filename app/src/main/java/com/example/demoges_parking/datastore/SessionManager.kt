package com.example.demoges_parking.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Crear instancia de DataStore
val Context.dataStore by preferencesDataStore(name = "user_session")

object SessionManager {

    // Claves
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val NOMBRE_KEY = stringPreferencesKey("nombreCompleto")
    private val FECHA_KEY = stringPreferencesKey("fechaInicio")
    private val TURNO_KEY = stringPreferencesKey("turno")
    private val NUM_TURNO_KEY = intPreferencesKey("numeroTurno")
    private val TURNO_FINALIZADO_KEY = booleanPreferencesKey("turno_finalizado")


    // Guardar sesión completa
    suspend fun saveSession(context: Context, session: SessionData) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = session.token
            prefs[NOMBRE_KEY] = session.nombreCompleto
            prefs[FECHA_KEY] = session.fechaInicio
            prefs[TURNO_KEY] = session.turno
            prefs[NUM_TURNO_KEY] = session.numeroTurno
            prefs[TURNO_FINALIZADO_KEY] = session.turnoFinalizado
        }
    }

    // Leer sesión como Flow
    fun getSessionData(context: Context): Flow<SessionData> {
        return context.dataStore.data.map { prefs ->
            SessionData(
                token = prefs[TOKEN_KEY] ?: "",
                nombreCompleto = prefs[NOMBRE_KEY] ?: "",
                fechaInicio = prefs[FECHA_KEY] ?: "",
                turno = prefs[TURNO_KEY] ?: "",
                numeroTurno = prefs[NUM_TURNO_KEY] ?: 0,
                turnoFinalizado = prefs[TURNO_FINALIZADO_KEY] ?: false
            )
        }
    }

    // 🔹 Guardar estado de turno finalizado
    suspend fun setTurnoFinalizado(context: Context, finalizado: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[TURNO_FINALIZADO_KEY] = finalizado
        }
    }

    // 🔹 Leer estado de turno finalizado
    fun getTurnoFinalizado(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[TURNO_FINALIZADO_KEY] ?: false
        }
    }

    // Borrar sesión (al cerrar sesión)
    suspend fun clearSession(context: Context) {
        context.dataStore.edit { it.clear() }
    }
}