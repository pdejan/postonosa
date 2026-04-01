package com.popovic.postonosa

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfEksport {
    fun generisiPdf(context: Context, podPutanja: String, racuni: List<Racun>, apoeni: List<String>, ukupnoFizicki: Double) {
        val pdfDocument = PdfDocument()
        var stranicaBroj = 1
        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, stranicaBroj).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        val paint = Paint()

        val prefs = context.getSharedPreferences("PostonosaPrefs", Context.MODE_PRIVATE)
        val radnikIme = prefs.getString("ime_prezime", "Nepoznat") ?: "Nepoznat"
        val radnikId = prefs.getString("radnik_id", "000") ?: "000"
        val posta = prefs.getString("posta_naziv", "Pošta") ?: "Pošta"
        val datum = SimpleDateFormat("dd.MM.yyyy  |  HH:mm", Locale.getDefault()).format(Date())

        fun nacrtajZaglavljeTabele(pocetnaY: Float): Float {
            paint.textAlign = Paint.Align.LEFT
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            canvas.drawText("R.br.", 40f, pocetnaY, paint)
            canvas.drawText("Vrsta usluge", 80f, pocetnaY, paint)
            canvas.drawText("Iznos", 240f, pocetnaY, paint)
            canvas.drawText("Poštarina", 310f, pocetnaY, paint)
            canvas.drawText("Ukupno", 380f, pocetnaY, paint)
            canvas.drawText("Br.transakcije", 460f, pocetnaY, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            return pocetnaY + 25f
        }

        // ZAGLAVLJE DOKUMENTA
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 16f
        canvas.drawText("PREGLED IZVRŠENIH UPLATA NA DOSTAVNOM REJONU", 40f, 50f, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 14f
        canvas.drawText("JPM: $posta  |  $radnikId $radnikIme |  $datum", 40f, 70f, paint)
        canvas.drawLine(40f, 85f, 555f, 85f, paint)
        var yPos = nacrtajZaglavljeTabele(115f)
        var ukupnoIznosSvi = 0.0
        var ukupnoProvizijaSvi = 0.0
        val hronoloskiRacuni = racuni.reversed()
        hronoloskiRacuni.forEachIndexed { index, racun ->
            if (yPos > 780f) {
                pdfDocument.finishPage(page)
                stranicaBroj++
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, stranicaBroj).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPos = 50f
                yPos = nacrtajZaglavljeTabele(yPos)
            }
            canvas.drawText("${index + 1}.", 40f, yPos, paint)
            canvas.drawText(racun.tipUsluge, 80f, yPos, paint)
            canvas.drawText(String.format("%.2f", racun.iznos), 240f, yPos, paint)
            canvas.drawText(String.format("%.2f", racun.provizija), 310f, yPos, paint)
            canvas.drawText(String.format("%.2f", racun.iznos + racun.provizija), 380f, yPos, paint)
            yPos += 20f
            ukupnoIznosSvi += racun.iznos
            ukupnoProvizijaSvi += racun.provizija
        }
        canvas.drawLine(40f, yPos, 555f, yPos, paint)
        yPos += 40f
        if (yPos > 600f) {
            pdfDocument.finishPage(page)
            stranicaBroj++
            pageInfo = PdfDocument.PageInfo.Builder(595, 842, stranicaBroj).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPos = 50f
        }
        // REKAPITULACIJA
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("REKAPITULACIJA:", 40f, yPos, paint)
        yPos += 25f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Ukupno transakcija: ${hronoloskiRacuni.size}", 40f, yPos, paint)
        yPos += 20f
        canvas.drawText("Ukupno naplaćeni računi: ${String.format("%.2f", ukupnoIznosSvi)} KM", 40f, yPos, paint)
        yPos += 20f
        canvas.drawText("Ukupno naplaćena provizija: ${String.format("%.2f", ukupnoProvizijaSvi)} KM", 40f, yPos, paint)
        yPos += 40f
        // APOENSKA STRUKTURA
        val sredinaStranice = 595f / 2f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("APOENSKA STRUKTURA:", sredinaStranice, yPos, paint)
        yPos += 25f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        if (apoeni.isEmpty()) {
            canvas.drawText("Nema unešenih apoena.", sredinaStranice, yPos, paint)
            yPos += 20f
        } else {
            apoeni.forEach { linija ->
                canvas.drawText(linija, sredinaStranice, yPos, paint)
                yPos += 20f
            }
        }
        yPos += 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("UKUPAN IZNOS: ${String.format("%.2f", ukupnoFizicki)} KM", sredinaStranice, yPos, paint)
        yPos += 80f
        // POTPIS NA DNU
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawLine(40f, yPos, 240f, yPos, paint)
        canvas.drawText("Predao:", 40f, yPos + 20f, paint)
        canvas.drawLine(350f, yPos, 555f, yPos, paint)
        canvas.drawText("Primio:", 350f, yPos + 20f, paint)

        pdfDocument.finishPage(page)
        try {
            val datumVrijemeFajl = SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.getDefault()).format(Date())
            val sigurnoIme = radnikIme.replace(" ", "_")
            val imeFajla = "Pregled_uplata_${radnikId}_${sigurnoIme}_${datumVrijemeFajl}.pdf"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, imeFajla)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/Postonosa/$podPutanja")
            }
            val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            if (uri != null) {
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    pdfDocument.writeTo(outputStream)
                    outputStream.close()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Greška pri snimanju PDF-a!", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}