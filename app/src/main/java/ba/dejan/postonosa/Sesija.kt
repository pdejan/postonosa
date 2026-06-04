package ba.dejan.postonosa

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf

object Sesija {
    private const val KLJUC_POCETAK_KORISNIKA = "pocetak_korisnika"

    var pocetakKorisnika = mutableStateOf(System.currentTimeMillis())
    var osvjeziBazu = mutableStateOf(0)

    fun ucitajPocetakKorisnika(prefs: SharedPreferences) {
        val sada = System.currentTimeMillis()
        val sacuvaniPocetak = if (prefs.contains(KLJUC_POCETAK_KORISNIKA)) {
            prefs.getLong(KLJUC_POCETAK_KORISNIKA, sada)
        } else {
            sada
        }
        pocetakKorisnika.value = sacuvaniPocetak
    }

    fun postaviPocetakKorisnika(prefs: SharedPreferences, vrijeme: Long = System.currentTimeMillis()) {
        pocetakKorisnika.value = vrijeme
        prefs.edit().putLong(KLJUC_POCETAK_KORISNIKA, vrijeme).apply()
        osvjeziBazu.value += 1
    }
}
