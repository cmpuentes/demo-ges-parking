package com.gesnnova.demoges_parking.screens

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gesnnova.demoges_parking.components.MainButton
import com.gesnnova.demoges_parking.components.SpacerH
import com.gesnnova.demoges_parking.model.UiMessage
import com.gesnnova.demoges_parking.navigation.Routes
import com.gesnnova.demoges_parking.utils.obtenerFechaActual
import com.gesnnova.demoges_parking.viewmodels.LoginViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavController) {

    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(context.applicationContext as Application) as T
            }
        }
    )

    val loginRequest by loginViewModel.loginRequest.collectAsState()
    val isLoading by loginViewModel.isLoading.collectAsState()

    // Mostrar/ocultar contraseña
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var expandedTurno by remember { mutableStateOf(false) }
    val opciones = listOf("Turno 1", "Turno 2", "Turno 3")
    val passwordFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        loginViewModel.uiMessage.collect { msg ->
            when (msg) {
                is UiMessage.Error -> {
                    snackbarHostState.showSnackbar(msg.message)
                }

                is UiMessage.Success -> {
                    snackbarHostState.showSnackbar(msg.message)
                    delay(500) // Pequeño retraso para mostrar el mensaje
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            }
        }
    }

    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ){
        Column (
            modifier = Modifier.fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            SpacerH(24.dp)

            ExposedDropdownMenuBox(
                expanded = expandedTurno,
                onExpandedChange = {expandedTurno = !expandedTurno}
            ){
                OutlinedTextField(
                    value = loginRequest.turno,
                    onValueChange = {loginViewModel.actualizarTurno(it)},
                    label = { Text("Seleccionar turno") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedTurno
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor() // 👈 necesario para posicionar correctamente el menú
                )
                ExposedDropdownMenu(
                    expanded = expandedTurno,
                    onDismissRequest = {expandedTurno = false}
                ) {
                    opciones.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                loginViewModel.actualizarTurno(opcion)
                                expandedTurno = false
                            }
                        )
                    }
                }
            }

            SpacerH(25.dp)

            OutlinedTextField(
                value = loginRequest.login,
                onValueChange = { loginViewModel.actualizarLogin(it) },
                label = { Text("Usuario") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        passwordFocusRequester.requestFocus()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            SpacerH(24.dp)

            OutlinedTextField(
                value = loginRequest.password,
                onValueChange = {loginViewModel.actualizarPassword(it)},
                label = {Text("Contraseña")},
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = "Toggle Password Visibility")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester)
            )

            SpacerH(24.dp)

            //Colocar barra de carga circular

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            } else {
                MainButton(
                    name = "Iniciar sesión",
                    backColor = MaterialTheme.colorScheme.primary,
                    textColor = Color.White,
                    onClick = {
                        keyboardController?.hide()
                        val fecha = obtenerFechaActual()
                        loginViewModel.actalizarFechaInicio(fecha)
                        loginViewModel.performLogin()
                    },
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                        .size(50.dp)
                )
            }
        }
    }
}