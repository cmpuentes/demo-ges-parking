package com.example.demoges_parking.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoges_parking.model.SalidaPlacaReq
import com.example.demoges_parking.model.SalidaReq
import com.example.demoges_parking.model.SalidaRes
import com.example.demoges_parking.model.SalidaResponseDTO
import com.example.demoges_parking.model.Tarifa
import com.example.demoges_parking.model.TarifaRequest
import com.example.demoges_parking.network.ApiClient
import com.example.demoges_parking.utils.obtenerFechaActual
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class SalidaViewModel: ViewModel() {

    private val _placa = MutableStateFlow("")
    val placa: StateFlow<String> = _placa

    private val _empleadoSalida = MutableStateFlow("")
    val empleadoSalida: StateFlow<String> = _empleadoSalida

    private val _empleadoIngreso = MutableStateFlow("")
    val empleadoIngreso: StateFlow<String> = _empleadoIngreso

    private val _turnoSalida = MutableStateFlow("")
    val turnoSalida: StateFlow<String> = _turnoSalida

    private val _numeroturnosalida = MutableStateFlow(0)
    val numeroturnosalida: StateFlow<Int> = _numeroturnosalida

    private val _idingreso = MutableStateFlow(0)
    val idingreso: StateFlow<Int> = _idingreso

    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total

    private val _numerorecibo = MutableStateFlow(0)//NUMERO RECIBO
    val numerorecibo: StateFlow<Int> = _numerorecibo

    private val _efectivo = MutableStateFlow("0")
    val efectivo: StateFlow<String> = _efectivo
    private var efectivoInt = 0

    private val _tarjeta = MutableStateFlow("0")
    val tarjeta: StateFlow<String> = _tarjeta
    private var tarjetaInt = 0

    private val _transferencia = MutableStateFlow("0")
    val transferencia: StateFlow<String> = _transferencia
    private var transferenciaInt = 0

    private val _descuento = MutableStateFlow(0)
    val descuento: MutableStateFlow<Int> = _descuento
    //private var descuentoInt = 0

    private val _subtotal = MutableStateFlow(0)
    val subtotal: StateFlow<Int> = _subtotal

    private val _cliente = MutableStateFlow("")
    val cliente: StateFlow<String> = _cliente

    private val _tarifa = MutableStateFlow<Tarifa?>(null)
    val tarifa: StateFlow<Tarifa?> = _tarifa

    // Estado observable para almacenar los datos de salida
    private val _salidaResult = MutableStateFlow<SalidaRes?>(null)
    val salidaResult: StateFlow<SalidaRes?> = _salidaResult

    //Estado observable para costo total
    private val _costoTotal = MutableStateFlow(0)
    val costoTotal: StateFlow<Int> = _costoTotal

    //Estado observable para dias
    private val _dias = MutableStateFlow(0)
    val dias: StateFlow<Int> = _dias

    //Estado observable para horas
    private val _horas = MutableStateFlow(0)
    val horas: StateFlow<Int> = _horas

    //Estado observable para minutos
    private val _minutos = MutableStateFlow(0)
    val minutos: StateFlow<Int> = _minutos

    //Estado observable para tipo de servicio
    private val _tipoServicio = MutableStateFlow("")
    val tipoServicio: StateFlow<String> = _tipoServicio

    //Estado observable para tipo de vehículo
    private val _tipoVehiculo = MutableStateFlow("")
    val tipoVehiculo: StateFlow<String> = _tipoVehiculo

    //Estado observable para la fecha de ingreso
    private val _fechaIngreso = MutableStateFlow("")
    val fechaIngreso: StateFlow<String> = _fechaIngreso

    //Estado observable para fecha de salida
    private val _fechaSalida = MutableStateFlow("")
    val fechaSalida: StateFlow<String> = _fechaSalida

    private val _numeroturnoingreso = MutableStateFlow(0)
    val numeroturnoingreso: StateFlow<Int> = _numeroturnoingreso

    private val _zona = MutableStateFlow("")
    val zona: StateFlow<String> = _zona

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _salidaImpresion = MutableStateFlow<SalidaResponseDTO?>(null)
    val salidaImpresion: StateFlow<SalidaResponseDTO?> = _salidaImpresion

    @RequiresApi(Build.VERSION_CODES.O)
    private val FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a", Locale.ENGLISH)

    private val _mostrarDialogo = MutableStateFlow(false)
    val mostrarDialogo: StateFlow<Boolean> = _mostrarDialogo

    fun mostrarDialogo() {
        _mostrarDialogo.value = true
    }

    fun cerrarDialogo() {
        _mostrarDialogo.value = false
    }

    private val _eventoSalida = MutableSharedFlow<SalidaResponseDTO?>()
    val eventoSalida: SharedFlow<SalidaResponseDTO?> = _eventoSalida


    // Inicialización del ViewModel
    init {
        viewModelScope.launch {
            _tarifa.collect { tarifa ->
                if (tarifa != null) {
                    // Calcular el costo cuando _tarifa.value esté disponible
                    calcularCosto()
                    //calcularValorConDescuento()
                }
            }
        }
    }

    //Método para actualizar el mensaje
    fun actualizarMessage(message: String){
        _message.value = message
    }

    //Método para actualizar idingreso
    fun actualizarIdingreso(idingreso: Int){
        _idingreso.value = idingreso
    }

    //Método para actualizar zona
    fun actualizarZona(zona: String){
        _zona.value = zona
    }

    //Método para actualizar número de turno ingreso
    fun actualizarNumeroTurnoIngreso(numeroturnoingreso: Int){
        _numeroturnoingreso.value = numeroturnoingreso
    }

    //Método para actualizar empledo salida
    fun actualizarEmpleadoSalida(empleadosalida: String){
        _empleadoSalida.value = empleadosalida
    }

    //Método para actualizar empleado salida
    fun actualizarEmpladoIngreso(empladoingreso: String){
        _empleadoIngreso.value = empladoingreso
    }

    //Método para actualizar el nutmero de turno salida
    fun actualizarNumeroTurnoSalida(numturnosalida: Int){
        _numeroturnosalida.value = numturnosalida
    }

    //Método para actualizar turno salida
    fun actuelizarTUrnoSalida(turnoSalida: String){
        _turnoSalida.value = turnoSalida
    }

    //Método para actualizar placa
    fun actulizarPlaca(placa: String){
        _placa.value = placa
    }

    //Método para actualizar numero de recibo
    fun actualizarNumeroRecibo(input: String) {
        // Si el usuario borra todo, mantenemos 0 por defecto
        val valor = input.toIntOrNull() ?: 0
        _numerorecibo.value = valor

        // Solo hacemos el cálculo si el valor es > 0 y las dependencias ya están listas
        if (valor > 0) {
            try {
                calcularDescuento()
            } catch (e: Exception) {
                Log.e("SalidaViewModel", "Error en cálculo: ${e.message}")
            }
        }
    }

    //Método para actualizar total
    fun actualizarTotal() {
        _total.value = efectivoInt + tarjetaInt + transferenciaInt
    }

    //Método para actualizar efectivo
    fun actualizarEfectivo(efectivo: String){
        _efectivo.value = efectivo
        efectivoInt = efectivo.toIntOrNull()?:0
    }

    //Método para actualizar tarjeta
    fun actualizarTarjeta(tarjeta: String){
        _tarjeta.value = tarjeta
        tarjetaInt = tarjeta.toIntOrNull()?:0
    }

    //Método para actualizar transferencia
    fun actualizarTransferencia(transferencia: String){
        _transferencia.value = transferencia
        transferenciaInt = transferencia.toIntOrNull()?:0
    }

    //Método para actualizar descuento
//    fun actualizarDescuento(descuento: String){
//        if(descuento.all { it.isDigit() }){
//            _descuento.value = descuento
//            descuentoInt = descuento.toIntOrNull()?:0
//        }
//    }

    //Método para actualizar subtotal
    fun actualizarSubtotal(costoTotal: Int){
        _subtotal.value = costoTotal
    }

    //Método para actualizar cliente
    fun actualizarCliente(cliente: String){
        _cliente.value = cliente
    }

    //Método para actualizar costo total
    fun actualizarCostototal(costoTotal: Int){
        _costoTotal.value = costoTotal
    }

    //Método para actualizar días
    fun actualizarDias(dias: Int){
        _dias.value = dias
    }

    //Método para actualizar horas
    fun actualizarHoras(horas: Int){
        _horas.value = horas
    }

    //Método para actualizar miniutos
    fun actualizarMinutos(minutos: Int){
        _minutos.value = minutos
    }

    //Método para actualizar tipo de servicio
    fun actualizarTipoServicio(tipoServicio: String){
        _tipoServicio.value = tipoServicio
    }

    //Método para actualizar tipo de vehículo
    fun actualizarTipoVehiculo(tipoVehiculo: String){
        _tipoVehiculo.value = tipoVehiculo
    }

    //Método para actiualizar la fecha de ingreso
    fun actualizarFechaIngreso(fechaIngreso: String){
        _fechaIngreso.value = fechaIngreso
    }

    //Método para actualizar la fecha de salida
    fun actualizarFechaSalida(fechaSalida: String){
        _fechaSalida.value = fechaSalida
    }

    @RequiresApi(Build.VERSION_CODES.O)
//    fun obtenerFechaSalida() {
//        // Obtener la fecha y hora actual usando java.time
////        val fechaActual = LocalDateTime.now().format(FORMATO_FECHA)
////            .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a", Locale.ENGLISH))
//
//        val formatoFecha = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
//        val fechaActual = formatoFecha.format(Date())
//        // Actualizar el estado observable
//        _fechaSalida.value = fechaActual
//    }

    fun consultarSalidaPorPlaca(placa: String){

        if (placa.isBlank()) {
            _message.value = "Ingrese una placa"
            return
        }

        viewModelScope.launch {
            try {
                // Llamada al endpoint de salida
                val response = ApiClient.apiService.consultarSalidaPorPlaca(
                    SalidaPlacaReq(placa = placa)
                )

                if (response.isSuccessful && response.body() != null){
                    val body = response.body()
                    if(body?.success == true){
                        _salidaResult.value = body // Almacena los datos de salida

                        // Actualizar campos específicos
                        body.data?.let { data ->
                            actualizarIdingreso(data.idingreso)
                            actualizarFechaIngreso(data.fechaingreso)
                            actualizarTipoServicio(data.tiposervicio)
                            actualizarTipoVehiculo(data.tipovehiculo)
                            actualizarNumeroTurnoIngreso(data.numeroturno)
                            actualizarCliente(data.cliente)
                            actualizarZona(data.zona)
                            actualizarEmpladoIngreso(data.empleado)
                        }
                        // Obtener la fecha de salida actual
                        _fechaSalida.value = obtenerFechaActual()

                        // Calcular el tiempo transcurrido
                        calcularTiempoTranscurrido()

                        // Obtener la tarifa correspondiente
                        obtenerTarifa()
                    }else{
                        _message.value = body?.message ?: "Error, la placa ingresada no se encuentra en el parqueadero."
                    }
                }else{
                    _message.value = "Esta placa no está en el parqueadero, ingrese una placa valida."
                }
            }catch (e: Exception){
                _message.value = "Error de red: ${e.message}"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calcularTiempoTranscurrido() {
        viewModelScope.launch {
            try {
                // Validar que ambas fechas estén presentes
                if (_fechaIngreso.value.isEmpty() || _fechaSalida.value.isEmpty()) {
                    _message.value = "Ambas fechas deben estar completas."
                    return@launch
                }

                // Normalizar las fechas
                val fechaInicioNormalizada = normalizarFecha(_fechaIngreso.value)
                val fechaFinNormalizada = normalizarFecha(_fechaSalida.value)

                Log.d("FechaDebug", "Fecha inicio normalizada: $fechaInicioNormalizada")
                Log.d("FechaDebug", "Fecha fin normalizada: $fechaFinNormalizada")

                // Parsear las fechas
                val formato = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a", Locale.ENGLISH)
                val inicio = LocalDateTime.parse(fechaInicioNormalizada, formato)
                val fin = LocalDateTime.parse(fechaFinNormalizada, formato)

                // Validar que la fecha de inicio sea anterior a la fecha fin
                if (inicio >= fin) {
                    _message.value = "La fecha de inicio debe ser anterior a la fecha fin."
                    return@launch
                }

                // Calcular la duración entre las dos fechas
                val duracion = Duration.between(inicio, fin)
                val totalMinutos = duracion.toMinutes()

                // Calcular días, horas y minutos
                val dias = totalMinutos / (24 * 60)
                val horasRestantes = (totalMinutos % (24 * 60)) / 60
                val minutosRestantes = totalMinutos % 60

                // Actualizar los estados observables
                _dias.value = dias.toInt()
                _horas.value = horasRestantes.toInt()
                _minutos.value = minutosRestantes.toInt()

                Log.d("FechaDebug", "Días: $dias, Horas: $horasRestantes, Minutos: $minutosRestantes")
            } catch (e: Exception) {
                _message.value = "Error al procesar las fechas: ${e.message}"
            }
        }
    }

    // Función original calcularCosto
    private suspend fun calcularCosto() {
        val tipoServicio = _tipoServicio.value
        val tipoVehiculo = _tipoVehiculo.value
        val dias = _dias.value
        val horas = _horas.value
        val minutos = _minutos.value

        // Log: Mostrar los valores iniciales
        println("=== INICIANDO CÁLCULO DE COSTO ===")
        println("Tipo de servicio: $tipoServicio")
        println("Tipo de vehículo: $tipoVehiculo")
        println("Días: $dias")
        println("Horas: $horas")
        println("Minutos: $minutos")

        // Si es empleado o prepago, no se cobra
        if (tipoServicio == "EMPLEADO" || tipoServicio == "PREPAGO") {
            println("El tipo de servicio es EMPLEADO o PREPAGO. No se cobra.")
            _tarifa.value = Tarifa(precio12h = 0, descuentorecibo = 0, preciohoras = 0)
            _costoTotal.value = 0
            actualizarSubtotal(0)
            //_error.value = "" // ← limpia error por si acaso
            return
        }

        // Obtener la tarifa actual desde _tarifa
        val tarifa = _tarifa.value
            ?: run {
                println("Error: No se encontraron tarifas para realizar el cálculo.")
                _message.value = "No se encontraron tarifas para realizar el cálculo."
                return
            }

        // Log: Mostrar los valores de la tarifa
        println("Tarifa obtenida:")
        println("- Precio 12h: ${tarifa.precio12h}")
        println("- Precio por hora: ${tarifa.preciohoras}")

        var costoDia = 0
        var costoHoras = 0

        val precio12H = tarifa.precio12h
        val precioHora = tarifa.preciohoras

        // Redondear minutos a la siguiente hora si son > 15
        var horasTotales = horas
        if (minutos > 15) {
            horasTotales += 1
            println("Redondeando minutos (> 15). Horas totales: $horasTotales")
        } else {
            println("Minutos <= 15. Horas totales sin redondear: $horasTotales")
        }

        // Cobro por días completos (cada día equivale a 2 bloques de 12h)
        if (dias > 0) {
            costoDia += dias * (precio12H * 2)
            println("Cobro por días: $dias días * (${precio12H} * 2) = $costoDia")
        } else {
            println("No hay días para cobrar.")
        }

        // Cobro por horas
        when {
            horasTotales in 1..5 -> {
                costoHoras += horasTotales * precioHora
                println("Cobro por horas (1-5): $horasTotales horas * $precioHora = $costoHoras")
            }
            horasTotales in 6..12 -> {
                costoDia += precio12H
                println("Cobro por horas (6-12): Bloque de 12h = $precio12H")
            }
            horasTotales in 13..17 -> {
                costoDia += precio12H
                costoHoras += (horasTotales - 12) * precioHora
                println("Cobro por horas (13-17): Bloque de 12h + ${(horasTotales - 12)} horas * $precioHora = ${costoDia + costoHoras}")
            }
            horasTotales > 17 -> {
                costoDia += 2 * precio12H
                println("Cobro por horas (> 17): 2 bloques de 12h = ${2 * precio12H}")
            }
            else -> {
                println("No hay horas para cobrar.")
            }
        }

        // Calcular el costo total
        val costoTotal = costoDia + costoHoras
        println("Costo total calculado: $costoTotal")
        _costoTotal.value = costoTotal
        actualizarSubtotal(costoTotal)

        println("=== FIN DEL CÁLCULO DE COSTO ===")
    }

    private fun obtenerTarifa() {
        viewModelScope.launch {
            try {
                val tipoServicio = _tipoServicio.value
                val tipoVehiculo = _tipoVehiculo.value

                // Validar que los valores no sean nulos o vacíos
                if (tipoServicio.isEmpty() || tipoVehiculo.isEmpty()) {

                    _message.value = "Tipo de servicio o tipo de vehículo no disponibles."
                    return@launch
                }

                // Crear el objeto de solicitud
                val request = TarifaRequest(tipoServicio, tipoVehiculo)

                // Llamar al endpoint para obtener la tarifa
                val response = ApiClient.apiService.obtenerTarifa(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        val tarifa = body.data
                        if (tarifa != null) {
                            _tarifa.value = tarifa // Actualizar el estado observable con la tarifa
                        } else {
                            // Asignar valores predeterminados si no se encontró ninguna tarifa
                            _tarifa.value = Tarifa(precio12h = 0, descuentorecibo = 0, preciohoras = 0)
                            _message.value = "No se encontraron tarifas. Usando valores predeterminados."
                            _message.value = "No se encontraron datos de tarifa."
                        }
                    } else {
                        _message.value = body?.message ?: "Error, no se encontro tarifa para este vehículo."
                    }
                } else {
                    _message.value = "Error en la respuesta del servidor al obtener tarifas."
                }
            } catch (e: Exception) {
                _message.value = "Error de red: ${e.message}"
            }
        }
    }

    fun registrarSalida(){
        viewModelScope.launch {
            Log.d("DEBUG", "Fecha formateada salida: ${_fechaSalida.value}")

            try {
                if (
                    _placa.value.isEmpty() || _tipoVehiculo.value.isEmpty() ||
                    _tipoServicio.value.isEmpty() || _cliente.value.isEmpty() || _fechaIngreso.value.isEmpty() ||
                    _fechaSalida.value.isEmpty() || _zona.value.isEmpty() || _efectivo.value.isEmpty() || _tarjeta.value.isEmpty() || _transferencia.value.isEmpty() ||
                    _turnoSalida.value.isEmpty() || _empleadoIngreso.value.isEmpty() ||
                    _empleadoSalida.value.isEmpty()
                ){
                    _message.value = "Todos los campos son requeridos"
                    return@launch
                }
                //Validar campos numéricos
                val idingreso = _idingreso.value
                val dias = _dias.value
                val horas = _horas.value
                val minutos = _minutos.value
                val costoTotal = _costoTotal.value
                val descuento = _descuento.value
                val subtotal = _subtotal.value
                val efectivo = efectivoInt
                val tarjeta = tarjetaInt
                val transferencia = transferenciaInt
                val numeroturnoingreso = _numeroturnoingreso.value
                val numeroturnosalida = _numeroturnosalida.value
                val total = _total.value

                if (idingreso < 0 || dias < 0 || horas < 0|| minutos < 0|| costoTotal < 0 || descuento < 0 ||
                    subtotal < 0|| efectivo < 0|| tarjeta < 0 || transferencia < 0|| numeroturnoingreso < 0||
                    numeroturnosalida < 0
                ){
                    _message.value = "Los valores numéricos deben ser mayores o iguales a cero."
                    return@launch
                }
                // Crear el objeto de solicitud
                val request = SalidaReq(
                    idingreso = idingreso,
                    placa = _placa.value,
                    tipovehiculo = _tipoVehiculo.value,
                    tiposervicio = _tipoServicio.value,
                    cliente = _cliente.value,
                    fechaingreso = _fechaIngreso.value,
                    fechasalida = _fechaSalida.value,
                    zona = _zona.value,
                    dias = dias,
                    horas = horas,
                    minutos = minutos,
                    costototal = costoTotal,
                    numerorecibo = _numerorecibo.value,
                    descuento = descuento,
                    subtotal = subtotal,
                    efectivo = efectivo,
                    tarjeta = tarjeta,
                    transferencia = transferencia,
                    total = total,
                    turno = _turnoSalida.value,
                    turnoentrada = numeroturnoingreso,
                    empleadoentrada = _empleadoIngreso.value,
                    turnosalida = numeroturnosalida,
                    empleadosalida = _empleadoSalida.value
                )
                // Llamar al endpoint para registrar la salida
                val response = ApiClient.apiService.registrarSalida(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    // Registro exitoso
                    val datosSalida = response.body()?.data
                    _salidaImpresion.value = datosSalida
                    // emitir evento una sola vez (suspend, estamos en coroutine)
                    _eventoSalida.emit(datosSalida)
                    //mostrarDialogo()
                    Log.d("RegistroSalida", "Código: ${response.code()}")
                    Log.d("RegistroSalida", "Body: ${response.body()}")

                } else {
                    // Error en la respuesta del servidor
                    _message.value = response.body()?.message ?: "No se pudo realizar el registro, intente nuevamente."
                    Log.d("RegistroSalida", "ErrorBody: ${response.errorBody()?.string()}")

                }
            }catch (e: Exception){
                // Error de red o excepción
                _message.value = "Error de red: ${e.message}"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun normalizarFecha(fecha: String): String {
        return try {
            // Formatos flexibles para parsear la fecha
            val inputFormats = listOf(
                DateTimeFormatter.ofPattern("d-M-yyyy h:m a", Locale.ENGLISH), // Ejemplo: "3-3-2025 5:8 AM"
                DateTimeFormatter.ofPattern("dd-MM-yyyy h:mm a", Locale.ENGLISH), // Ejemplo: "03-03-2025 5:08 AM"
                DateTimeFormatter.ofPattern("d-M-yyyy h:mm a", Locale.ENGLISH) // Ejemplo: "3-3-2025 5:08 AM"
            )

            // Intentar parsear con cada formato hasta encontrar uno válido
            val fechaParseada = inputFormats.mapNotNull { formatter ->
                runCatching {
                    LocalDateTime.parse(fecha, formatter)
                }.getOrNull()
            }.firstOrNull()

            // Si se pudo parsear, formatear la fecha al formato estricto
            fechaParseada?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a", Locale.ENGLISH)) ?: fecha
        } catch (e: Exception) {
            // Si falla la normalización, devolver la fecha original
            fecha
        }
    }

    fun limpiarCampos(){
        _placa.value = ""
        _tipoVehiculo.value = ""
        _tipoServicio.value = ""
        _fechaIngreso.value = ""
        _fechaSalida.value = ""
        _dias.value = 0
        _horas.value = 0
        _minutos.value = 0
        _costoTotal.value = 0
        _tarifa.value = Tarifa(precio12h = 0, descuentorecibo = 0, preciohoras = 0)
        _numerorecibo.value = 0
        _descuento.value = 0
        _subtotal.value = 0
        _efectivo.value = "0"
        efectivoInt = 0
        _tarjeta.value = "0"
        tarjetaInt = 0
        _transferencia.value = "0"
        transferenciaInt = 0
        _total.value = 0
    }

    fun calcularDescuento() {
        val dias = _dias.value
        var horas = _horas.value.toLong()
        var minutos = _minutos.value.toLong()
        val tarifa = _tarifa.value ?: return

        val descuentoRecibo = tarifa.descuentorecibo
        val precioHoras = tarifa.preciohoras

        if (descuentoRecibo <= 0 || precioHoras <= 0) {
            _message.value = "Los precios obtenidos no son válidos"
            return
        }

        var costoPorDias = 0.0
        var costoPorHoras = 0.0

        if (dias > 0) {
            costoPorDias = (descuentoRecibo * 2.0) * dias
        }

        if (horas > 0 || minutos > 0) {
            if (minutos >= 15) {
                horas++
                minutos = 0
            }
            if (horas >= 24) {
                val diasExtras = horas / 24
                val horasRestantes = horas % 24
                horas = horasRestantes
                // aumentamos los días
                costoPorDias += (descuentoRecibo * 2.0) * diasExtras
            }

            costoPorHoras = (precioHoras * horas).toDouble()
        }

        if (costoPorHoras > descuentoRecibo) {
            costoPorHoras = descuentoRecibo.toDouble()
        }

        val costoTotal = costoPorDias + costoPorHoras
        val descuentoAplicado = (tarifa.precio12h - tarifa.descuentorecibo).coerceAtLeast(0)
        _descuento.value = descuentoAplicado
        _subtotal.value = costoTotal.toInt()
    }



    fun limpiarSalidaImpresion() {
        _salidaImpresion.value = null
    }

    fun limpiarMensaje() {
        _message.value = null
    }
}