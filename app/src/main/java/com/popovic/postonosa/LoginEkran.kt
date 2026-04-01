package com.popovic.postonosa

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

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
            painter = painterResource(id = R.drawable.email),
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
            onValueChange = { noviUnos ->
                val samoBrojevi = noviUnos.filter {it.isDigit()}
                if (samoBrojevi.length <= 10){
                   radnikId = samoBrojevi
                }
            },
            label = { Text("Šifra radnika", color = Color.Gray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            onValueChange = { noviUnos ->
                val bezNovogReda = noviUnos.replace("\n", "")
                if (bezNovogReda.length <= 30){
                    imePrezime = bezNovogReda
                }
            },
            label = { Text("Ime i prezime", color = Color.Gray) },
            singleLine = true,
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
            onValueChange = { noviUnos ->
                val bezNovogReda = noviUnos.replace("\n", "")
                if (bezNovogReda.length <= 30) {
                    posta = bezNovogReda
                }
            },
            label = { Text("Pošta (npr. 74101 Doboj)", color = Color.Gray) },
            singleLine = true,
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

