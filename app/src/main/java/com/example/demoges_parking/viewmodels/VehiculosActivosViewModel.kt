package com.example.demoges_parking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoges_parking.model.UiMessage
import com.example.demoges_parking.model.VehiculoActivoDTO
import com.example.demoges_parking.network.ApiClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VehiculosActivosViewModel: ViewModel() {

    private val _vehiculos = MutableStateFlow<List<VehiculoActivoDTO>>(emptyList())
    val vehiculos: StateFlow<List<VehiculoActivoDTO>> = _vehiculos

    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiMessage = Channel<UiMessage>()
    val uiMessage = _uiMessage.receiveAsFlow()

    // 👇 Nuevo: query de búsqueda
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // 👇 Nuevo: lista filtrada
    val vehiculosFiltrados: StateFlow<List<VehiculoActivoDTO>> =
        combine(_vehiculos, _query) { lista, texto ->
            val q = texto.trim().uppercase()
            when {
                q.isBlank() -> lista // sin filtro
                q.length >= 6 -> lista.filter { it.placa.equals(q, ignoreCase = true) }
                else -> lista.filter { it.placa.startsWith(q, ignoreCase = true) }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun actualizarQuery(texto: String) {
        _query.value = texto.uppercase().take(6) // siempre mayúscula y máximo 6
    }

    fun cargarVehiculosActivos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiClient.apiService.obtenerVehiculosActivos()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.data != null) {
                        _vehiculos.value = body.data.vehiculos.sortedBy { it.placa }
                        _total.value = body.data.total
                    } else {
                        _uiMessage.send(UiMessage.Error(body?.message ?: "Sin resultados"))
                    }
                } else {
                    _uiMessage.send(UiMessage.Error("Error en la solicitud"))
                }
            } catch (e: Exception) {
                _uiMessage.send(UiMessage.Error("Error de conexión"))
            } finally {
                _isLoading.value = false
            }
        }
    }
}