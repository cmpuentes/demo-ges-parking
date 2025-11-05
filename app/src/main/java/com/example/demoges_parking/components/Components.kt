package com.example.demoges_parking.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SpacerH(size: Dp = 5.dp){
    Spacer(modifier = Modifier.height(size))
}

@Composable
fun SpacerW(size: Dp = 5.dp){
    Spacer(modifier = Modifier.width(size))
}

@Composable
fun MainButton(
    name: String,
    backColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp), // 👈 Añadimos el parámetro shape
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium // 👈 Nuevo parámetro
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backColor,
            contentColor = textColor
        ),
        enabled = enabled,
        shape = shape, // 👈 Aquí lo usamos
        modifier = modifier
    ) {
        Text(
            text = name,
            style = textStyle // 👈 Aplicamos el estilo aquí
        )
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    textColor: Color = Color.Black // color por defecto negro
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            color = textColor
        )
    }
}
