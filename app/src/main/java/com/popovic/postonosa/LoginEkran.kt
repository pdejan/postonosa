package com.popovic.postonosa

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginEkran(navController: NavController, prefs: SharedPreferences) {
    var radnikId by remember { mutableStateOf("") }
    var imePrezime by remember { mutableStateOf("") }
    var posta by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().background(Pozadina).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // LOGO I NAZIV APLIKACIJE NA SREDINI
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.Email,
            contentDescription = "Logo",
            tint = GlavnaBoja,
            modifier = Modifier.size(80.dp).padding(bottom = 8.dp)
        )
        Text(
            text = "POŠTONOŠA",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SporednaBoja
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "PRIJAVA", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = radnikId,
            onValueChange = { radnikId = it },
            label = { Text("Šifra radnika", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GlavnaBoja,
                focusedLabelColor = GlavnaBoja,
                cursorColor = SporednaBoja
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = imePrezime,
            onValueChange = { imePrezime = it },
            label = { Text("Ime i prezime", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GlavnaBoja,
                focusedLabelColor = GlavnaBoja,
                cursorColor = SporednaBoja
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = posta,
            onValueChange = { posta = it },
            label = { Text("Pošta (npr. 74101 Doboj)", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GlavnaBoja,
                focusedLabelColor = GlavnaBoja,
                cursorColor = SporednaBoja
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (radnikId.isNotBlank() && imePrezime.isNotBlank()) {
                    prefs.edit().apply {
                        putString("radnik_id", radnikId)
                        putString("ime_prezime", imePrezime)
                        putString("posta_naziv", posta)
                        apply()
                    }
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GlavnaBoja)
        ) {
            Text(
                text = "PRIJAVI SE",
                color = SporednaBoja,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

