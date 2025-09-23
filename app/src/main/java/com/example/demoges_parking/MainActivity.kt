package com.example.demoges_parking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.demoges_parking.navigation.NavManager
import com.example.demoges_parking.ui.theme.DemoGesparkingTheme


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
