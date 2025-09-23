package com.example.demoges_parking.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.demoges_parking.components.SpacerH
import com.example.demoges_parking.model.UiMessage
import com.example.demoges_parking.model.VehiculoActivoDTO
import com.example.demoges_parking.viewmodels.VehiculosActivosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiculosActivosScreen(navController: NavController, viewModel: VehiculosActivosViewModel = viewModel()) {

    val query by viewModel.query.collectAsState()
    val vehiculos by viewModel.vehiculosFiltrados.collectAsState()
    val total by viewModel.total.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    // Cargar vehículos al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarVehiculosActivos()
    }

    // Escuchar mensajes de error
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            when (msg) {
                is UiMessage.Error -> snackbarHostState.showSnackbar(msg.message)
                is UiMessage.Success -> snackbarHostState.showSnackbar(msg.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("Vehículos en parqueadero") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 32.dp)
                )
            } else {
                // Card con total
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Text(
                        text = "Total vehículos activos: $total",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // 🔎 Campo de búsqueda
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.actualizarQuery(it) },
                    label = { Text("Buscar placa") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                // Lista de vehículos
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(vehiculos) { vehiculo ->
                        VehiculoActivoItem(vehiculo)
                    }
                }
            }
        }
    }
}

@Composable
fun VehiculoActivoItem(vehiculo: VehiculoActivoDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Placa: ${vehiculo.placa}", style = MaterialTheme.typography.titleMedium, color = Color.Black)
            SpacerH(4.dp)
            Text("Tipo: ${vehiculo.tipoVehiculo}", color = Color.Black)
            Text("Servicio: ${vehiculo.tipoServicio}", color = Color.Black)
            Text("Fecha ingreso: ${vehiculo.fechaIngreso}", color = Color.Black)
            Text("Zona: ${vehiculo.zona}", color = Color.Black)
        }
    }
}
