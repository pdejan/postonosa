package ba.dejan.postonosa

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ba.dejan.postonosa.ui.theme.GlavnaBoja
import ba.dejan.postonosa.ui.theme.Pozadina
import ba.dejan.postonosa.ui.theme.SporednaBoja

@Composable
fun ListaEkran(navController: NavController, dao: RacunDao) {
    // ucitaj sve iz baze
    var racuni by remember { mutableStateOf(dao.dohvatiSve()) }
    var racunZaBrisanje by remember { mutableStateOf<Racun?>(null) }
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
                                    color = SporednaBoja,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                Text("Iznos: ${String.format("%.2f", racun.iznos)} KM", color = Color.Gray, fontSize = 14.sp)
                                Text("Provizija: ${String.format("%.2f", racun.provizija)} KM", color = Color.Gray, fontSize = 14.sp)
                            }
                            // Dugme za brisanje
                            IconButton(onClick = { racunZaBrisanje = racun }) {
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
    // POTVRDA BRISANJA
    racunZaBrisanje?.let { racun ->
        AlertDialog(
            onDismissRequest = { racunZaBrisanje = null },
            containerColor = Color.White,
            title = { Text("Brisanje uplate", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = {
                Column {
                    Text("Da li sigurno želiš obrisati ovu uplatu?", fontSize = 16.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = racun.tipUsluge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = SporednaBoja,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Iznos:", color = Color.Gray, fontSize = 14.sp)
                            Text("${String.format("%.2f", racun.iznos)} KM", color = Color.Black, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Provizija:", color = Color.Gray, fontSize = 14.sp)
                            Text("${String.format("%.2f", racun.provizija)} KM", color = Color.Black, fontSize = 14.sp)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ukupno:", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "${String.format("%.2f", racun.iznos + racun.provizija)} KM",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        dao.obrisiPojedinacni(racun)
                        racuni = dao.dohvatiSve()
                        Sesija.osvjeziBazu.value += 1
                        racunZaBrisanje = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("OBRIŠI", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { racunZaBrisanje = null }) {
                    Text("ODUSTANI", color = Color.Gray)
                }
            }
        )
    }
}