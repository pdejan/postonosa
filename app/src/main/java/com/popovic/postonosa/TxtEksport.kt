package com.popovic.postonosa

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TxtEksport {
    @SuppressLint("DefaultLocale")
    fun generisiTxt(context: Context, podPutanja: String, racuni: List<Racun>, apoeni: List<String>, ukupnoFizicki: Double) {
        val prefs = context.getSharedPreferences("PostonosaPrefs", Context.MODE_PRIVATE)
        val radnikIme = prefs.getString("ime_prezime", "Nepoznat") ?: "Nepoznat"
        val radnikId = prefs.getString("radnik_id", "000") ?: "000"
        val posta = prefs.getString("posta_naziv", "Posta") ?: "Posta"
        val datum = SimpleDateFormat("dd.MM.yyyy | HH:mm", Locale.getDefault()).format(Date())
        val sb = java.lang.StringBuilder()
        // ZAGLAVLJE
        sb.append("PREGLED IZVRSENIH UPLATA NA DOSTAVNOM REJONU\n")
        sb.append("JPM: $posta | $radnikId $radnikIme | $datum\n")
        sb.append("--------------------------------------------------------------------------------\n")
        // R.br(5) + Vrsta(25) + Iznos(10) + Postarina(10) + Ukupno(11) + Transakcija(14) + 5 razmaka = 80 karaktera
        sb.append(String.format("%-5s %-25s %-10s %-10s %-11s %-14s\n", "R.br.", "Vrsta uplate", "Iznos", "Postarina", "Ukupno", "Br.transakcije"))
        sb.append("--------------------------------------------------------------------------------\n")
        var ukupnoIznosSvi = 0.0
        var ukupnoProvizijaSvi = 0.0
        val hronoloskiRacuni = racuni.reversed()
        // REDOVI SA RACUNIMA
        hronoloskiRacuni.forEachIndexed { index, racun ->
            val usluga = if (racun.tipUsluge.length > 24) racun.tipUsluge.take(22) + ".." else racun.tipUsluge
            sb.append(String.format("%-5s %-25s %-10.2f %-10.2f %-11.2f %-14s\n",
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
        sb.append("--------------------------------------------------------------------------------\n")
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
        sb.append("Predao: ____________________          Primio: ____________________\n")
        // SNIMANJE
        try {
            val datumVrijemeFajl = SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.getDefault()).format(Date())
            val sigurnoIme = radnikIme.replace(" ", "_")
            val imeFajla = "Pregled_uplata_${radnikId}_${sigurnoIme}_${datumVrijemeFajl}.txt"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, imeFajla)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/Postonosa/$podPutanja")
            }
            val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            if (uri != null) {
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    outputStream.write(sb.toString().toByteArray())
                    outputStream.close()
                    context.getSharedPreferences("PostonosaPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("zadnji_txt_uri", uri.toString())
                        .apply()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Greška pri snimanju TXT-a!", Toast.LENGTH_SHORT).show()
        }
    }
}