package com.example.demoges_parking.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoges_parking.datastore.SessionData
import com.example.demoges_parking.datastore.SessionManager
import com.example.demoges_parking.model.LogoutRequest
import com.example.demoges_parking.network.ApiClient.apiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SessionViewModel(application: Application): AndroidViewModel(application) {

    private val _sessionData = MutableStateFlow(SessionData())
    val sessionData: StateFlow<SessionData> = _sessionData

    private val _turnoFinalizado = MutableStateFlow(false)
    val turnoFinalizado: StateFlow<Boolean> = _turnoFinalizado

    init {
        // Cargar datos de sesión normales
        viewModelScope.launch {
            SessionManager.getSessionData(application.applicationContext).collectLatest {
                _sessionData.value = it
            }
        }

        // Cargar estado de turno finalizado
        viewModelScope.launch {
            SessionManager.getTurnoFinalizado(application.applicationContext).collectLatest {
                _turnoFinalizado.value = it
            }
        }
    }

    // 🔹 Marcar turno como finalizado
    fun marcarTurnoFinalizado() {
        viewModelScope.launch {
            SessionManager.setTurnoFinalizado(getApplication(), true)
        }
    }

    // 🔹 Reiniciar turno (cuando se inicia turno nuevo)
    fun reiniciarTurnoFinalizado() {
        viewModelScope.launch {
            SessionManager.setTurnoFinalizado(getApplication(), false)
        }
    }

    // Función para cerrar sesión
    fun cerrarSesion() {
        viewModelScope.launch {
            SessionManager.clearSession(getApplication())
            _sessionData.value = SessionData() // Limpia también en memoria
        }
    }

    fun cerrarSesionServidor(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val token = sessionData.value.token
            try {
                val response = apiService.cerrarSesion(LogoutRequest(token))
                if (response.isSuccessful && response.body()?.success == true) {
                    cerrarSesion() // Esta ya borra del DataStore y memoria
                    onResult(true, response.body()?.message ?: "Sesión cerrada")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    onResult(false, errorMsg)
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Error de red")
            }
        }
    }

    fun actualizarSesionDesdeCheck(
        context: Context,
        token: String,
        nombreCompleto: String,
        fechaInicio: String,
        turno: String,
        numeroTurno: Int
    ) {
        viewModelScope.launch {
            SessionManager.saveSession(
                context,
                SessionData(
                    token = token,
                    nombreCompleto = nombreCompleto,
                    fechaInicio = fechaInicio,
                    turno = turno,
                    numeroTurno = numeroTurno,
                    turnoFinalizado = false
                )
            )
        }
    }


}