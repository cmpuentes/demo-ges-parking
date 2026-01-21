package com.gesnnova.demoges_parking.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gesnnova.demoges_parking.components.InfoRow
import com.gesnnova.demoges_parking.components.SpacerH
import com.gesnnova.demoges_parking.components.SpacerW
import com.gesnnova.demoges_parking.model.UiMessage
import com.gesnnova.demoges_parking.viewmodels.ConsultaVehiculoViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavController
import com.gesnnova.demoges_parking.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultaVehiculoScreen(navController: NavController, viewModel: ConsultaVehiculoViewModel = viewModel()){

    val placa by viewModel.placa.collectAsState()
    val consultaResponse by viewModel.consultaResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showMessageDialog by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope() // << Necesario para el showSnackbar
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current

    // Captura los mensajes
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            when (msg) {
                is UiMessage.Error -> showMessageDialog = msg.message
                is UiMessage.Success -> showMessageDialog = msg.message
            }
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Consultar vehículo", color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.CONSULTAR_VEHICULO_PARQUEADERO) { inclusive = true } // ✅ limpia el backstack
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
    ){ padding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ){
            // Campo de placa con ícono
            OutlinedTextField(
                value = placa,
                onValueChange = { viewModel.actualizarPlaca(it.uppercase()) },
                label = { Text("Placa del vehículo") },
                leadingIcon = {
                    Icon(Icons.Default.DirectionsCar, contentDescription = "Placa")
                },
                trailingIcon = { // 👈 Lupa para buscar
                    IconButton(onClick = {
                        if (placa.isNotBlank()) {
                            viewModel.consultarVehiculo()
                            focusManager.clearFocus()
                        } else {
                            showMessageDialog = "Ingresa una placa"
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (placa.isNotBlank()) {
                            viewModel.consultarVehiculo()
                            focusManager.clearFocus()
                        } else {
                            showMessageDialog = "Ingresa una placa"
                        }
                    }
                )
            )

            SpacerH(16.dp)

            if(isLoading){
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            // Mostrar resultados si existen
            consultaResponse?.let { data ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        // Ícono y Placa grande
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = "Placa", tint = Color.Black)
                            SpacerW(8.dp)
                            Text("Placa: ${data.placa}", style = MaterialTheme.typography.titleLarge, color = Color.Black)
                        }

                        SpacerH(8.dp)

                        InfoRow(label = "Fecha ingreso", value = data.fechaIngreso)
                        InfoRow(label = "Cliente", value = data.cliente)
                        InfoRow(label = "Tipo Vehículo", value = data.tipoVehiculo)
                        InfoRow(label = "Tipo Servicio", value = data.tipoServicio)
                        InfoRow(label = "Número Turno", value = data.numeroTurno.toString())
                        InfoRow(label = "Tiempo", value = "${data.dias}d ${data.horas}h ${data.minutos}m")
                        InfoRow(label = "Valor", value = "$${data.valor}")
                        InfoRow(label = "Empleado", value = data.empleado)
                    }
                }
            }
        }
        if (showMessageDialog != null) {
            AlertDialog(
                onDismissRequest = { showMessageDialog = null },
                title = { Text("Aviso") },
                text = { Text(showMessageDialog ?: "") },
                confirmButton = {
                    TextButton(onClick = { showMessageDialog = null }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}
