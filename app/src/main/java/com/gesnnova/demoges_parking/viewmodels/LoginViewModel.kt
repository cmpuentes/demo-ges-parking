package com.gesnnova.demoges_parking.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gesnnova.demoges_parking.datastore.SessionData
import com.gesnnova.demoges_parking.datastore.SessionManager
import com.gesnnova.demoges_parking.model.LoginRequest
import com.gesnnova.demoges_parking.model.UiMessage
import com.gesnnova.demoges_parking.network.ApiClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class LoginViewModel(application: Application) : AndroidViewModel(application){

    private val appContext = application.applicationContext

    private val _loginRequest = MutableStateFlow(LoginRequest())
    val loginRequest: StateFlow<LoginRequest> = _loginRequest

    //Métodos para actualizar los campos de la UI
    fun actualizarLogin(login: String){
        _loginRequest.value = _loginRequest.value.copy(login = login)
    }

    fun actualizarPassword(password: String) {
        _loginRequest.value = _loginRequest.value.copy(password = password)
    }

    fun actualizarTurno(turno: String) {
        _loginRequest.value = _loginRequest.value.copy(turno = turno)
    }

    fun actalizarFechaInicio(fecha_inicio: String) {
        _loginRequest.value = _loginRequest.value.copy(fecha_inicio = fecha_inicio)
    }

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Mensaje UI (Snackbar)
    private val _uiMessage = Channel<UiMessage>()
    val uiMessage = _uiMessage.receiveAsFlow()

    fun performLogin() {
        viewModelScope.launch {
            val request = _loginRequest.value

            if (request.login.isBlank() || request.password.isBlank()) {
                _uiMessage.send(UiMessage.Error("Por favor complete todos los campos"))
                return@launch
            }

            _isLoading.value = true

            try {
                val response = ApiClient.apiService.login(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!

                    // Guardar sesión localmente
                    body.numero_turno?.let {
                        SessionManager.saveSession(
                            context = appContext,
                            session = SessionData(
                                token = body.token ?: "",
                                nombreCompleto = body.nombreCompleto ?: "",
                                fechaInicio = body.fecha_inicio ?: "",
                                turno = body.turno ?: "",
                                numeroTurno = it
                            )
                        )
                    }

                    _uiMessage.send(UiMessage.Success("Bienvenido ${body.nombreCompleto}"))

                    // Aquí puedes navegar a HomeScreen
                } else {
                    val message = response.body()?.message ?: "Credenciales inválidas"
                    _uiMessage.send(UiMessage.Error(message))
                }
            } catch (e: Exception) {
                _uiMessage.send(UiMessage.Error("Error de conexión: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }
}