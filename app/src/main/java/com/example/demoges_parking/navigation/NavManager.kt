package com.example.demoges_parking.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demoges_parking.screens.CierreScreen
import com.example.demoges_parking.screens.ConsultaVehiculoScreen
import com.example.demoges_parking.screens.HistorialScreen
import com.example.demoges_parking.screens.HomeScreen
import com.example.demoges_parking.screens.IngresoScreen
import com.example.demoges_parking.screens.InicioScreen
import com.example.demoges_parking.screens.LoginScreen
import com.example.demoges_parking.screens.ResumenTurnoScreen
import com.example.demoges_parking.screens.SalidaScreen
import com.example.demoges_parking.screens.VehiculosActivosScreen
import com.example.demoges_parking.viewmodels.CierreViewModel
import com.example.demoges_parking.viewmodels.SessionViewModel

@Composable
fun NavManager(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.INICIO) {

        composable(Routes.INICIO){
            val sessionViewModel: SessionViewModel = viewModel()
            InicioScreen(
                navController = navController, sessionViewModel = sessionViewModel
            )
        }

        composable(Routes.LOGIN){
            LoginScreen(navController)
        }

        composable(Routes.HOME){
            HomeScreen(navController)
        }

        composable(Routes.INGRESO){
            val sessionViewModel: SessionViewModel = viewModel()
            IngresoScreen(
                navController = navController,
                sessionViewModel = sessionViewModel
            )
        }

        composable(Routes.SALIDA){
            val sessionViewModel: SessionViewModel = viewModel()
            SalidaScreen(
                navController = navController,
                sessionViewModel = sessionViewModel
            )
        }

        composable(Routes.CIERRE){
            val sessionViewModel: SessionViewModel = viewModel()
            CierreScreen(
                navController = navController,
                sessionViewModel = sessionViewModel
            )
        }

        composable(Routes.CONSULTAR_VEHICULO_PARQUEADERO){
            ConsultaVehiculoScreen(navController = navController)
        }

        composable(Routes.HISTORIAL){
            val sessionViewModel: SessionViewModel = viewModel()
            HistorialScreen(
                navController = navController,
                sessionViewModel = sessionViewModel
            )
        }

        composable(Routes.VEHICULO_EN_PARQUEADERO){
            VehiculosActivosScreen(navController)
        }

        composable(Routes.RESUMEN) {
            val sessionViewModel: SessionViewModel = viewModel()
            val cierreViewModel: CierreViewModel = viewModel()
            ResumenTurnoScreen(
                navController = navController,
                sessionViewModel = sessionViewModel,
                cierreViewModel = cierreViewModel
            )
        }
    }
}