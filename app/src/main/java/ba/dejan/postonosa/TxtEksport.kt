package ba.dejan.postonosa

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TxtEksport {
    @SuppressLint("DefaultLocale")
    // Vraća true samo ako je fajl stvarno snimljen — pozivalac smije obrisati bazu tek tada
    fun generisiTxt(context: Context, podPutanja: String, racuni: List<Racun>, apoeni: List<String>, ukupnoFizicki: Double): Boolean {
        val prefs = context.getSharedPreferences("PostonosaPrefs", Context.MODE_PRIVATE)
        val radnikIme = prefs.getString("ime_prezime", "Nepoznat") ?: "Nepoznat"
        val radnikId = prefs.getString("radnik_id", "000") ?: "000"
        val rejon = prefs.getString("radni_rejon", "000") ?: "000"
        val posta = prefs.getString("posta_naziv", "Posta") ?: "Posta"
        val datum = SimpleDateFormat("dd.MM.yyyy | HH:mm", Locale.getDefault()).format(Date())
        val sb = java.lang.StringBuilder()
        // ZAGLAVLJE
        sb.append("PREGLED IZVRSENIH UPLATA NA DOSTAVNOM REJONU\n")
        sb.append("JPM: $posta | $radnikId $radnikIme\n")
        sb.append("Rejon: $rejon | Datum: $datum\n")
        // 65 karaktera
        sb.append("-----------------------------------------------------------------\n")
        // R.br(5) + Vrsta(20) + Iznos(9) + Postarina(9) + Ukupno(9) + Br.tran.(9) + 4 razmaka = 65 karaktera
        sb.append(String.format("%-5s %-20s %-9s %-9s %-9s %-9s\n", "R.br.", "Vrsta uplate", "Iznos", "Postarina", "Ukupno", "Br.tran."))
        sb.append("-----------------------------------------------------------------\n")
        var ukupnoIznosSvi = 0.0
        var ukupnoProvizijaSvi = 0.0
        val hronoloskiRacuni = racuni.reversed()
        // REDOVI SA RACUNIMA
        hronoloskiRacuni.forEachIndexed { index, racun ->
            val usluga = if (racun.tipUsluge.length > 20) racun.tipUsluge.take(18) + ".." else racun.tipUsluge
            sb.append(String.format("%-5s %-20s %-9.2f %-9.2f %-9.2f %-9s\n",
                "${index + 1}.",
                usluga,
                racun.iznos,
                racun.provizija,
                (racun.iznos + racun.provizija),
                ""
            ))
            ukupnoIznosSvi += racun.iznos
            ukupnoProvizijaSvi += racun.provizija
        }
        // REKAPITULACIJA
        sb.append("-----------------------------------------------------------------\n")
        sb.append("REKAPITULACIJA:\n")
        sb.append("Ukupno transakcija: ${hronoloskiRacuni.size}\n")
        sb.append("Ukupno naplaceni racuni: ${String.format("%.2f", ukupnoIznosSvi)} KM\n")
        sb.append("Ukupno naplacena provizija: ${String.format("%.2f", ukupnoProvizijaSvi)} KM\n")
        val sveUkupno = ukupnoIznosSvi + ukupnoProvizijaSvi
        sb.append("UKUPNO: ${String.format("%.2f", sveUkupno)} KM\n\n")
        // APOENI
        sb.append("APOENSKA STRUKTURA:\n")
        if (apoeni.isEmpty()) {
            sb.append("Nema unesenih apoena.\n")
        } else {
            apoeni.forEach { linija ->
                sb.append("$linija\n")
            }
        }
        sb.append("\nUKUPAN IZNOS: ${String.format("%.2f", ukupnoFizicki)} KM\n")
        // POTPISI
        sb.append("\n\n\n\n")
        sb.append("Predao: ____________________     Primio: ____________________\n")
        // SNIMANJE
        var uri: Uri? = null
        return try {
            val datumVrijemeFajl = SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.getDefault()).format(Date())
            val siguranRadnikId = siguranDioImenaFajla(radnikId, "000")
            val sigurnoIme = siguranDioImenaFajla(radnikIme, "Nepoznat")
            val imeFajla = "Pregled_uplata_${siguranRadnikId}_${sigurnoIme}_${datumVrijemeFajl}.txt"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, imeFajla)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/Postonosa/$podPutanja")
            }
            uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            if (uri == null) {
                false
            } else {
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
                if (outputStream == null) {
                    obrisiNedovrsenFajl(context, uri)
                    false
                } else {
                    outputStream.use { it.write(sb.toString().toByteArray()) }
                    context.getSharedPreferences("PostonosaPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("zadnji_txt_uri", uri.toString())
                        .apply()
                    true
                }
            }
        } catch (e: Exception) {
            obrisiNedovrsenFajl(context, uri)
            false
        }
    }
}
