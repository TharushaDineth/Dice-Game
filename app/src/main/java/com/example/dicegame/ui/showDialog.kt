package com.example.dicegame.ui

import android.app.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import java.util.Properties

@Composable
fun AboutDialog(showDialog: Boolean, onDismiss: ()-> Unit  ){
    if (showDialog){
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "About Dice Game") },
            text = { Text(text = "Tharusha Dineth - 20230257\n I confirm that I understand what plagiarism is and have read and\n" +
                    "understood the section on Assessment Offences in the Essential\n" +
                    "Information for Students. The work that I have submitted is\n" +
                    "entirely my own. Any work from other authors is duly referenced\n" +
                    "and acknowledged." ) },
            confirmButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text(text = "OK")
                }
            },
            properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true)

        )
    }
}