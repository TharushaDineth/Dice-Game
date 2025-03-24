package com.example.dicegame.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun StartScreen(navController: NavController){
    var showDialog by remember { mutableStateOf(false) }
    Column(
        modifier= Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to the Dice game!", fontSize = 24.sp, fontWeight = FontWeight.Bold )

        Button(
            onClick = {navController.navigate("MainGame")},
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(text = "New Game")
        }
        Button(
            onClick = {showDialog = true},
            modifier = Modifier.padding(top = 20.dp)
        ) { Text(text = "About")}
    }
    AboutDialog(showDialog = showDialog, onDismiss = { showDialog = false })

}