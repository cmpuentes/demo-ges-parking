package com.example.demoges_parking.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoges_parking.model.IngresoData
import com.example.demoges_parking.model.IngresoRequest
import com.example.demoges_parking.model.UiMessage
import com.example.demoges_parking.network.ApiService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class IngresoVieModel(
    private val api: ApiService,
    application: Application
) : AndroidViewModel(application){

    private val _ingresoRequest = MutableStateFlow(IngresoRequest())
    val ingresoRequest: StateFlow<IngresoRequest> = _ingresoRequest

    //Métodos para actualizar los campos de la UI
    fun actualizarTurno(turno: String){
        _ingresoRequest.value = _ingresoRequest.value.copy(turno = turno)
    }

    fun actualizarPlaca(placa: String){
        _ingresoRequest.value = _ingresoRequest.value.copy(placa = placa)
    }

    fun actualizarFechaIngreso(fehaIngreso: String){
        _ingresoRequest.value = _ingresoRequest.value.copy(fechaingreso = fehaIngreso)
    }

    fun actualizarTipoVehiculo(tipoVehiculo: String){
        _ingresoRequest.value = _ingresoRequest.value.copy(tipovehiculo = tipoVehiculo)
    }

    fun actualizarTipoServicio(tipoServicio: String){
        _ingresoRequest.value = _ingresoRequest.value.copy(tiposervicio = tipoServicio)
    }

    fun actualizarCliente(cliente: String){
        _ingresoRequest.value = _ingresoRequest.value.copy(cliente = cliente)
    }

    fun actualizarZona(zona: String){
        _ingresoRequest.value = _ingresoRequest.value.copy(zona = zona)
    }

    fun actualizarObservaciones(observaciones: String){
        _ingresoRequest.value = _ingresoRequest.value.copy(observaciones = observaciones)
    }

    fun actualizarNumeroTurno(numeroTurno: Int){
        _ingresoRequest.value = _ingresoRequest.value.copy(numeroturno = numeroTurno)
    }

    fun actualizarEmpleado(empleado: String){
        _ingresoRequest.value = _ingresoRequest.value.copy(empleado = empleado)
    }

    private val _dataIngresada = MutableStateFlow(IngresoData())
    val dataIngresada: StateFlow<IngresoData> = _dataIngresada


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _uiMessage = Channel<UiMessage>()
    val uiMessage = _uiMessage.receiveAsFlow()

    private val _tiposVehiculo = MutableStateFlow<List<String>>(emptyList())
    val tiposVehiculo: StateFlow<List<String>> = _tiposVehiculo

    private val _tiposServicio = MutableStateFlow<List<String>>(emptyList())
    val tiposServicio: StateFlow<List<String>> = _tiposServicio

    private val _mostrarDialogo = MutableStateFlow(false)
    val mostrarDialogo: StateFlow<Boolean> = _mostrarDialogo

    fun mostrarDialogo() {
        _mostrarDialogo.value = true
    }

    fun cerrarDialogo() {
        _mostrarDialogo.value = false
    }



    fun registrarIngreso() {
        viewModelScope.launch {
            val dto = _ingresoRequest.value

            if (!validarCampos()) {
                _uiMessage.send(UiMessage.Error("Todos los campos son obligatorios"))
                return@launch
            }

            _isLoading.value = true

            try {
                val response = api.registrarIngreso(dto)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        _uiMessage.send(UiMessage.Success(body.message))
                        _dataIngresada.emit(body.data) // <-- para impresión
                        limpiarFormulario()
                    } else {
                        _uiMessage.send(UiMessage.Error(body?.message ?: "No se pudo registrar el ingreso"))
                    }
                } else {
                    //_uiMessage.send(UiMessage.Error("Error ${response.code()} al registrar el ingreso"))
                    _uiMessage.send(UiMessage.Error("La placa que desea ingresar ya se encuentra en el parqueadero."))
                }

            } catch (e: Exception) {
                _uiMessage.send(UiMessage.Error("Error al registrar ingreso: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun consultarClientePrepago(placa: String) {
        viewModelScope.launch {
            try {
                val response = api.consultarClientePrepago(placa)
                val cliente = response.string()  // ← Aquí ya tienes el texto plano
                Log.d("IngresoViewModel", "Cliente recibido: $cliente")
                actualizarCliente(cliente)
            } catch (e: Exception) {
                Log.e("IngresoViewModel", "Error al consultar prepago", e)
                _uiMessage.send(UiMessage.Error("Error al consultar prepago"))
            }
        }
    }

    fun cargarTiposVehiculo() {
        viewModelScope.launch {
            try {
                _tiposVehiculo.value = api.obtenerTiposVehiculo()
            } catch (e: Exception) {
                _uiMessage.send(UiMessage.Error("Error al cargar tipos de vehículo"))
            }
        }
    }

    fun cargarTipoServicio(){
        viewModelScope.launch {
            try{
                _tiposServicio.value = api.obtenerTiposServicio()
            }catch (e: Exception){
                _uiMessage.send(UiMessage.Error("Error al cargar tipos de vehículo"))
            }
        }
    }

    fun validarCampos(): Boolean {
        val r = _ingresoRequest.value
        return r.turno.isNotBlank() &&
                r.placa.isNotBlank() &&
                r.fechaingreso.isNotBlank() &&
                r.tipovehiculo.isNotBlank() &&
                r.tiposervicio.isNotBlank() &&
                r.cliente.isNotBlank() &&
                r.zona.isNotBlank() &&
                r.numeroturno > 0 &&
                r.empleado.isNotBlank()
    }

    fun limpiarFormulario(){
        actualizarTurno("")
        actualizarPlaca("")
        actualizarFechaIngreso("")
        actualizarTipoVehiculo("")
        actualizarTipoServicio("")
        actualizarCliente("")
        actualizarZona("")
        actualizarObservaciones("")
        actualizarNumeroTurno(0)
        actualizarEmpleado("")
    }
}