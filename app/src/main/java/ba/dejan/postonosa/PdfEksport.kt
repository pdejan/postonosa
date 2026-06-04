package ba.dejan.postonosa

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
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
        val rejon = prefs.getString("radni_rejon", "000") ?: "000"
        val posta = prefs.getString("posta_naziv", "Pošta") ?: "Pošta"
        val datum = SimpleDateFormat("dd.MM.yyyy  |  HH:mm", Locale.getDefault()).format(Date())

    fun nacrtajZaglavljeTabele(trenutniCanvas: Canvas, pocetnaY: Float): Float {
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10f
        trenutniCanvas.drawText("R.br.", 40f, pocetnaY, paint)
        trenutniCanvas.drawText("Vrsta usluge", 80f, pocetnaY, paint)
        trenutniCanvas.drawText("Iznos", 240f, pocetnaY, paint)
        trenutniCanvas.drawText("Poštarina", 310f, pocetnaY, paint)
        trenutniCanvas.drawText("Ukupno", 380f, pocetnaY, paint)
        trenutniCanvas.drawText("Br.transakcije", 460f, pocetnaY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        return pocetnaY + 15f
    }
        // ZAGLAVLJE DOKUMENTA
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f
        canvas.drawText("PREGLED IZVRŠENIH UPLATA NA DOSTAVNOM REJONU", 40f, 25f, paint)
        canvas.drawText("JPM: $posta  |  $radnikId $radnikIme", 40f, 40f, paint)
        canvas.drawText("Rejon: $rejon  |  Datum: $datum", 40f, 55f, paint)
        canvas.drawLine(40f, 65f, 555f, 65f, paint)
        paint.textSize = 10f
        var yPos = nacrtajZaglavljeTabele(canvas, 80f)
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
                yPos = 30f
                yPos = nacrtajZaglavljeTabele(canvas, yPos)
            }
                canvas.drawText("${index + 1}.", 40f, yPos, paint)
                canvas.drawText(racun.tipUsluge, 80f, yPos, paint)
                canvas.drawText(String.format("%.2f", racun.iznos), 240f, yPos, paint)
                canvas.drawText(String.format("%.2f", racun.provizija), 310f, yPos, paint)
                canvas.drawText(String.format("%.2f", racun.iznos + racun.provizija), 380f, yPos, paint)
                yPos += 14f
                ukupnoIznosSvi += racun.iznos
                ukupnoProvizijaSvi += racun.provizija
        }
        // ZAVRŠETAK TABELE
        canvas.drawLine(40f, yPos, 555f, yPos, paint)
        yPos += 15f
        // Rekapitulacija (~90f) + Apoeni (svaki po 16f) + Potpis i razmaci (~80f)
        val potrebanProstor = 170f + (apoeni.size * 16f)
        if (yPos + potrebanProstor > 800f) {
            pdfDocument.finishPage(page)
            stranicaBroj++
            pageInfo = PdfDocument.PageInfo.Builder(595, 842, stranicaBroj).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPos = 50f
        }
        // REKAPITULACIJA
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("REKAPITULACIJA:", 40f, yPos, paint)
        yPos += 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Ukupno transakcija: ${hronoloskiRacuni.size}", 40f, yPos, paint)
        yPos += 14f
        canvas.drawText("Ukupno naplaćeni računi: ${String.format("%.2f", ukupnoIznosSvi)} KM", 40f, yPos, paint)
        yPos += 14f
        canvas.drawText("Ukupno naplaćena provizija: ${String.format("%.2f", ukupnoProvizijaSvi)} KM", 40f, yPos, paint)
        yPos += 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val sveUkupno = ukupnoIznosSvi + ukupnoProvizijaSvi
        canvas.drawText("UKUPNO: ${String.format("%.2f", sveUkupno)} KM", 40f, yPos, paint)
        yPos += 30f
        // APOENSKA STRUKTURA
        val sredinaStranice = 595f / 2f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("APOENSKA STRUKTURA:", sredinaStranice, yPos, paint)
        yPos += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        if (apoeni.isEmpty()) {
            canvas.drawText("Nema unešenih apoena.", sredinaStranice, yPos, paint)
            yPos += 16f
        } else {
            apoeni.forEach { linija ->
                if (yPos > 780f) {
                    pdfDocument.finishPage(page)
                    stranicaBroj++
                    pageInfo = PdfDocument.PageInfo.Builder(595, 842, stranicaBroj).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = 50f
                }
                canvas.drawText(linija, sredinaStranice, yPos, paint)
                yPos += 16f
            }
        }
        yPos += 5f
        if (yPos > 750f) {
            pdfDocument.finishPage(page)
            stranicaBroj++
            pageInfo = PdfDocument.PageInfo.Builder(595, 842, stranicaBroj).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPos = 50f
        }
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("UKUPAN IZNOS: ${String.format("%.2f", ukupnoFizicki)} KM", sredinaStranice, yPos, paint)
        yPos += 50f
        // POTPIS NA DNU
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawLine(40f, yPos, 240f, yPos, paint)
        canvas.drawText("Predao:", 40f, yPos + 16f, paint)
        canvas.drawLine(350f, yPos, 555f, yPos, paint)
        canvas.drawText("Primio:", 350f, yPos + 16f, paint)
        pdfDocument.finishPage(page)
        try {
            val datumVrijemeFajl = SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.getDefault()).format(Date())
            val siguranRadnikId = siguranDioImenaFajla(radnikId, "000")
            val sigurnoIme = siguranDioImenaFajla(radnikIme, "Nepoznat")
            val imeFajla = "Pregled_uplata_${siguranRadnikId}_${sigurnoIme}_${datumVrijemeFajl}.pdf"
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
                    context.getSharedPreferences("PostonosaPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("zadnji_pdf_uri", uri.toString())
                        .apply()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Greška pri snimanju PDF-a!", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}
