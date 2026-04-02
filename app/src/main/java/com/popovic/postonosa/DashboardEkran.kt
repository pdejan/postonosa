package com.popovic.postonosa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DashboardEkran(navController: NavController, dao: RacunDao) {
    // Stoperica / okidac
    val trigger = Sesija.osvjeziBazu.value
    val pocetak = Sesija.pocetakKorisnika.value

    // Stanja koja ekran prikazuje
    var ukupnoStanje by remember { mutableStateOf(0.0) }
    var sumaTrenutni by remember { mutableStateOf(0.0) }
    var listaTrenutni by remember { mutableStateOf(listOf<Racun>()) }

    // Baza racuna na osnovu stoperice
    LaunchedEffect(trigger, pocetak) {
        ukupnoStanje = dao.ukupnoZaRazduzenje() ?: 0.0
        sumaTrenutni = dao.ukupnoZaKorisnika(pocetak) ?: 0.0
        listaTrenutni = dao.dohvatiRacuneKorisnika(pocetak)
    }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().background(Pozadina).systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //ZAGLAVLJE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lijeva strana: Logo + ime
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.email),
                    contentDescription = "Logo",
                    tint = GlavnaBoja,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "POŠTONOŠA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SporednaBoja
                )
            }
            // Desna strana - Ikonica list/zupcanik
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigate("lista") }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = "Pregled uplata",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Gray
                    )
                }
                IconButton(onClick = { navController.navigate("podesavanja") }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                        contentDescription = "Podešavanja",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
        // STANJE KASE
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = SporednaBoja),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("STANJE KASE", color = Color.Gray, fontSize = 12.sp)
                Text("${String.format("%.2f", ukupnoStanje)} KM", color = GlavnaBoja, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        // SREDINA (skrol)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TRENUTNA STRANKA
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TRENUTNA STRANKA", fontSize = 12.sp, color = Color.Gray)
                            Text("${String.format("%.2f", sumaTrenutni)} KM", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                Sesija.pocetakKorisnika.value = System.currentTimeMillis()
                                Sesija.osvjeziBazu.value += 1
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SporednaBoja)
                        ) {
                            Text("NOVA STRANKA", fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = GlavnaBoja)
                        }
                    }
                    // LISTA RACUNA TRENUTNE STRANKE
                    if (listaTrenutni.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        listaTrenutni.forEach { racun ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("- ${racun.tipUsluge}", fontSize = 14.sp, color = Color.DarkGray)
                                Text("${String.format("%.2f", racun.iznos + racun.provizija)} KM", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            TextButton(
                onClick = { navController.navigate("lista") },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("PREGLED UPLATA", color = SporednaBoja, fontWeight = FontWeight.Bold)
            }
        }
        // DNO (fiksno)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Button(
                onClick = { navController.navigate("unos") },
                modifier = Modifier.fillMaxWidth().height(80.dp).padding(bottom = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlavnaBoja),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("+ NOVA UPLATA", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SporednaBoja)
            }
            Button(
                onClick = { navController.navigate("kraj") },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SporednaBoja),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("KRAJ RADA", fontSize = 16.sp, color = GlavnaBoja)
            }
        }
    }
}