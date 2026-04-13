package ba.dejan.postonosa

import androidx.compose.runtime.mutableStateOf

object Sesija {
    var pocetakKorisnika = mutableStateOf(System.currentTimeMillis())
    var osvjeziBazu = mutableStateOf(0)
}