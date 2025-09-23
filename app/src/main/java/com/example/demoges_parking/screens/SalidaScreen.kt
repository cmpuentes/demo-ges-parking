package com.example.demoges_parking.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.demoges_parking.navigation.Routes
import com.example.demoges_parking.viewmodels.PrintSalidaViewModel
import com.example.demoges_parking.viewmodels.SalidaViewModel
import com.example.demoges_parking.viewmodels.SessionViewModel
import kotlinx.coroutines.launch

import java.text.NumberFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SalidaScreen(navController: NavController, sessionViewModel: SessionViewModel, viewModel: SalidaViewModel = viewModel(), printSalidaViewModel: PrintSalidaViewModel = viewModel()){

    val placa by viewModel.placa.collectAsState()
    val tipoVehiculo by viewModel.tipoVehiculo.collectAsState()
    val tipoServicio by viewModel.tipoServicio.collectAsState()
    val fechaIngreso by viewModel.fechaIngreso.collectAsState()
    val fechaSalida by viewModel.fechaSalida.collectAsState()
    val dias by viewModel.dias.collectAsState()
    val horas by viewModel.horas.collectAsState()
    val minutos by viewModel.minutos.collectAsState()
    val costoTotal by viewModel.costoTotal.collectAsState()
    val tarifa by viewModel.tarifa.collectAsState()
    val descuento by viewModel.descuento.collectAsState()
    val efectivo by viewModel.efectivo.collectAsState()
    val tarjeta by viewModel.tarjeta.collectAsState()
    val transferencia by viewModel.transferencia.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val numerorecibo by viewModel.numerorecibo.collectAsState()
    val total by viewModel.total.collectAsState()
    val message by viewModel.message.collectAsState()
    val sessionData by sessionViewModel.sessionData.collectAsState()
    val salidaImpresion by viewModel.salidaImpresion.collectAsState()
    val turnoFinalizado by sessionViewModel.turnoFinalizado.collectAsState()

    val mostrarDialogo by viewModel.mostrarDialogo.collectAsState()

    val printViewModel = remember { PrintSalidaViewModel() }
    val context = LocalContext.current

    //Colocar las variables de session para actualizar las de salida que faltan
    val nombreSessoion = sessionData.nombreCompleto // ✅ CORRECTO
    val turnoSession = sessionData.turno
    val numeroTurnoSession = sessionData.numeroTurno
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // FocusRequesters
    val focusManager = LocalFocusManager.current

    viewModel.actualizarNumeroTurnoSalida(numeroTurnoSession)
    viewModel.actualizarEmpleadoSalida(nombreSessoion)
    viewModel.actuelizarTUrnoSalida(turnoSession)

    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val precio12Formateado = tarifa?.precio12h?.let {
        NumberFormat.getNumberInstance(Locale.US).format(it)
    } ?: "N/A"

    val precioHorasFormateado = tarifa?.preciohoras?.let {
        NumberFormat.getNumberInstance(Locale.US).format(it)
    } ?: "N/A"

//    val costototalFormateado = costoTotal.let {
//        NumberFormat.getNumberInstance(Locale.US).format(it)
//    }?: "N/A"

    val subtotalFormateado = subtotal.let {
        NumberFormat.getNumberInstance(Locale.US).format(it)
    }?: "N/A"

    val totalFormateado = total.let {
        NumberFormat.getNumberInstance(Locale.US).format(it)
    }?: "N/A"

    LaunchedEffect(Unit) {
        viewModel.eventoSalida.collect { salida ->
            salida?.let {
                // disparar el flag en el ViewModel para mostrar el diálogo
                viewModel.mostrarDialogo()
            }
        }
    }

    // Escuchar cambios de mensajes
    LaunchedEffect(message) {
        message?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.limpiarMensaje()
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
                    Text("Salida de vehículo", color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SALIDA) { inclusive = true } // ✅ limpia el backstack
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
    ){ innerPadding ->
        Column (
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState) // 👈 esto habilita el scroll
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            OutlinedTextField(
                value = placa,
                onValueChange = {input ->
                    val placaFormateada = input.replace(" ", "").uppercase()
                    viewModel.actulizarPlaca(placaFormateada)
                                },
                label = { Text("Placa") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (placa.isNotBlank()) {
                            viewModel.consultarSalidaPorPlaca(placa)
                            keyboardController?.hide()
                        }
                    }
                )
            )
            SpacerH(20.dp)
            Card(
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column (
                    modifier = Modifier.padding(3.dp)
                ){
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                                append("Tipo de vehiculo: ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray)) {
                                append(tipoVehiculo)
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    SpacerH(4.dp)
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                                append("Tipo de servicio: ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray)) {
                                append(tipoServicio)
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    SpacerH(4.dp)
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                                append("Fecha ingreso: ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray)) {
                                append(fechaIngreso)
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    SpacerH(4.dp)
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                                append("Fecha salida: ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray)) {
                                append(fechaSalida)
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }
            }
            SpacerH(15.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Text("Días: $dias", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Horas: $horas", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Minutos: $minutos", fontWeight = FontWeight.Bold, color = Color.Black)
            }
            SpacerH(10.dp)
            Card(
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.padding(5.dp)
                    .align(Alignment.Start) // ✅ Este es el truco
            ) {
                Text("Costo : $$costoTotal", fontWeight = FontWeight.Bold, color = Color.Black)
            }
            SpacerH(20.dp)
            Card(
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column (
                    modifier = Modifier.padding(4.dp)
                ){
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                                append("Precio 12 horas: ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray)) {
                                append("$ $precio12Formateado")
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    SpacerH(4.dp)
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                                append("Precio hora: ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray)) {
                                append("$ $precioHorasFormateado")
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }
            }
            SpacerH(15.dp)
            OutlinedTextField(
                value = numerorecibo.toString(),
                onValueChange = { input ->
                    // Validamos que el input sea numérico (opcional, pero el teclado ya ayuda)
                    if (input.all { it.isDigit() } || input.isEmpty()) {
                        viewModel.actualizarNumeroRecibo(input)
                    }
                },
                label = {Text("Número de recibo")},
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number, // Solo teclado numérico
                    imeAction = ImeAction.Done
                ),
            )
            SpacerH(15.dp)
            Text("Descuento: $$descuento")
            SpacerH(15.dp)
            Card(
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.padding(5.dp)
                    .align(Alignment.Start) // ✅ Este es el truco
            ) {
                Text("Subtotal : $$subtotalFormateado", fontWeight = FontWeight.Bold, color = Color.Black)
            }

            SpacerH(15.dp)

            OutlinedTextField(
                value = efectivo,
                onValueChange = {
                    viewModel.actualizarEfectivo(it.trim())
                    viewModel.actualizarTotal()
                },
                label = { Text("Efectivo") },
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
                value = tarjeta,
                onValueChange = {
                    viewModel.actualizarTarjeta(it.trim())
                    viewModel.actualizarTotal()
                },
                label = { Text("Tarjeta") },
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
                value = transferencia,
                onValueChange = {
                    viewModel.actualizarTransferencia(it.trim())
                    viewModel.actualizarTotal()
                },
                label = { Text("Transferencia") },
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

            Card(
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.padding(5.dp)
                    .align(Alignment.Start) // ✅ Este es el truco
            ) {
                Text("Total: $$totalFormateado", fontWeight = FontWeight.Bold, color = Color.Black)
            }
            SpacerH(15.dp)
            MainButton(
                name = "Salida de vehículo",
                backColor = MaterialTheme.colorScheme.primary,
                textColor = Color.White,
                onClick = {
                    if(total == subtotal){
                        //Se hace el registro en la bd
                        viewModel.registrarSalida()
                        viewModel.limpiarCampos()
                    }else{
                        viewModel.actualizarMessage("Los valores ingresados no coinciden con el subtotal.")
                    }
                },
                enabled = !turnoFinalizado,
                modifier = Modifier.fillMaxWidth()
            )
            SpacerH(15.dp)

        }

        if (mostrarDialogo && salidaImpresion != null) {
            val salida = salidaImpresion  // copia local estable
            AlertDialog(
                onDismissRequest = { viewModel.cerrarDialogo() },
                title = { Text("Salida registrada") },
                text = { Text("¿Desea imprimir la tirilla?") },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            salida?.let {
                                printViewModel.imprimirReciboSalida(
                                    context = context,
                                    data = it, // aquí usa el mismo tipo SalidaResponseDTO?
                                    nombreCompleto = sessionData.nombreCompleto
                                )
                            }
                        }
                        viewModel.cerrarDialogo()
                        viewModel.limpiarSalidaImpresion() // <- importante: evita datos persistentes
                    }) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.cerrarDialogo() }) {
                        Text("No")
                    }
                }
            )
        }
    }
}