package ba.dejan.postonosa

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import ba.dejan.postonosa.ui.theme.GlavnaBoja
import ba.dejan.postonosa.ui.theme.Pozadina
import ba.dejan.postonosa.ui.theme.SporednaBoja

@Composable
fun KrajDanaEkran(navController: NavController, dao: RacunDao, prefs: SharedPreferences) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val ukupnoOcekivano = dao.ukupnoZaRazduzenje() ?: 0.0
    val racuni = dao.dohvatiSve()
    // Svi apoeni
    var kom200 by remember { mutableStateOf("") }
    var kom100 by remember { mutableStateOf("") }
    var kom50 by remember { mutableStateOf("") }
    var kom20 by remember { mutableStateOf("") }
    var kom10 by remember { mutableStateOf("") }
    var kom5 by remember { mutableStateOf("") }
    var kom2 by remember { mutableStateOf("") }
    var kom1 by remember { mutableStateOf("") }
    var kom050 by remember { mutableStateOf("") }
    var kom020 by remember { mutableStateOf("") }
    var kom010 by remember { mutableStateOf("") }
    var kom005 by remember { mutableStateOf("") }
    var kom001 by remember { mutableStateOf("") }
    // Matematika za svaki apoen
    val fizickiNovac = (kom200.toIntOrNull() ?: 0) * 200.0 +
            (kom100.toIntOrNull() ?: 0) * 100.0 +
            (kom50.toIntOrNull() ?: 0) * 50.0 +
            (kom20.toIntOrNull() ?: 0) * 20.0 +
            (kom10.toIntOrNull() ?: 0) * 10.0 +
            (kom5.toIntOrNull() ?: 0) * 5.0 +
            (kom2.toIntOrNull() ?: 0) * 2.0 +
            (kom1.toIntOrNull() ?: 0) * 1.0 +
            (kom050.toIntOrNull() ?: 0) * 0.50 +
            (kom020.toIntOrNull() ?: 0) * 0.20 +
            (kom010.toIntOrNull() ?: 0) * 0.10 +
            (kom005.toIntOrNull() ?: 0) * 0.05 +
            (kom001.toIntOrNull() ?: 0) * 0.01
    val razlika = fizickiNovac - ukupnoOcekivano
    val zavrsiSmjenuLogika = {
        // SIGURNOSNA PROVJERA
        if (racuni.isEmpty()) {
            Toast.makeText(context, "Nema unešenih uplata!", Toast.LENGTH_SHORT).show()
        }
        else if (razlika in -0.005..0.005) {
            // KASA SE SLAŽE
            val listaApoena = listOf(
                "200.00" to kom200, "100.00" to kom100, "50.00" to kom50, "20.00" to kom20, "10.00" to kom10,
                "5.00" to kom5, "2.00" to kom2, "1.00" to kom1,
                "0.50" to kom050, "0.20" to kom020, "0.10" to kom010, "0.05" to kom005, "0.01" to kom001
            ).mapNotNull { (vrijednostStr, komadaStr) ->
                val komada = komadaStr.toIntOrNull() ?: 0
                if (komada > 0) {
                    val ukupno = komada * vrijednostStr.toDouble()
                    "$vrijednostStr KM   x   $komada kom   =   ${String.format("%.2f", ukupno)} KM"
                } else null
            }
            val sdf = java.text.SimpleDateFormat("dd_MM_yyyy", java.util.Locale.getDefault())
            val imeFoldera = "${sdf.format(java.util.Date())}"
            // eksport
            val trebaPdf = prefs.getBoolean("export_pdf", true)
            val trebaTxt = prefs.getBoolean("export_txt", true)
            //Ocisti stare prefs za email prije snimanja novih
            prefs.edit().remove("zadnji_pdf_uri").remove("zadnji_txt_uri").apply()
            if (trebaPdf) {
                PdfEksport.generisiPdf(context, imeFoldera, racuni, listaApoena, fizickiNovac)
            }
            if (trebaTxt) {
                TxtEksport.generisiTxt(context, imeFoldera, racuni, listaApoena, fizickiNovac)
            }
            // Jedan toast
            val poruka = when {
                trebaPdf && trebaTxt -> "Fajlovi sačuvani!"
                trebaPdf -> "PDF sačuvan!"
                trebaTxt -> "TXT sačuvan!"
                else -> "Fajlovi sačuvani!" //fallback
            }
            Toast.makeText(context, poruka, Toast.LENGTH_SHORT).show()
            dao.obrisiSve()
            Sesija.postaviPocetakKorisnika(prefs)
            navController.popBackStack()

        } else {
            // KASA SE NE SLAZE
            Toast.makeText(context, "Kraj nije moguć! Stanje kase se ne poklapa.", Toast.LENGTH_LONG).show()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().background(Pozadina).imePadding().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //ZAGLAVLJE
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Nazad",
                    tint = SporednaBoja
                )
            }
            Text(
                text = "Kraj rada",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (razlika in -0.005..0.005) Color(0xFFE8F5E9) else if (razlika > 0.005) Color(0xFFE3F2FD) else Color(0xFFFFEBEE)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Stanje kase: ${String.format("%.2f", ukupnoOcekivano)} KM", fontWeight = FontWeight.Bold)
                Text("Unešeno (apoeni): ${String.format("%.2f", fizickiNovac)} KM")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                val razlikaTekst = when {
                    razlika in -0.005..0.005 -> "SVE SE SLAŽE! (0.00 KM)"
                    razlika > 0.005 -> "VIŠAK: +${String.format("%.2f", razlika)} KM"
                    else -> "MANJAK: ${String.format("%.2f", razlika)} KM"
                }
                val bojaTeksta = if (razlika in -0.005..0.005) Color(0xFF2E7D32) else if (razlika > 0.005) Color.Blue else Color.Red
                Text(razlikaTekst, color = bojaTeksta, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        // APOENI
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Apoenska struktura", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
            ApoenRed("200", kom200) { kom200 = it }
            ApoenRed("100", kom100) { kom100 = it }
            ApoenRed("50", kom50) { kom50 = it }
            ApoenRed("20", kom20) { kom20 = it }
            ApoenRed("10", kom10) { kom10 = it }
            ApoenRed("5", kom5) { kom5 = it }
            ApoenRed("2", kom2) { kom2 = it }
            ApoenRed("1", kom1) { kom1 = it }
            ApoenRed("0.50", kom050) { kom050 = it }
            ApoenRed("0.20", kom020) { kom020 = it }
            ApoenRed("0.10", kom010) { kom010 = it }
            ApoenRed("0.05", kom005) { kom005 = it }
            ApoenRed(
                vrijednost = "0.01",
                komada = kom001,
                akcijaTastature = ImeAction.Done,
                okidac = { focusManager.clearFocus() }
            ) { kom001 = it }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { zavrsiSmjenuLogika() },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlavnaBoja)
            ) {
                Text(
                    text = "ZAVRŠI RAD I SNIMI PREGLED",
                    fontSize = 18.sp,
                    color = SporednaBoja,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
// APOENI
@Composable
fun ApoenRed(
    vrijednost: String,
    komada: String,
    akcijaTastature: ImeAction = ImeAction.Next,
    okidac: () -> Unit = {},
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    ) {
        OutlinedTextField(
            value = komada,
            onValueChange = { if (it.length <= 5 && it.all { char -> char.isDigit() }) { onValueChange(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = akcijaTastature
            ),
            keyboardActions = KeyboardActions(
                onDone = { okidac() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GlavnaBoja,
                focusedLabelColor = GlavnaBoja,
                cursorColor = SporednaBoja
            ),
            modifier = Modifier.width(100.dp).focusRequester(focusRequester),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "x  $vrijednost KM", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
    }
}
