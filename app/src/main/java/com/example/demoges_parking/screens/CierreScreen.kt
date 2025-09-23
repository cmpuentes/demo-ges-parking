package com.example.demoges_parking.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.demoges_parking.components.MainButton
import com.example.demoges_parking.components.SpacerH
import com.example.demoges_parking.components.SpacerW
import com.example.demoges_parking.navigation.Routes
import com.example.demoges_parking.viewmodels.CierreViewModel
import com.example.demoges_parking.viewmodels.PrintViewModel
import com.example.demoges_parking.viewmodels.SessionViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CierreScreen(navController: NavController, sessionViewModel: SessionViewModel, viewModel: CierreViewModel = viewModel()){

    val printViewModel: PrintViewModel = viewModel()
    val context = LocalContext.current
    val cierre by viewModel.cierreRegistrado.collectAsState()
    val numeroturno by viewModel.numeroDeTurno.collectAsState()
    val turnoCierre by viewModel.turnoCierre.collectAsState()
    val fechainicio by viewModel.fechaDeInicio.collectAsState()
    val fechasalida by viewModel.fechaDeSalida.collectAsState()
    val empleadocierre by viewModel.empleadoCierre.collectAsState()
    val vehiculosrecibidos by viewModel.vehiculosRecibidos.collectAsState()
    val totalvehiculos by viewModel.totalVehiculos.collectAsState()
    val base by viewModel.base.collectAsState()
    val totalefectivo by viewModel.totalEfectivo.collectAsState()
    val totaltarjeta by viewModel.totalTarjeta.collectAsState()
    val totaltransferencia by viewModel.totalTransferencia.collectAsState()
    val otrosingresos by viewModel.otrsingresos.collectAsState()
    val totalabonos by viewModel.totalAbonos.collectAsState()
    val efectivoloquido by viewModel.efectivoLiquido.collectAsState()
    val totalrecaudado by viewModel.totalRecaudado.collectAsState()
    val observaciones by viewModel.observaciones.collectAsState()
    val mostrarDialogo by viewModel.mostrarDialogo.collectAsState()
    val message by viewModel.message.collectAsState()
    val sessionData by sessionViewModel.sessionData.collectAsState()
    val turnoFinalizado by sessionViewModel.turnoFinalizado.collectAsState()

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val nombreSessoion = sessionData.nombreCompleto  // ✅ CORRECTO
    val turnoSession = sessionData.turno
    val numeroTurnoSession = sessionData.numeroTurno
    val fechainicioSession = sessionData.fechaInicio

    viewModel.actualizarNumeroDeTurno(numeroTurnoSession)
    viewModel.actualizarTurnoCierre(turnoSession)
    viewModel.actualizarFechaInicio(fechainicioSession)
    viewModel.actualizarEmpleado(nombreSessoion)

    viewModel.actualizarTotalAbonos()
    viewModel.actualizarEfectivoLiquido(totalefectivo)

    val focusManager = LocalFocusManager.current

    val focusRequesterObservaciones = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val totalefectivoformateado = totalefectivo.let {
        NumberFormat.getNumberInstance(Locale.US).format(it)
    }?: "N/A"
    val totaltarjetaformateado = totaltarjeta.let {
        NumberFormat.getNumberInstance(Locale.US).format(it)
    }?: "N/A"
    val totaletransferenciaformateado = totaltransferencia.let {
        NumberFormat.getNumberInstance(Locale.US).format(it)
    }?: "N/A"
    val efectivoliquidoformateado = efectivoloquido.let {
        NumberFormat.getNumberInstance(Locale.US).format(it)
    }?: "N/A"
    val totalabonoformateado = totalabonos.let {
        NumberFormat.getNumberInstance(Locale.US).format(it)
    }?: "N/A"
    val totalrecaudadoformateado = totalrecaudado.let {
        NumberFormat.getNumberInstance(Locale.US).format(it)
    }?: "N/A"

    val cierreExitoso by viewModel.cierreExitoso.collectAsState()

    LaunchedEffect(cierreExitoso) {
        if (cierreExitoso) {
            sessionViewModel.marcarTurnoFinalizado()
        }
    }

    LaunchedEffect(cierre) {
        cierre?.let {
            printViewModel.imprimirCierre(context, it)
        }
    }

    LaunchedEffect(numeroTurnoSession) {
        if (numeroTurnoSession > 0) {
            viewModel.obtenerDatosTurno(numeroTurnoSession)
        }
    }


    // Mostrar mensajes desde el ViewModel.message
    LaunchedEffect(message) {
        val msg = message
        if (msg.isNotBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = msg,
                    duration = SnackbarDuration.Short
                )
                viewModel.limpiarMensaje() // 👈 Limpia el mensaje para no repetirlo
            }
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                title = {
                    Text("Cierre de turno", color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.CIERRE) { inclusive = true } // ✅ limpia el backstack
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ){innerpadding ->
        Column (
            modifier = Modifier.fillMaxSize()
                .padding(innerpadding)
                .padding(15.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Card(
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
            ) {
                Column (
                    modifier = Modifier.padding(4.dp)
                ){
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Número de turno: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.DarkGray
                                )
                            ) {
                                append(numeroturno.toString())
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Turno: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.DarkGray
                                )
                            ) {
                                append(turnoCierre)
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Empleado: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.DarkGray
                                )
                            ) {
                                append(empleadocierre)
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Fecha de inicio: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.DarkGray
                                )
                            ) {
                                append(fechainicio)
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Fecha de salida: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.DarkGray
                                )
                            ) {
                                append(fechasalida)
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                OutlinedTextField(
                    value = vehiculosrecibidos,
                    onValueChange = {viewModel.actualizarVehiculosRecibidos(it.trim())},
                    label = { Text("Recibidos") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    singleLine = true
                )
                SpacerW(8.dp)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                ){
                    Column (
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = "Total Vehículos: $totalvehiculos",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            SpacerH(15.dp)
            OutlinedTextField(
                value = base,
                onValueChange = {viewModel.actualizarBase(it.trim())},
                label = { Text("Base") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true
            )
            SpacerH(15.dp)
            OutlinedTextField(
                value = otrosingresos,
                onValueChange = {viewModel.actualizarOtrosIngresos(it.trim())},
                label = { Text("Otros ingresos") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true
            )
            Card (
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
            ){
                Column (
                    modifier = Modifier.padding(4.dp)
                ){
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Total efectivo: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.DarkGray
                                )
                            ) {
                                append(totalefectivoformateado)
                            }
                        },
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Total tarjerta: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.DarkGray
                                )
                            ) {
                                append(totaltarjetaformateado)
                            }
                        },
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Total transferencia: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.DarkGray
                                )
                            ) {
                                append(totaletransferenciaformateado)
                            }
                        },
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Efectivo liquido: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.DarkGray
                                )
                            ) {
                                append(efectivoliquidoformateado)
                            }
                        },
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Total abonos: ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.DarkGray
                                )
                            ) {
                                append(totalabonoformateado)
                            }
                        },
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Total recaudado: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Normal,
                            color = Color.DarkGray
                        )
                    ) {
                        append(totalrecaudadoformateado)
                    }
                },
                fontSize = 17.sp,
                color = Color.Black
            )
            SpacerH(15.dp)
            OutlinedTextField(
                value = observaciones,
                onValueChange = {viewModel.actualizarObservaciones(it)},
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(focusRequesterObservaciones),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )
            SpacerH(15.dp)
            MainButton(
                name = "Cerrar turno",
                backColor = MaterialTheme.colorScheme.primary,
                textColor = Color.White,
                onClick = {
                    viewModel.abrirDialogoConfirmacion()
                },
                enabled = !turnoFinalizado, // 🔹 Deshabilita si el turno está finalizado
                modifier = Modifier.fillMaxWidth()
            )

            if (mostrarDialogo) {
                AlertDialog(
                    onDismissRequest = { viewModel.cerrarDialogoConfirmacion() },
                    title = { Text("Confirmar cierre de turno") },
                    text = { Text("¿Está seguro de registrar el cierre del turno? Esta acción se hará solo una vez. Verifique que la información es correcta.") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.cerrarDialogoConfirmacion()
                            viewModel.registrarCierreTurno() // Aquí ya se ejecuta el registro real
                        }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.cerrarDialogoConfirmacion() }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

        }
    }
}