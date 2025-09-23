package com.example.demoges_parking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoges_parking.model.ConsultaVehiculoResponse
import com.example.demoges_parking.model.UiMessage
import com.example.demoges_parking.network.ApiClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ConsultaVehiculoViewModel: ViewModel() {

    private val _placa = MutableStateFlow("")
    val placa: StateFlow<String> = _placa

    private val _consultaResponse = MutableStateFlow<ConsultaVehiculoResponse?>(null)
    val consultaResponse: StateFlow<ConsultaVehiculoResponse?> = _consultaResponse

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiMessage = Channel<UiMessage>()
    val uiMessage = _uiMessage.receiveAsFlow()

    fun actualizarPlaca(placa: String){
        _placa.value = placa
    }

    fun consultarVehiculo() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = ApiClient.apiService.consultarVehiculo(_placa.value.trim())

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        _consultaResponse.value = body.data!!
                    } else {
                        _uiMessage.send(UiMessage.Error(body?.message ?: "Vehículo no encontrado"))
                        _consultaResponse.value = null // Limpia datos anteriores
                    }
                } else {
                    _uiMessage.send(UiMessage.Error("No se pudo consultar el vehículo"))
                }

            } catch (e: Exception) {
                _uiMessage.send(UiMessage.Error("Error de conexión"))
            } finally {
                _isLoading.value = false
            }
        }
    }
}