package com.example.demoges_parking.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoges_parking.model.IngresoDTO
import com.example.demoges_parking.model.SalidaDTO
import com.example.demoges_parking.model.UiMessage
import com.example.demoges_parking.network.ApiClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HistorialTurnoViewModel: ViewModel() {

    private val _ingresos = MutableStateFlow<List<IngresoDTO>>(emptyList())
    val ingresos: StateFlow<List<IngresoDTO>> = _ingresos

    private val _salidas = MutableStateFlow<List<SalidaDTO>>(emptyList())
    val salidas: StateFlow<List<SalidaDTO>> = _salidas

    private val _totalIngresos = MutableStateFlow(0)
    val totalIngresos: StateFlow<Int> = _totalIngresos

    private val _totalSalidas = MutableStateFlow(0)
    val totalSalidas: StateFlow<Int> = _totalSalidas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiMessage = Channel<UiMessage>()
    val uiMessage = _uiMessage.receiveAsFlow()

    fun obtenerHistorial(numeroTurno: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = ApiClient.apiService.obtenerHistorialTurno(numeroTurno)

                if (response.isSuccessful) {

                    val result = response.body()
                    if (result != null && result.success) {
                        val data = result.data
                        _ingresos.value = data.ingresos
                        _salidas.value = data.salidas
                        _totalIngresos.value = data.totalIngresos
                        _totalSalidas.value = data.totalSalidas
                        Log.d("HISTORIAL", "Ingresos recibidos: ${data.ingresos}")
                        Log.d("HISTORIAL", "Salidas recibidas: ${data.salidas}")

                    } else {
                        _uiMessage.send(UiMessage.Error(result?.message ?: "Respuesta vacía"))
                        _ingresos.value = emptyList()
                        _salidas.value = emptyList()
                    }
                } else {
                    _uiMessage.send(UiMessage.Error("No se pudo cargar el historial"))
                }

            } catch (e: Exception) {
                _uiMessage.send(UiMessage.Error("Error de conexión"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun imprimirIngresoPorPlaca(context: Context, placa: String, printViewModel: PrintViewModel) {
        viewModelScope.launch {
            Log.d("IMPRESION", "Placa enviada: '${placa}'")

            try {
                val response = ApiClient.apiService.getIngresoActivo(placa)

                if (response.isSuccessful) {
                    val ingresoData = response.body()
                    if (ingresoData != null) {
                        // Imprimir usando el printViewModel existente
                        printViewModel.imprimirIngreso(context, ingresoData)
                    } else {
                        _uiMessage.send(UiMessage.Error("No se encontró información para la placa $placa"))
                    }
                } else {
                    _uiMessage.send(UiMessage.Error("Error ${response.code()}: No se pudo obtener el ingreso"))
                }
            } catch (e: Exception) {
                _uiMessage.send(UiMessage.Error("Error de conexión"))
            }
        }
    }

    fun imprimirSalidaPorPlaca(context: Context, idsalida: Int, nombre: String,printSalidaViewModel: PrintSalidaViewModel){
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getDetalleSalida(idsalida)

                if(response.isSuccessful){
                    val salidaData = response.body()
                    if (salidaData != null){
                        printSalidaViewModel.imprimirReciboSalida(context, salidaData, nombre)
                    }else{
                        _uiMessage.send(UiMessage.Error("No se encontró información para la placa"))
                    }
                }else{
                    _uiMessage.send(UiMessage.Error("Error ${response.code()}: No se pudo obtener la salida"))
                }
            }catch (e: Exception){
                Log.e("IMPRESION", "Excepción al obtener salida", e)
                _uiMessage.send(UiMessage.Error("Error de conexión"))
            }
        }
    }
}