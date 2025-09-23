package com.example.demoges_parking.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.demoges_parking.components.InfoRow
import com.example.demoges_parking.components.SpacerH
import com.example.demoges_parking.navigation.Routes
import com.example.demoges_parking.network.ApiClient
import com.example.demoges_parking.viewmodels.PrintViewModel
import com.example.demoges_parking.viewmodels.ResumenTurnoViewModel
import com.example.demoges_parking.viewmodels.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenTurnoScreen(navController: NavController, sessionViewModel: SessionViewModel){

    val printViewModel: PrintViewModel = viewModel()
    val context = LocalContext.current
    val apiService = ApiClient.apiService
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ResumenTurnoViewModel(apiService) as T
        }
    }
    val viewModel: ResumenTurnoViewModel = viewModel(factory = factory)

    val resumen by viewModel.resumen.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val cierreParaImprimir by viewModel.cierreParaImprimir.collectAsState()

    val sesion by sessionViewModel.sessionData.collectAsState()
    val turno = sesion.numeroTurno

    // Imprimir automáticamente cuando hay datos
    LaunchedEffect(cierreParaImprimir) {
        cierreParaImprimir?.let { cierre ->
            printViewModel.imprimirCierre(context, cierre)
            viewModel.clearCierreParaImprimir()
        }
    }

    // Llama al backend solo una vez
    LaunchedEffect(turno) {
        viewModel.obtenerResumen(turno)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.Black
                ),
                title = { Text("Resumen del Turno") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Routes.HOME) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.obtenerCierreParaImprimir(turno)
                    }) {
                        Icon(Icons.Default.Print, contentDescription = "Imprimir")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator()
            } else {
                resumen?.let {
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(40.dp)
                            )

                            SpacerH(16.dp)

                            InfoRow("Empleado", sesion.nombreCompleto)
                            InfoRow("Turno", sesion.turno)
                            InfoRow("N° Turno", sesion.numeroTurno.toString())
                            InfoRow("Inicio", sesion.fechaInicio)

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            InfoRow("Vehículos salieron", it.vehiculosSalida.toString())
                            InfoRow("Total efectivo", "$${it.efectivo}")
                            InfoRow("Total tarjeta", "$${it.tarjeta}")
                            InfoRow("Total transferencia", "$${it.transferencia}")
                            InfoRow("Total recaudado", "$${it.totalRecaudado}")
                        }
                    }
                } ?: Text("No se encontró información.")
            }

            mensaje?.let {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { viewModel.limpiarMensaje() }) {
                            Text("Cerrar")
                        }
                    }
                ) {
                    Text(it)
                }
            }
        }
    }
}