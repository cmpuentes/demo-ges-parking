package com.example.demoges_parking.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.demoges_parking.components.SpacerH
import com.example.demoges_parking.model.IngresoDTO
import com.example.demoges_parking.model.SalidaDTO
import com.example.demoges_parking.model.UiMessage
import com.example.demoges_parking.navigation.Routes
import com.example.demoges_parking.viewmodels.HistorialTurnoViewModel
import com.example.demoges_parking.viewmodels.PrintSalidaViewModel
import com.example.demoges_parking.viewmodels.PrintViewModel
import com.example.demoges_parking.viewmodels.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    navController: NavController,
    viewModel: HistorialTurnoViewModel = viewModel(),
    sessionViewModel: SessionViewModel
) {
    val ingresos by viewModel.ingresos.collectAsState()
    val salidas by viewModel.salidas.collectAsState()
    val totalIngresos by viewModel.totalIngresos.collectAsState()
    val totalSalidas by viewModel.totalSalidas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sessionData by sessionViewModel.sessionData.collectAsState()

    val numeroTurno = sessionData.numeroTurno
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTabIndex by remember { mutableStateOf(0) } // 0 = Ingresos, 1 = Salidas
    val tabTitles = listOf("Ingresos", "Salidas")

    // Estado del filtro
    var searchQuery by remember { mutableStateOf("") }

    // Filtrado dinámico según el tab
    val filteredIngresos = ingresos.filter {
        it.placa.contains(searchQuery, ignoreCase = true)
    }

    val filteredSalidas = salidas.filter {
        it.placa.contains(searchQuery, ignoreCase = true)
    }

    // Cargar historial al iniciar
    LaunchedEffect(numeroTurno) {
        if (numeroTurno != 0) {
            viewModel.obtenerHistorial(numeroTurno)
        }
    }

    // Escuchar mensajes del ViewModel
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                title = { Text("Historial Turno #$numeroTurno", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HISTORIAL) { inclusive = true }
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
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

            // Tabs superiores
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            searchQuery = "" // limpiar búsqueda al cambiar de tab
                        },
                        text = { Text(title) }
                    )
                }
            }

            // Campo de búsqueda
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it.uppercase() },
                label = { Text("Buscar por placa") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Contenido según tab
            when (selectedTabIndex) {
                0 -> {
                    // INGRESOS
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 32.dp)
                        )
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Text(
                                    text = "Total ingresos: ${filteredIngresos.size}",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredIngresos) { ingreso ->
                                    IngresoItem(ingreso)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // SALIDAS
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 32.dp)
                        )
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Text(
                                    text = "Total salidas: ${filteredSalidas.size}",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredSalidas.filterNotNull()) { salida ->
                                    SalidaItem(salida)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IngresoItem(ingreso:IngresoDTO, printViewModel: PrintViewModel = viewModel(), viewModel: HistorialTurnoViewModel = viewModel()){

    val context = LocalContext.current

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),

        ){
        Box (modifier = Modifier.fillMaxWidth()){
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Placa: ${ingreso.placa}", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                SpacerH(4.dp)
                Text("Tipo: ${ingreso.tipoVehiculo}", color = Color.Black)
                Text("Servicio: ${ingreso.tipoServicio}", color = Color.Black)
                Text("Fecha ingreso: ${ingreso.fechaIngreso}", color = Color.Black)
                Text("Zona: ${ingreso.zona}", color = Color.Black)
            }

            // Icono de impresión arriba a la derecha
            IconButton(
                onClick = {
                    viewModel.imprimirIngresoPorPlaca(
                        context,
                        ingreso.placa,
                        printViewModel
                    )
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = "Imprimir ingreso",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun SalidaItem(salida:SalidaDTO, printSalidaViewModel: PrintSalidaViewModel = viewModel(), viewModel: HistorialTurnoViewModel = viewModel(), sessionViewModel: SessionViewModel= viewModel()){

    val context = LocalContext.current
    val idsalida = salida.idsalida
    val sesionData by sessionViewModel.sessionData.collectAsState()
    val nombre = sesionData.nombreCompleto

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            // Contenido principal
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Placa: ${salida.placa}", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                SpacerH(4.dp)
                Text("Tipo: ${salida.tipoVehiculo}", color = Color.Black)
                Text("Servicio: ${salida.tipoServicio}", color = Color.Black)
                Text("Fecha salida: ${salida.fechaSalida}", color = Color.Black)
                Text("Valor: $${salida.total}", color = Color.Black)
            }

            // Icono de impresión arriba a la derecha
            IconButton(
                onClick = {
                    viewModel.imprimirSalidaPorPlaca(context, idsalida, nombre, printSalidaViewModel)
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = "Imprimir ingreso",
                    tint = Color.Black
                )
            }
        }
    }
}
