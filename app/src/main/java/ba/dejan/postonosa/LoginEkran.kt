package ba.dejan.postonosa

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import ba.dejan.postonosa.ui.theme.GlavnaBoja
import ba.dejan.postonosa.ui.theme.Pozadina
import ba.dejan.postonosa.ui.theme.SporednaBoja

@Composable
fun LoginEkran(navController: NavController, prefs: SharedPreferences) {
    val context = LocalContext.current
    var radnikId by remember { mutableStateOf("") }
    var rejon by remember { mutableStateOf("") }
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
        Text(text = "Unesi svoje identifikacione podatke.", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = imePrezime,
            onValueChange = { noviUnos ->
                imePrezime = pripremiKratkiTekstZaUnos(noviUnos)
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Šifra radnika
            OutlinedTextField(
                value = radnikId,
                onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) radnikId = it },
                label = { Text("Šifra radnika", color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.6f),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GlavnaBoja, cursorColor = SporednaBoja)
            )

            // Rejon
            OutlinedTextField(
                value = rejon,
                onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) rejon = it },
                label = { Text("Rejon", color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.4f),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GlavnaBoja, cursorColor = SporednaBoja)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = posta,
            onValueChange = { noviUnos ->
                posta = pripremiKratkiTekstZaUnos(noviUnos)
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
                val normalizovanoIme = normalizujKratkiTekst(imePrezime)
                val normalizovanaPosta = normalizujKratkiTekst(posta)
                if (radnikId.isNotBlank() && rejon.isNotBlank() && normalizovanoIme.isNotBlank() && normalizovanaPosta.isNotBlank()) {
                    prefs.edit().apply {
                        putString("radnik_id", radnikId)
                        putString("radni_rejon", rejon)
                        putString("ime_prezime", normalizovanoIme)
                        putString("posta_naziv", normalizovanaPosta)
                        apply()
                    }
                    Sesija.postaviPocetakKorisnika(prefs)
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Unesi sve identifikacione podatke!", Toast.LENGTH_SHORT).show()
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
