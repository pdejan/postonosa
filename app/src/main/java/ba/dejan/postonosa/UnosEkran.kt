package ba.dejan.postonosa

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnosEkran(navController: NavController, dao: RacunDao, prefs: SharedPreferences) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val usluge = remember { ucitajUsluge(prefs) }
    val focusRequester = remember { FocusRequester() }
    var iznosTekst by remember { mutableStateOf("") }
    var izabranaUsluga by remember { mutableStateOf(usluge.firstOrNull() ?: TipUsluge("Platni promet", false)) }
    var expanded by remember { mutableStateOf(false) }
    val iznos = iznosTekst.toDoubleOrNull() ?: 0.0
    val provizija = if (izabranaUsluga.bezProvizije) 0.0 else Kalkulator.izracunaj(context, iznos)
    val snimiUplatu = {
        if (iznos > 0) {
            val racun = Racun(tipUsluge = izabranaUsluga.naziv, iznos = iznos, provizija = provizija)
            dao.dodajRacun(racun)
            Sesija.osvjeziBazu.value += 1
            iznosTekst = ""
            Toast.makeText(context, "Uplata snimljena!", Toast.LENGTH_SHORT).show()
        }
    }
    Column(modifier = Modifier.fillMaxSize().background(Pozadina).padding(24.dp)) {
        // ZAGLAVLJE
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nazad", tint = SporednaBoja)
            }
            Text("Nova uplata", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        // DROPDOWN ZA IZBOR USLUGE
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = izabranaUsluga.naziv,
                onValueChange = {},
                readOnly = true,
                label = { Text("Vrsta uplate") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 20.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GlavnaBoja,
                    focusedLabelColor = SporednaBoja,
                    cursorColor = SporednaBoja
                ),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Pozadina)
            ) {
                usluge.forEach { usluga ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = usluga.naziv,
                                fontSize = 20.sp,
                                color = SporednaBoja
                            )
                        },
                        onClick = {
                            izabranaUsluga = usluga
                            expanded = false
                            focusRequester.requestFocus()
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // UNOS IZNOSA
        OutlinedTextField(
            value = iznosTekst,
            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) iznosTekst = it },
            label = { Text("Iznos računa (KM)", color = Color.Gray) },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 20.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GlavnaBoja,
                focusedLabelColor = SporednaBoja,
                cursorColor = SporednaBoja
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    snimiUplatu()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Provizija: ${String.format("%.2f", provizija)} KM", color = SporednaBoja)
        Spacer(modifier = Modifier.height(24.dp))
        // DUGME ZA SNIMANJE
        Button(
            onClick = {
                keyboardController?.hide()
                snimiUplatu()
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GlavnaBoja)
        ) {
            Text("UNESI UPLATU", color = SporednaBoja, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}