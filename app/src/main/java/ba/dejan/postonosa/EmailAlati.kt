package ba.dejan.postonosa

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object EmailAlati {
    fun posaljiZadnjiIzvjestaj(context: Context) {
        val prefs = context.getSharedPreferences("PostonosaPrefs", Context.MODE_PRIVATE)
        val emailAdresa = prefs.getString("email_za_slanje", "") ?: ""
        val imeRadnika = prefs.getString("ime_prezime", "Poštonoša") ?: "Poštonoša"
        val pdfUriString = prefs.getString("zadnji_pdf_uri", "") ?: ""
        val txtUriString = prefs.getString("zadnji_txt_uri", "") ?: ""
        if (emailAdresa.isBlank()) {
            Toast.makeText(context, "Unesite email pošte na koji šaljete pregled u polje iznad!", Toast.LENGTH_LONG).show()
            return
        }
        val uris = ArrayList<Uri>()
        if (pdfUriString.isNotBlank()) uris.add(Uri.parse(pdfUriString))
        if (txtUriString.isNotBlank()) uris.add(Uri.parse(txtUriString))
        if (uris.isEmpty()) {
            Toast.makeText(context, "Još niste snimili nijedan pregled uplata za slanje!", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAdresa))
            putExtra(Intent.EXTRA_SUBJECT, "Pregled izvršenih uplata na dostavnom rejonu - $imeRadnika")
            putExtra(Intent.EXTRA_TEXT, "Poštovani,\n\nU prilogu Vam dostavljam pregled izvršenih uplata na dostavnom rejonu.\n\nPozdrav!")
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Pošalji pregled uplata putem..."))
        } catch (e: Exception) {
            Toast.makeText(context, "Nemate instaliranu aplikaciju za email!", Toast.LENGTH_SHORT).show()
        }
    }
}