package com.gesnnova.demoges_parking.viewmodels

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gesnnova.demoges_parking.model.CierreData
import com.gesnnova.demoges_parking.model.IngresoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class PrintViewModel: ViewModel(){

    var status by mutableStateOf("Listo para imprimir")
        private set

    // 1 UUID único y correcto
    //private val sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission")
    fun printTest(context: Context) = viewModelScope.launch {
        val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        // Usa MAC directamente para evitar depender del nombre
        val mac = "10:22:33:69:52:81" // <-- CAMBIA a la MAC real de tu impresora
        val printer = try {
            adapter.getRemoteDevice(mac)
        } catch (e: IllegalArgumentException) {
            updateStatus("❌ MAC inválida o impresora no emparejada")
            return@launch
        }

        updateStatus("Conectando a impresora…")

        withContext(Dispatchers.IO) {
            adapter.cancelDiscovery()

            // 1. Canal RFCOMM "inseguro"
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            val socket = try {
                printer.createInsecureRfcommSocketToServiceRecord(uuid)
            } catch (e: Exception) {
                updateStatus("❌ Error al crear socket: ${e.message}")
                return@withContext
            }

            try {
                socket.connect()

                // 2. Comandos ESC/POS + texto + salto
                val escInit = byteArrayOf(0x1B, 0x40) // ESC @
                val text = "Hola desde PT-210, lo logramos CARAJOOOOO"
                val data = text.toByteArray(Charsets.ISO_8859_1)
                val feed = byteArrayOf(0x0A, 0x0D)   // LF + CR

                socket.outputStream.use { os ->
                    os.write(escInit)
                    os.write(data)
                    os.write(feed)
                    os.flush()
                    Thread.sleep(200) // Espera para que se imprima completo
                }

                updateStatus("✅ Mensaje enviado")
            } catch (e: IOException) {
                updateStatus("❌ No se pudo conectar: ${e.message}")
            } finally {
                try {
                    socket.close()
                } catch (_: IOException) {}
            }
        }
    }


    private fun updateStatus(newStatus: String) {
        status = newStatus
        Log.d("BluetoothPrint", newStatus)
    }

    @SuppressLint("MissingPermission")
    fun imprimirIngreso(context: Context, data: IngresoData) = viewModelScope.launch {
        val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        val mac = "10:22:33:69:52:81" // <- tu MAC real

        val printer = try {
            adapter.getRemoteDevice(mac)
        } catch (e: IllegalArgumentException) {
            updateStatus("❌ MAC inválida o impresora no emparejada")
            return@launch
        }

        updateStatus("Conectando a impresora…")

        withContext(Dispatchers.IO) {
            adapter.cancelDiscovery()

            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            val socket = try {
                printer.createInsecureRfcommSocketToServiceRecord(uuid)
            } catch (e: Exception) {
                updateStatus("❌ Error al crear socket: ${e.message}")
                return@withContext
            }

            try {
                socket.connect()

                val escInit = byteArrayOf(0x1B, 0x40) // ESC @

                val texto = buildString {
                    appendLine("        GES-PARKING - INGRESO")
                    appendLine("COMBUGAS S.A.S")
                    appendLine("NIT: 900139412-4")
                    appendLine("TEL:3205417916")
                    appendLine(" CARTAGENA")
                    appendLine("Responsable: ${data.empleado}") // <-- Aquí va el nombre de sesión
                    appendLine("-----------------------------")
                    appendLine("Placa: ${data.placa}")
                    appendLine("Tipo Vehículo: ${data.tipoVehiculo}")
                    appendLine("Cliente: ${data.cliente}")
                    appendLine("-----------------------------")
                    appendLine("Ingreso: ${data.fechaIngreso}")
                    appendLine("Zona: ${data.zona}")
                    appendLine("Turno: ${data.numeroTurno}")
                    appendLine("-----------------------------")
                    appendLine("Fabricante Software: GESNNOVA")
                    appendLine("NIT:901102506-1")
                    appendLine("https://www.grupogesnnova.com")
                    appendLine("     ¡Bienvenido!")
                    appendLine("     ")
                    appendLine("     ")
                    appendLine("     ")
                }

                val dataBytes = texto.toByteArray(Charsets.ISO_8859_1)
                val feed = byteArrayOf(0x0A, 0x0D)

                socket.outputStream.use { os ->
                    os.write(escInit)
                    os.write(dataBytes)
                    os.write(feed)
                    os.flush()
                    Thread.sleep(200)
                }

                updateStatus("✅ Ticket enviado")
            } catch (e: IOException) {
                updateStatus("❌ Falló la conexión: ${e.message}")
            } finally {
                try {
                    socket.close()
                } catch (_: IOException) {}
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun imprimirCierre(context: Context,
                       data: CierreData,
                       efectivoAbono: Int,
                       tarjetaAbono: Int,
                       transferenciaAbono: Int
                       ) = viewModelScope.launch {
        val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        val mac = "10:22:33:69:52:81" // <-- cambia a la MAC real de tu impresora

        val printer = try {
            adapter.getRemoteDevice(mac)
        } catch (e: IllegalArgumentException) {
            updateStatus("❌ MAC inválida o impresora no emparejada")
            return@launch
        }

        updateStatus("Conectando a impresora…")

        withContext(Dispatchers.IO) {
            adapter.cancelDiscovery()

            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            val socket = try {
                printer.createInsecureRfcommSocketToServiceRecord(uuid)
            } catch (e: Exception) {
                updateStatus("❌ Error al crear socket: ${e.message}")
                return@withContext
            }

            try {
                socket.connect()

                val escInit = byteArrayOf(0x1B, 0x40) // ESC @ (inicializar impresora)

                val texto = buildString {
                    appendLine("     GES-PARKING - CIERRE")
                    appendLine("COMBUGAS S.A.S")
                    appendLine("NIT: 900139412-4")
                    appendLine("DG 31D N.32A-25B.TERNERA")
                    appendLine("TEL: 3205417916")
                    appendLine("CARTAGENA")
                    appendLine("")
                    appendLine("-----------------------------")
                    appendLine("Turno: ${data.turno}")
                    appendLine("Numero turno: ${data.numeroturno}")
                    appendLine("Empleado: ${data.empleado}")
                    appendLine("Fecha ingreso: ${data.fechaingreso}")
                    appendLine("Fecha salida: ${data.fechasalida}")
                    appendLine("-----------------------------")
                    appendLine("Total vehiculos: ${data.totalvehiculos}")
                    appendLine("Efectivo: ${data.efectivo}")
                    appendLine("Tarjeta: ${data.tarjeta}")
                    appendLine("Transferencia: ${data.transferencia}")
                    appendLine("Otros ingresos: ${data.otrosingresos}")
                    appendLine("----- ABONOS -----")
                    appendLine("Efectivo abono: $efectivoAbono")
                    appendLine("Tarjeta abono: $tarjetaAbono")
                    appendLine("Transferencia abono: $transferenciaAbono")
                    appendLine("Total abonos: ${data.totalabonos}")
                    appendLine("-----------------------------")
                    appendLine("Efectivo líquido: ${data.efectivoliquido}")
                    appendLine("TOTAL RECAUDADO: ${data.totalrecaudado}")
                    appendLine("-----------------------------")
                    appendLine("Observaciones: ${data.observaciones}")
                    appendLine("-----------------------------")
                    appendLine("Fabricante Software: GESNNOVA")
                    appendLine("NIT: 901102506-1")
                    appendLine("www.grupogesnnova.com")
                    appendLine("     ¡Hasta pronto!")
                    appendLine("     ")
                    appendLine("     ")
                    appendLine("     ")
                }

                val dataBytes = texto.toByteArray(Charsets.ISO_8859_1)
                val feed = byteArrayOf(0x0A, 0x0D) // salto de línea

                socket.outputStream.use { os ->
                    os.write(escInit)
                    os.write(dataBytes)
                    os.write(feed)
                    os.flush()
                    Thread.sleep(200) // darle tiempo a la impresora
                }

                updateStatus("✅ Ticket de cierre enviado")
            } catch (e: IOException) {
                updateStatus("❌ Falló la conexión: ${e.message}")
            } finally {
                try {
                    socket.close()
                } catch (_: IOException) {}
            }
        }
    }

}