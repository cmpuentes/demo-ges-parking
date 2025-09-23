package com.example.demoges_parking.viewmodels

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.demoges_parking.model.SalidaResponseDTO

//import com.example.demoges_parking.model.SalidaResponsePrint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class PrintSalidaViewModel: ViewModel() {

    var status by mutableStateOf("Listo para imprimir")
        private set

    private fun updateStatus(newStatus: String) {
        status = newStatus
        Log.d("BluetoothPrintSalida", newStatus)
    }

    @SuppressLint("MissingPermission")
    suspend fun imprimirReciboSalida(
        context: Context,
        data: SalidaResponseDTO,
        nombreCompleto: String
    ) {
        val adapter =
            (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        val mac = "10:22:33:69:52:81" // <- tu MAC real

        val printer = try {
            adapter.getRemoteDevice(mac)
        } catch (e: IllegalArgumentException) {
            updateStatus("❌ MAC inválida o impresora no emparejada")
            return
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
                    appendLine("        Ges-parking - Salida")
                    appendLine("COMBUGAS S.A.S")
                    appendLine("NIT: 900139412-4")
                    appendLine("TEL:3205417916")
                    appendLine(" CARTAGENA")
                    appendLine("Responsable: $nombreCompleto") // <-- Aquí va el nombre de sesión
                    appendLine("-----------------------------")
                    appendLine("Placa: ${data.placa}")
                    appendLine("Tipo Vehículo: ${data.tipovehiculo}")
                    appendLine("Tipo Servicio: ${data.tiposervicio}")
                    appendLine("-----------------------------")
                    appendLine("Ingreso: ${data.fechaentrada}")
                    appendLine("Salida: ${data.fechasalida}")
                    appendLine("Tiempo: ${data.dias}d ${data.horas}h ${data.minutos}m")
                    appendLine("Valor: $${data.valor}")
                    appendLine("Descuento: $${data.descuento}")
                    appendLine("TOTAL: $${data.total}")
                    if (data.efectivo > 0) appendLine("Efectivo: $${data.efectivo}")
                    if (data.tarjeta > 0) appendLine("Tarjeta: $${data.tarjeta}")
                    if (data.transferencia > 0) appendLine("Transferencia: $${data.transferencia}")
                    appendLine("-----------------------------")
                    appendLine("Fabricante Software: GESNNOVA")
                    appendLine("NIT:901102506-1")
                    appendLine("https://www.grupogesnnova.com")
                    appendLine("     ¡Gracias por su visita!")
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
                } catch (_: IOException) {
                }
            }
        }
    }
}