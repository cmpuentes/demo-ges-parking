package com.gesnnova.demoges_parking.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ChecklistRtl
import androidx.compose.material.icons.rounded.ContentPasteSearch
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gesnnova.demoges_parking.R
import com.gesnnova.demoges_parking.components.MainButton
import com.gesnnova.demoges_parking.components.SpacerH
import com.gesnnova.demoges_parking.navigation.Routes
import com.gesnnova.demoges_parking.viewmodels.SessionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel = viewModel() // pasar la misma instancia
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    navController = navController,
                    drawerState = drawerState,
                    scope = scope,
                    sessionViewModel = sessionViewModel // <-- pasar aquí
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(onOpenDrawer = {
                    scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    }
                })
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(8.dp)) {
                ScreenContent(sessionViewModel = sessionViewModel)
                SpacerH(10.dp)
                MainButton(
                    name = "Ingreso de vehículo",
                    backColor = MaterialTheme.colorScheme.primary,
                    textColor = Color.White,
                    onClick = { navController.navigate(Routes.INGRESO) },
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                        .size(50.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 18.sp, // 👈 Tamaño aumentado
                        fontWeight = FontWeight.Bold
                    ),
                )
                SpacerH(10.dp)
                MainButton(
                    name = "Salida de vehículo",
                    backColor = MaterialTheme.colorScheme.primary,
                    textColor = Color.White,
                    onClick = { navController.navigate(Routes.SALIDA) },
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                        .size(50.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 18.sp, // 👈 Tamaño aumentado
                        fontWeight = FontWeight.Bold
                    ),
                )
                SpacerH(10.dp)
                MainButton(
                    name = "Cierre de turno",
                    backColor = MaterialTheme.colorScheme.primary,
                    textColor = Color.White,
                    onClick = { navController.navigate(Routes.CIERRE) },
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                        .size(50.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 18.sp, // 👈 Tamaño aumentado
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel, // ahora obligatorio y provisto por HomeScreen
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {

    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }

    val turnoFinalizado by sessionViewModel.turnoFinalizado.collectAsState()

    Text(text = "Ges-parking", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
    HorizontalDivider()

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.ContentPasteSearch,
                contentDescription = "consulta"
            )
        },
        label = {
            Text(
                text = "Consulta de vehículo",
                fontSize = 17.sp,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {
            navController.navigate(Routes.CONSULTAR_VEHICULO_PARQUEADERO)
            scope.launch {
                drawerState.close()
            }
        }
    )

    SpacerH(4.dp)

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.History,
                contentDescription = "historial"
            )
        },
        label = {
            Text(
                text = "Historial del turno",
                fontSize = 17.sp,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {
            navController.navigate(Routes.HISTORIAL)
            scope.launch {
                drawerState.close()
            }
        }
    )

    SpacerH(4.dp)

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.ChecklistRtl,
                contentDescription = "vehiculos en parqueadero"
            )
        },
        label = {
            Text(
                text = "Vehículos en parqueadero",
                fontSize = 17.sp,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {
            navController.navigate(Routes.VEHICULO_EN_PARQUEADERO)
            scope.launch {
                drawerState.close()
            }
        }
    )

    SpacerH(4.dp)

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.ReceiptLong,
                contentDescription = "resumen"
            )
        },
        label = {
            Text(
                text = "Resumen de turno",
                fontSize = 17.sp,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {
            navController.navigate(Routes.RESUMEN)
            scope.launch {
                drawerState.close()
            }
        }
    )

    SpacerH(4.dp)

    // Único botón de Cerrar sesión (aquí controlamos todo)
    NavigationDrawerItem(
        icon = {
            Icon(imageVector = Icons.Rounded.AccountCircle, contentDescription = "Sesion")
        },
        label = {
            Text(text = "Cerrar sesión", fontSize = 17.sp, modifier = Modifier.padding(16.dp))
        },
        selected = false,
        onClick = {
            if (turnoFinalizado) {
                showConfirmDialog = true
            } else {
                showWarningDialog = true
            }
        }
    )

    // Dialogo de advertencia (turno aún abierto)
    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            title = { Text("Aviso") },
            text = { Text("Debe cerrar el turno antes de cerrar la sesión.") },
            confirmButton = {
                TextButton(onClick = { showWarningDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Dialogo de confirmación (turno cerrado)
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmación") },
            text = { Text("¿Está seguro de cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false

                    // Cerrar el drawer primero para que la navegación se vea limpia
                    scope.launch { drawerState.close() }

                    // Llamar al servidor para cerrar sesión y, en caso de éxito, navegar a "inicio"
                    sessionViewModel.cerrarSesionServidor { success, message ->
                        if (success) {
                            // Navegación en UI thread
                            scope.launch {
                                navController.navigate("inicio") {
                                    popUpTo(0)
                                }
                            }
                        } else {
                            // Mostrar mensaje al usuario
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("No")
                }
            }
        )
    }

}

@Composable
fun ScreenContent(modifier: Modifier = Modifier, sessionViewModel: SessionViewModel = viewModel()){
    val sessionData by sessionViewModel.sessionData.collectAsState()

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Card (
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.fillMaxWidth()
                .padding(4.dp)
        ){
            Column (
                modifier = Modifier.padding(10.dp)
            ){
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                            append("Empleado: ")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.DarkGray)){
                            append(sessionData.nombreCompleto)
                        }
                    },
                    fontSize = 15.sp,
                    color = Color.Black
                )

                SpacerH(8.dp)

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                            append("Turno: ")
                        }
                        append(sessionData.turno)
                    },
                    fontSize = 15.sp,
                    color = Color.Black
                )

                SpacerH(8.dp)

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                            append("Número de turno: ")
                        }
                        append(sessionData.numeroTurno.toString())
                    },
                    fontSize = 15.sp,
                    color = Color.Black
                )

                SpacerH(8.dp)

                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                            append("Fecha: ")
                        }
                        append(sessionData.fechaInicio)
                    },
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
        }

        SpacerH(30.dp)

        Text(
            "Parqueadero",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.Black
        )

        SpacerH(40.dp)

        Image(
            painter = painterResource(R.drawable.aparcamientodecoches),
            contentDescription = "Logo home",
            modifier = Modifier.size(160.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onOpenDrawer: ()-> Unit
){
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            Icon(
               imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(28.dp)
                    .clickable {
                    onOpenDrawer()
                }
            )
        },
        title = {
            Text("Parqueadero", color = Color.Black)
        }
    )
}
