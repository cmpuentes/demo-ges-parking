package com.example.demoges_parking.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.demoges_parking.R
import com.example.demoges_parking.navigation.Routes
import com.example.demoges_parking.network.ApiClient
import com.example.demoges_parking.viewmodels.SessionViewModel
import kotlinx.coroutines.delay

@Composable
fun InicioScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel
) {
    val context = LocalContext.current
    val session by sessionViewModel.sessionData.collectAsState()

    // Animación Lottie
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animationparking))
    val progress by animateLottieCompositionAsState(composition)

    LaunchedEffect(Unit) {
        delay(2000) // esperar 2 segundos para mostrar animación

        if (session.token.isNotBlank()) {
            // Validar sesión con el backend
            try {
                val response = ApiClient.apiService.checkSession(session.token)

                if (response.isSuccessful && response.body()?.get("success") == true) {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.INICIO) { inclusive = true }
                    }
                } else {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.INICIO) { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                // En caso de error de red, redirigir a Login por seguridad
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.INICIO) { inclusive = true }
                }
            }
        } else {
            // No hay token, ir a Login
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.INICIO) { inclusive = true }
            }
        }
    }

    // UI: Animación centrada
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )
    }
}
