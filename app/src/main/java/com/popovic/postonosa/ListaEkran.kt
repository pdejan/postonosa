package com.popovic.postonosa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
fun ListaEkran(navController: NavController, dao: RacunDao) {
    // ucitaj sve iz baze
    var racuni by remember { mutableStateOf(dao.dohvatiSve()) }
    Column(modifier = Modifier.fillMaxSize().background(Pozadina)) {
        // ZAGLAVLJE
        Row(
            modifier = Modifier.fillMaxWidth().background(SporednaBoja).padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Nazad",
                    tint = GlavnaBoja
                )
            }
            Text("Pregled uplata", color = GlavnaBoja, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
        }
        if (racuni.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nema uplata.", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            // LISTA RAČUNA
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                itemsIndexed(racuni) { index, racun ->
                    val redniBroj = racuni.size - index
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$redniBroj. - ${racun.tipUsluge}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = SporednaBoja
                                )
                                Text("Iznos: ${String.format("%.2f", racun.iznos)} KM", color = Color.Gray, fontSize = 14.sp)
                                Text("Provizija: ${String.format("%.2f", racun.provizija)} KM", color = Color.Gray, fontSize = 14.sp)
                            }
                            // Dugme za brisanje
                            IconButton(onClick = {
                                dao.obrisiPojedinacni(racun)
                                racuni = dao.dohvatiSve()
                                Sesija.osvjeziBazu.value += 1
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Obriši",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}