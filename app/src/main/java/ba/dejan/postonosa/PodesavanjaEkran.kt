package ba.dejan.postonosa

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.URL

// LINK ZA AZURIRANJE PROVIZIJE (Hostani JSON fajl se mora zvati "naknada.json")
const val PROVIZIJA_URL = "https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/naknada.json"

@Composable
fun PodesavanjaEkran(navController: NavController, prefs: SharedPreferences, dao: RacunDao) {
    val coroutineScope = rememberCoroutineScope()
    var uslugeList by remember { mutableStateOf(ucitajUsluge(prefs)) }
    var prikaziDialogZaNovuUslugu by remember { mutableStateOf(false) }
    var tekstNoveUsluge by remember { mutableStateOf("") }
    var bezProvizijeCheckbox by remember { mutableStateOf(false) }
    val ime = prefs.getString("ime_prezime", "Nepoznat") ?: "Nepoznat"
    val idRadnika = prefs.getString("radnik_id", "000") ?: "000"
    val posta = prefs.getString("posta_naziv", "Nepoznato") ?: "Nepoznato"
    var ucitavanje by remember { mutableStateOf(false) }
    var exportPdf by remember { mutableStateOf(prefs.getBoolean("export_pdf", true)) }
    var exportTxt by remember { mutableStateOf(prefs.getBoolean("export_txt", true)) }
    val context = LocalContext.current
    var emailZaSlanje by remember { mutableStateOf(prefs.getString("email_za_slanje", "") ?: "") }
    val dinamickaVerzija = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            "1.0"
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Pozadina)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ZAGLAVLJE
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nazad", tint = Color.Black)
            }
            Text("Podešavanja", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
        }
        // PODACI RADNIKA
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("Prijavljeni korisnik", fontSize = 12.sp, color = Color.Gray)
                Text("Ime i prezime: $ime", fontSize = 16.sp)
                Text("Šifra radnika: $idRadnika", fontSize = 16.sp)
                Text("Pošta: $posta", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        dao.obrisiSve()
                        Sesija.pocetakKorisnika.value = System.currentTimeMillis()
                        Sesija.osvjeziBazu.value += 1
                        prefs.edit().clear().apply()
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ODJAVI SE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pošalji pregled uplata", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Text("Unesi email adresu pošte na koju šalješ pregled uplata.", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
                // Polje za unos emaila
                OutlinedTextField(
                    value = emailZaSlanje,
                    onValueChange = { noviUnos ->
                        val bezRazmaka = noviUnos.replace(" ", "")
                        emailZaSlanje = bezRazmaka
                        prefs.edit().putString("email_za_slanje", emailZaSlanje).apply()
                    },
                    label = { Text("Email adresa pošte", color = Color.Gray) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GlavnaBoja,
                        focusedLabelColor = GlavnaBoja,
                        cursorColor = SporednaBoja
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Dugme za slanje
                Button(
                    onClick = {
                        EmailAlati.posaljiZadnjiIzvjestaj(context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GlavnaBoja)
                ) {
                    Text(
                        text = "POŠALJI ZADNJI PREGLED",
                        color = SporednaBoja,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
        // USLUGE
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Usluge", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Text("Prilagodi listu usluga koje se prikazuju u padajućem meniju na ekranu za unos.", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
                Button(
                    onClick = { prikaziDialogZaNovuUslugu = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GlavnaBoja),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Icon(imageVector = androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Dodaj", tint = SporednaBoja)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DODAJ NOVU USLUGU", color = SporednaBoja, fontWeight = FontWeight.Bold, overflow = TextOverflow.Ellipsis, maxLines = 1)
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    uslugeList.forEachIndexed { index, usluga ->
                        val bojaPozadine = if (usluga.bezProvizije) Color(0xFFC8E6C9) else Color(0xFFE0E0E0)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(bojaPozadine, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = usluga.naziv,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            // Kontrole
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Dugme GORE
                                if (index > 0) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Gore",
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(28.dp).clickable {
                                            val novaLista = uslugeList.toMutableList()
                                            val temp = novaLista[index]
                                            novaLista[index] = novaLista[index - 1]
                                            novaLista[index - 1] = temp
                                            uslugeList = novaLista
                                            spasiUsluge(prefs, novaLista)
                                        }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                // Dugme DOLE
                                if (index < uslugeList.size - 1) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Dole",
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(28.dp).clickable {
                                            val novaLista = uslugeList.toMutableList()
                                            val temp = novaLista[index]
                                            novaLista[index] = novaLista[index + 1]
                                            novaLista[index + 1] = temp
                                            uslugeList = novaLista
                                            spasiUsluge(prefs, novaLista)
                                        }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                // Obrisi
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                                    contentDescription = "Obriši",
                                    tint = Color.Red.copy(alpha = 0.8f),
                                    modifier = Modifier.size(24.dp).clickable {
                                        val novaLista = uslugeList.filter { it.naziv != usluga.naziv }
                                        uslugeList = novaLista
                                        spasiUsluge(prefs, novaLista)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        // DIALOG ZA DODAVANJE NOVE USLUGE
        if (prikaziDialogZaNovuUslugu) {
            AlertDialog(
                onDismissRequest = { prikaziDialogZaNovuUslugu = false },
                containerColor = Color.White,
                title = { Text("NOVA USLUGA", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tekstNoveUsluge,
                            onValueChange = { tekstNoveUsluge = it },
                            label = { Text("Naziv usluge", color = Color.Gray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GlavnaBoja, focusedLabelColor = SporednaBoja)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { bezProvizijeCheckbox = !bezProvizijeCheckbox }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = bezProvizijeCheckbox,
                                onCheckedChange = { bezProvizijeCheckbox = it },
                                colors = CheckboxDefaults.colors(checkedColor = GlavnaBoja, checkmarkColor = SporednaBoja)
                            )
                            Text("Bez provizije!", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tekstNoveUsluge.isNotBlank() && !uslugeList.any { it.naziv == tekstNoveUsluge }) {
                                val novaLista = uslugeList + TipUsluge(tekstNoveUsluge.trim(), bezProvizijeCheckbox)
                                uslugeList = novaLista
                                spasiUsluge(prefs, novaLista)
                            }
                            tekstNoveUsluge = ""
                            bezProvizijeCheckbox = false
                            prikaziDialogZaNovuUslugu = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GlavnaBoja)
                    ) {
                        Text("DODAJ", color = SporednaBoja, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { prikaziDialogZaNovuUslugu = false }) {
                        Text("Odustani", color = Color.Gray)
                    }
                }
            )
        }
        //IZBOR EKSPORTA
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Format pregleda uplata", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Text("Izaberi format (ili oba) pregleda uplata koji želiš snimiti prilikom kraja rada.", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))
                // PDF
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (exportTxt) {
                                exportPdf = !exportPdf
                                prefs.edit().putBoolean("export_pdf", exportPdf).apply()
                            } else {
                                Toast.makeText(context, "Bar jedan format mora biti izabran!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = exportPdf,
                        onCheckedChange = { noviStatus ->
                            if (exportTxt || noviStatus) {
                                exportPdf = noviStatus
                                prefs.edit().putBoolean("export_pdf", noviStatus).apply()
                            } else {
                                Toast.makeText(context, "Bar jedan format mora biti izabran!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = CheckboxDefaults.colors(checkedColor = GlavnaBoja)
                    )
                    Text("Snimi kao PDF (Laserska štampa)", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                // TXT
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (exportPdf) {
                                exportTxt = !exportTxt
                                prefs.edit().putBoolean("export_txt", exportTxt).apply()
                            } else {
                                Toast.makeText(context, "Bar jedan format mora biti izabran!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = exportTxt,
                        onCheckedChange = { noviStatus ->
                            if (exportPdf || noviStatus) {
                                exportTxt = noviStatus
                                prefs.edit().putBoolean("export_txt", noviStatus).apply()
                            } else {
                                Toast.makeText(context, "Bar jedan format mora biti izabran!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = CheckboxDefaults.colors(checkedColor = GlavnaBoja)
                    )
                    Text("Snimi kao TXT (Matična štampa)", fontSize = 16.sp)
                }
            }
        }
        // AZURIRANJE JSON
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Ažuriranje provizije", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Text("Preuzmi najnoviji cjenovnik provizije.", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
                Button(
                    onClick = {
                        ucitavanje = true
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                val skinutiTekst = URL(PROVIZIJA_URL).readText()
                                JSONObject(skinutiTekst)
                                File(context.filesDir, "naknada.json").writeText(skinutiTekst)
                                withContext(Dispatchers.Main) {
                                    ucitavanje = false
                                    Toast.makeText(context, "Provizija uspješno ažurirana!", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    ucitavanje = false
                                    Toast.makeText(context, "Greška pri ažuriranju. Provjerite internet.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GlavnaBoja),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !ucitavanje
                ) {
                    Text(if (ucitavanje) "PREUZIMANJE..." else "AŽURIRAJ PROVIZIJU", color = SporednaBoja, fontWeight = FontWeight.Bold, overflow = TextOverflow.Ellipsis, maxLines = 1)
                }
            }
        }
        //Potpis i verzija
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "POŠTONOŠA v$dinamickaVerzija",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Text(
                text = "Razvoj: Dejan Popović",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
// Čuvanje liste usluga
data class TipUsluge(val naziv: String, val bezProvizije: Boolean = false)
fun ucitajUsluge(prefs: SharedPreferences): List<TipUsluge> {
    val spaseno = prefs.getString("lista_usluga", "PRVI_PUT")
    return when {
        spaseno == "PRVI_PUT" -> {
            // Aplikacija se prvi put pokreće
            listOf(
                TipUsluge("Elektroprivreda"),
                TipUsluge("Mtel"),
                TipUsluge("Supernova"),
                TipUsluge("Telemach"),
                TipUsluge("Voda"),
                TipUsluge("Smeće"),
                TipUsluge("RTV taksa", true),
                TipUsluge("Platni promet"),
            )
        }
        spaseno.isNullOrEmpty() -> {
            // Sve usluge obrisane
            listOf(TipUsluge("Platni promet"))
        }
        else -> {
            spaseno.split(";").map {
                val dijelovi = it.split("|")
                if (dijelovi.size == 2) {
                    TipUsluge(naziv = dijelovi[0], bezProvizije = dijelovi[1].toBoolean())
                } else {
                    // Fallback
                    TipUsluge(naziv = dijelovi[0], bezProvizije = false)
                }
            }
        }
    }
}
fun spasiUsluge(prefs: SharedPreferences, usluge: List<TipUsluge>) {
    val stringZaSnimanje = usluge.joinToString(";") { "${it.naziv}|${it.bezProvizije}" }
    prefs.edit().putString("lista_usluga", stringZaSnimanje).apply()
}