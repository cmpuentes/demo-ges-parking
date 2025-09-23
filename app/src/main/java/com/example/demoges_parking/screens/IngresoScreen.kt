package com.example.demoges_parking.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.demoges_parking.components.MainButton
import com.example.demoges_parking.components.SpacerH
import com.example.demoges_parking.components.SpacerW
import com.example.demoges_parking.model.UiMessage
import com.example.demoges_parking.navigation.Routes
import com.example.demoges_parking.network.ApiClient
import com.example.demoges_parking.network.ApiService
import com.example.demoges_parking.utils.obtenerFechaActual
import com.example.demoges_parking.viewmodels.IngresoVieModel
import com.example.demoges_parking.viewmodels.PrintViewModel
import com.example.demoges_parking.viewmodels.SessionViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun IngresoScreen(navController: NavController, sessionViewModel: SessionViewModel, printViewModel: PrintViewModel = viewModel()) {

    val context = LocalContext.current

    val ingresoViewModel: IngresoVieModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return IngresoVieModel(
                    api = ApiClient.apiService,
                    application = context.applicationContext as Application
                ) as T
            }
        }
    )

    val ingresoRequest by ingresoViewModel.ingresoRequest.collectAsState()
    val sessionData by sessionViewModel.sessionData.collectAsState()

    var expandedTipoVehiculo by remember { mutableStateOf(false) }
    val opcionesVehiculo = ingresoViewModel.tiposVehiculo.collectAsState().value

    var expandedTipoServicio by remember { mutableStateOf(false) }
    val opcionesServicio = ingresoViewModel.tiposServicio.collectAsState().value

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequesterObservaciones = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val data by ingresoViewModel.dataIngresada.collectAsState()
    val turnoFinalizado by sessionViewModel.turnoFinalizado.collectAsState()

    val mostrarDialogo by ingresoViewModel.mostrarDialogo.collectAsState()

    ingresoViewModel.actualizarTurno(sessionData.turno)
    ingresoViewModel.actualizarNumeroTurno(sessionData.numeroTurno)
    ingresoViewModel.actualizarEmpleado(sessionData.nombreCompleto)
    
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val hasScanPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED

            val hasConnectPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasScanPermission || !hasConnectPermission) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(
                        android.Manifest.permission.BLUETOOTH_SCAN,
                        android.Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    1001
                )
            }
        }
    }


    LaunchedEffect(Unit) {
        ingresoViewModel.cargarTiposVehiculo()
        ingresoViewModel.cargarTipoServicio()
    }

    LaunchedEffect(ingresoRequest.cliente) {
        Log.d("IngresoScreen", "Cliente actualizado: ${ingresoRequest.cliente}")
    }

    LaunchedEffect(Unit) {
        ingresoViewModel.uiMessage.collect { msg ->
            when (msg) {
                is UiMessage.Error -> {
                    snackbarHostState.showSnackbar(msg.message)
                }

                is UiMessage.Success -> {
                    ingresoViewModel.mostrarDialogo()

                    // Imprimir si hay datos listos
                    try {
                        data?.let { printViewModel.imprimirIngreso(context, it) }
                    } catch (e: Exception) {
                        Log.e("Impresión", "Error al imprimir: ${e.message}")
                        snackbarHostState.showSnackbar("No se pudo imprimir")
                    }
                }
            }
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                title = {
                    Text("Ingreso de vehículo", color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.HOME){
                            popUpTo(Routes.INGRESO){inclusive = true}
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        //Aquí va el snackbar
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ){ innerPadding ->
        Column (
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            OutlinedTextField(
                value = ingresoRequest.placa,
                onValueChange = { input ->
                    // Eliminar todos los espacios
                    val placaSinEspacios = input.replace(" ", "").uppercase()
                    ingresoViewModel.actualizarPlaca(placaSinEspacios)

                    if (placaSinEspacios.length >= 6) { // o 5 según tu criterio
                        ingresoViewModel.consultarClientePrepago(placaSinEspacios)
                    }
                },
                label = {Text("Placa")},
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )

            SpacerH(20.dp)

            ExposedDropdownMenuBox(
                expanded = expandedTipoVehiculo,
                onExpandedChange = {
                    expandedTipoVehiculo = !expandedTipoVehiculo
                }
            ) {
                OutlinedTextField(
                    value = ingresoRequest.tipovehiculo,
                    onValueChange = {ingresoViewModel.actualizarTipoVehiculo(it)},
                    label = {Text("Seleccionar tipo de vehículo")},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedTipoVehiculo
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedTipoVehiculo,
                    onDismissRequest = {expandedTipoVehiculo = false}
                ) {
                    opcionesVehiculo.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                ingresoViewModel.actualizarTipoVehiculo(opcion)
                                expandedTipoVehiculo = false
                            }
                        )
                    }
                }
            }

            SpacerH(20.dp)

            ExposedDropdownMenuBox(
                expanded = expandedTipoServicio,
                onExpandedChange = {expandedTipoServicio = !expandedTipoServicio}
            ) {
                OutlinedTextField(
                    value = ingresoRequest.tiposervicio,
                    onValueChange = {ingresoViewModel.actualizarTipoServicio(it)},
                    label = {Text("Seleccionar tipo de servicio")},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedTipoServicio
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedTipoServicio,
                    onDismissRequest = {
                        expandedTipoServicio = false
                    }
                ) {
                    opcionesServicio.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                ingresoViewModel.actualizarTipoServicio(opcion)
                                expandedTipoServicio = false
                            }
                        )
                    }
                }
            }

            SpacerH(20.dp)

            OutlinedTextField(
                value = ingresoRequest.cliente,
                onValueChange = {},
                label = {Text("Cliente")},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            SpacerH(20.dp)

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                OutlinedTextField(
                    value = ingresoRequest.zona,
                    onValueChange = {ingresoViewModel.actualizarZona(it)},
                    label = {Text("Zona")},
                    modifier = Modifier
                        .weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusRequesterObservaciones.requestFocus()
                        }
                    )
                )

                SpacerW(8.dp)

                Card (
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
                            text = "Estado: Activo",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Turno: ${sessionData.numeroTurno}",
                            fontSize = 13.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            SpacerH(20.dp)

            OutlinedTextField(
                value = ingresoRequest.observaciones,
                onValueChange = {ingresoViewModel.actualizarObservaciones(it)},
                label = { Text("Observaciones") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterObservaciones),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )

            SpacerH(20.dp)

            MainButton(
                name = "Ingresar vehículo",
                backColor = MaterialTheme.colorScheme.primary,
                textColor = Color.White,
                onClick = {
                    val fecha = obtenerFechaActual()
                    ingresoViewModel.actualizarFechaIngreso(fecha)
                    ingresoViewModel.registrarIngreso()
                },
                enabled = !turnoFinalizado,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (mostrarDialogo && data != null) {
            AlertDialog(
                onDismissRequest = { ingresoViewModel.cerrarDialogo() },
                title = { Text("Ingreso exitoso") },
                text = { Text("¿Desea imprimir la tirilla?") },
                confirmButton = {
                    TextButton(onClick = {
                        printViewModel.imprimirIngreso(context, data)
                        ingresoViewModel.cerrarDialogo()
                    }) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { ingresoViewModel.cerrarDialogo() }) {
                        Text("No")
                    }
                }
            )
        }

    }
}