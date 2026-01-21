package com.gesnnova.demoges_parking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gesnnova.demoges_parking.navigation.NavManager
import com.gesnnova.demoges_parking.ui.theme.DemoGesparkingTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoGesparkingTheme {
                NavManager()
            }
        }
    }
}
