package com.example.demoges_parking.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoges_parking.model.CierreData
import com.example.demoges_parking.model.CierreRegistroRequest
import com.example.demoges_parking.model.CierreRequest
import com.example.demoges_parking.model.CierreResponseDTO
import com.example.demoges_parking.network.ApiClient
import com.example.demoges_parking.network.ApiService
import com.example.demoges_parking.utils.obtenerFechaActual
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CierreViewModel: ViewModel() {

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _datosTurno = MutableStateFlow<CierreResponseDTO?>(null)
    val datosTurno: StateFlow<CierreResponseDTO?> = _datosTurno

    private val _numeroDeTurno = MutableStateFlow(0)
    val numeroDeTurno: StateFlow<Int> = _numeroDeTurno

    private val _turnoCierre = MutableStateFlow("")
    val turnoCierre: StateFlow<String> = _turnoCierre

    private val _empleado = MutableStateFlow("")
    val empleadoCierre: StateFlow<String> = _empleado

    private val _fechadeInicio = MutableStateFlow("")
    val fechaDeInicio: StateFlow<String> = _fechadeInicio

    private val _fechaDeSalida = MutableStateFlow("")
    val fechaDeSalida: StateFlow<String> = _fechaDeSalida

    private val _totalVehiculos = MutableStateFlow(0)
    val totalVehiculos: StateFlow<Int> = _totalVehiculos

    private val _cierreEnviado = MutableStateFlow(false)
    val cierreEnviado: StateFlow<Boolean> = _cierreEnviado

    private val _vehiculosRecibidos = MutableStateFlow(0)
    val vehiculosRecibidos: StateFlow<Int> = _vehiculosRecibidos


    private val _base = MutableStateFlow("0")
    val base: StateFlow<String> = _base
    private var baseInt = 0

    private val _totalEfectivo = MutableStateFlow(0)
    val totalEfectivo: StateFlow<Int> = _totalEfectivo

    private val _totalTarjeta = MutableStateFlow(0)
    val totalTarjeta: StateFlow<Int> = _totalTarjeta

    private val _totalTransferencia = MutableStateFlow(0)
    val totalTransferencia: StateFlow<Int> = _totalTransferencia

    private val _otrosIngresos = MutableStateFlow("0")
    val otrsingresos: StateFlow<String> = _otrosIngresos
    private var otrosIngresosInt = 0

    private val _totalAbonos = MutableStateFlow(0)
    val totalAbonos: StateFlow<Int> = _totalAbonos

    private val _efectivoLiquido = MutableStateFlow(0)
    val efectivoLiquido: StateFlow<Int> = _efectivoLiquido

    private val _totalRecaudado = MutableStateFlow(0)
    val totalRecaudado: StateFlow<Int> = _totalRecaudado

    private val _totalEfectivoAbono = MutableStateFlow(0)
    val totalEfectivoAbono: MutableStateFlow<Int> = _totalEfectivoAbono

    private val _totalTarjetaAbono = MutableStateFlow(0)
    val totalTarjetaAbono: StateFlow<Int> = _totalTarjetaAbono

    private val _totalTransferenciaAbono = MutableStateFlow(0)
    val totalTransferenciaAbono: StateFlow<Int> = _totalTransferenciaAbono

    private val _observaciones = MutableStateFlow("")
    val observaciones: StateFlow<String> = _observaciones

    // Estado para indicar si el registro fue exitoso
    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    private val _mostrarDialogo = MutableStateFlow(false)
    val mostrarDialogo: StateFlow<Boolean> = _mostrarDialogo

    private val _cierreExitoso = MutableStateFlow(false)
    val cierreExitoso: StateFlow<Boolean> = _cierreExitoso

    private val _cierreRegistrado = MutableStateFlow<CierreData?>(null)
    val cierreRegistrado: StateFlow<CierreData?> = _cierreRegistrado


    //Actualizar los estados observables
    fun actualizarNumeroDeTurno(numeroDeTurno: Int){
        _numeroDeTurno.value = numeroDeTurno
    }

    fun actualizarTotalEfectivoAbono(totalefectivoabono: Int){
        _totalEfectivoAbono.value = totalefectivoabono
    }

    fun actualizarTotalTarjetaAbono(totaltarjetaabono: Int){
        _totalTarjetaAbono.value = totaltarjetaabono
    }

    fun actualizarTotalTransferenciaAbono(totaltransferenciaabono: Int){
        _totalTransferenciaAbono.value = totaltransferenciaabono
    }

    fun actualizarTurnoCierre(turnocierre: String) {
        _turnoCierre.value = turnocierre
    }

    fun actualizarEmpleado(empleado: String){
        _empleado.value = empleado
    }

    fun actualizarFechaInicio(fechainicio: String){
        _fechadeInicio.value = fechainicio
    }

    fun actualizarFechaSalida(fechasalida: String){
        _fechaDeSalida.value = fechasalida
    }

    fun actualizarTotalVehiculos(totalvehiculos: Int){
        _totalVehiculos.value = totalvehiculos
    }

    fun actualizarVehiculosRecibidos(vehiculosrecibidos: Int){
        _vehiculosRecibidos.value = vehiculosrecibidos
    }

    fun actualizarBase(base: String){
        _base.value = base
        baseInt = base.toIntOrNull() ?: 0
    }

    fun actualizarTotalEfectivo(totatefectivo: Int){
        _totalEfectivo.value = totatefectivo
    }

    fun actualizarTotalTarjeta(totaltarjeta: Int){
        _totalTarjeta.value = totaltarjeta
    }

    fun actualizarTotalTransferencia(totaltransferencia: Int){
        _totalTransferencia.value = totaltransferencia
    }

    fun actualizarOtrosIngresos(otrosingresos: String){
        _otrosIngresos.value = otrosingresos
        otrosIngresosInt = otrosingresos.toIntOrNull() ?: 0
    }

    fun actualizarTotalAbonos(){
        _totalAbonos.value = _totalEfectivoAbono.value + _totalTarjetaAbono.value + _totalTransferenciaAbono.value
    }

    fun actualizarEfectivoLiquido(efectivoliquido: Int){
        _efectivoLiquido.value = efectivoliquido
    }

    fun actualizarTotalRecaudado(totalrecaudado: Int){
        _totalRecaudado.value = totalrecaudado
    }

    fun actualizarObservaciones(observaciones: String){
        _observaciones.value = observaciones
    }

    fun abrirDialogoConfirmacion() {
        _mostrarDialogo.value = true
    }

    fun cerrarDialogoConfirmacion() {
        _mostrarDialogo.value = false
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun obtenerFechaSalida() {
//        // Obtener la fecha y hora actual usando java.time
//        val fechaActual = java.time.LocalDateTime.now()
//            .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a", Locale.ENGLISH))
//
//        // Actualizar el estado observable
//        _fechaDeSalida.value = fechaActual
//    }

    fun obtenerDatosTurno(numeroTurno: Int){
        viewModelScope.launch {
            Log.d("CierreViewModel", "Número de turno enviado: $numeroTurno")
            try {
                // Llamar al endpoint para obtener los datos del turno
                val response = ApiClient.apiService.obtenerDatosTurno(CierreRequest(numeroTurno))

                if(response.isSuccessful && response.body()?.success == true){
                    // Usar let para manejar el objeto data de forma segura
                    response.body()?.data?.let { datos ->
                        _datosTurno.value = datos
                        actualizarTotalVehiculos(datos.totalVehiculosActivos)
                        actualizarTotalEfectivo(datos.totalEfectivo)
                        actualizarTotalTarjeta(datos.totalTarjeta)
                        actualizarTotalTransferencia(datos.totalTransferencia)
                        actualizarTotalEfectivoAbono(datos.totalEfectivoAbono)
                        actualizarTotalTarjetaAbono(datos.totalTarjetaAbono)
                        actualizarTotalTransferenciaAbono(datos.totalTransferenciaAbono)
                        _fechaDeSalida.value = obtenerFechaActual()
                    }
                }else{
                    // Manejar errores en la respuesta
                    _message.value = response.body()?.message ?: "No se pudo obtener los datos del turno a cerrar"
                }

                calcularReacaudoTotal()
            }catch (e: Exception){
                // Manejar errores de red
                _message.value = "Error de red: ${e.message}"
            }
        }
    }

    fun registrarCierreTurno() {
        viewModelScope.launch {

            if (_cierreEnviado.value) return@launch // Previene múltiples envíos

            _cierreEnviado.value = true // Bloquea el botón inmediatamente
            _registroExitoso.value = false

            // Validación local antes de enviar
            val turno = _turnoCierre.value
            val numeroTurno = _numeroDeTurno.value
            val empleado = _empleado.value
            val fechaInicio = _fechadeInicio.value
            val fechaSalida = _fechaDeSalida.value
            val recibidos = _vehiculosRecibidos.value
            val totalVehiculos = _totalVehiculos.value
            val base = baseInt
            val efectivo = _totalEfectivo.value
            val tarjeta = _totalTarjeta.value
            val transferencia = _totalTransferencia.value
            val otrosIngresos = otrosIngresosInt
            val efectivoLiquido = _efectivoLiquido.value
            val totalRecaudado = _totalRecaudado.value
            val observaciones = _observaciones.value
            val totalAbonos = _totalAbonos.value

            // Validación de campos requeridos y válidos
            if (
                turno.isBlank() || numeroTurno == 0 || empleado.isBlank() || fechaInicio.isBlank() || fechaSalida.isBlank()
                || totalVehiculos < 0 || base < 0 || efectivo < 0 || tarjeta < 0
                || transferencia < 0 || efectivoLiquido < 0 || totalRecaudado < 0 || observaciones.isBlank()
                || totalAbonos < 0
            ) {
                _message.value = "Todos los campos son requeridos y deben ser válidos"
                _cierreEnviado.value = false // Permite reintentar si hay error de validación
                return@launch
            }

            try {
                val request = CierreRegistroRequest(
                    turno = turno,
                    numeroturno = numeroTurno,
                    empleado = empleado,
                    fechaingreso = fechaInicio,
                    fechasalida = fechaSalida,
                    recibidos = recibidos,
                    totalvehiculos = totalVehiculos,
                    base = base,
                    efectivo = efectivo,
                    tarjeta = tarjeta,
                    transferencia = transferencia,
                    otrosingresos = otrosIngresos,
                    efectivoliquido = efectivoLiquido,
                    totalrecaudado = totalRecaudado,
                    observaciones = observaciones,
                    totalabonos = totalAbonos
                )

                //Mostrar lo que se está enviando
                Log.d("CierreRegistro", "Request JSON: ${Gson().toJson(request)}")

                val response = ApiClient.apiService.registrarCierre(request)
                Log.d("CierreRegistro", "Código: ${response.code()}")
                Log.d("CierreRegistro", "Body: ${response.body()}")
                Log.d("CierreRegistro", "ErrorBody: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    val cierreData = response.body()?.cierre
                    _cierreRegistrado.value = cierreData // 🔹 Guardamos el cierre
                    _registroExitoso.value = true
                    _message.value = response.body()?.message ?: "Registro exitoso"
                    _cierreExitoso.value = true

                } else {
                    _message.value = response.body()?.message ?: "Error desconocido"
                    _cierreEnviado.value = false // Si falla, permite reintentar
                }

            } catch (e: Exception) {
                _message.value = "Error de red: ${e.message}"
                _cierreEnviado.value = false // Si hay error de red, permite reintentar
                _cierreExitoso.value = false
            }
        }
    }


    fun calcularReacaudoTotal(){
        _totalRecaudado.value = _totalEfectivo.value + _totalTarjeta.value + _totalTransferencia.value + _totalEfectivoAbono.value +
                _totalTarjetaAbono.value + _totalTransferenciaAbono.value
    }

    fun validarYCerrarTurno() {
        val turno = _turnoCierre.value
        val numeroTurno = _numeroDeTurno.value
        val empleado = _empleado.value
        val fechaInicio = _fechadeInicio.value
        val fechaSalida = _fechaDeSalida.value
        val recibidos = _vehiculosRecibidos.value
        val totalVehiculos = _totalVehiculos.value
        val base = baseInt
        val efectivo = _totalEfectivo.value
        val tarjeta = _totalTarjeta.value
        val transferencia = _totalTransferencia.value
        val otrosIngresos = otrosIngresosInt
        val efectivoLiquido = _efectivoLiquido.value
        val totalRecaudado = _totalRecaudado.value
        val observaciones = _observaciones.value
        val totalAbonos = _totalAbonos.value

        if (
            turno.isBlank() || numeroTurno == 0 || empleado.isBlank() || fechaInicio.isBlank() || fechaSalida.isBlank()
            || recibidos < 0 || totalVehiculos < 0 || base < 0 || efectivo < 0 || tarjeta < 0
            || transferencia < 0 || efectivoLiquido < 0 || totalRecaudado < 0 || observaciones.isBlank()
            || totalAbonos < 0
        ) {
            _message.value = "Todos los campos son requeridos y deben ser válidos"
            return
        }

        // Si pasa la validación, muestra el diálogo de confirmación
        abrirDialogoConfirmacion()
    }

    fun cargarVehiculosRecibidos(numeroTurno: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.obtenerVehiculosRecibidos(numeroTurno)
                if (response.isSuccessful) {
                    val cantidad = response.body()?.vehiculosRecibidos ?: 0
                    _vehiculosRecibidos.value = cantidad

                } else {
                    _message.value = "No se pudo obtener los vehículos recibidos"
                }
            } catch (e: Exception) {
                _message.value = "Error de conexión al obtener vehículos recibidos"
            }
        }
    }



    fun limpiarMensaje() {
        _message.value = ""
    }

    fun limpiarRegistroExitoso(){
        _registroExitoso.value = false
    }

    fun limpiarCampos(){
        _totalVehiculos.value = 0
        _vehiculosRecibidos.value = 0
        _base.value = ""
        baseInt = 0
        _otrosIngresos.value = ""
        otrosIngresosInt = 0
        _totalEfectivo.value = 0
        _totalTarjeta.value = 0
        _totalTransferencia.value = 0
        _efectivoLiquido.value = 0
        _totalRecaudado.value = 0
        _observaciones.value = ""
    }

}