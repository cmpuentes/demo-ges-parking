package com.gesnnova.demoges_parking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gesnnova.demoges_parking.model.CierreData
import com.gesnnova.demoges_parking.model.ResumenTurnoResponse
import com.gesnnova.demoges_parking.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResumenTurnoViewModel(private val apiService: ApiService) : ViewModel() {

    private val _resumen = MutableStateFlow<ResumenTurnoResponse?>(null)
    val resumen: StateFlow<ResumenTurnoResponse?> = _resumen

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _cierreParaImprimir = MutableStateFlow<CierreData?>(null)
    val cierreParaImprimir: StateFlow<CierreData?> = _cierreParaImprimir

    fun obtenerResumen(turno: Int) {
        viewModelScope.launch {
            _loading.value = true
            _mensaje.value = null
            try {
                val response = apiService.obtenerResumenTurno(turno)
                if (response.isSuccessful) {
                    _resumen.value = response.body()
                } else {
                    _mensaje.value = "Error al obtener resumen: ${response.code()}"
                }
            } catch (e: Exception) {
                _mensaje.value = "No se pudo conectar con el servidor"
            } finally {
                _loading.value = false
            }
        }
    }

    // 🔹 Nuevo: traer cierre completo para impresión
    fun obtenerCierreParaImprimir(turno: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.obtenerUltimoCierrePorTurno(turno)
                if (response.isSuccessful && response.body() != null) {
                    _cierreParaImprimir.value = response.body()
                } else {
                    _mensaje.value = "No se encontró cierre para este turno"
                }
            } catch (e: Exception) {
                _mensaje.value = "Error obteniendo cierre: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearCierreParaImprimir() {
        _cierreParaImprimir.value = null
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}