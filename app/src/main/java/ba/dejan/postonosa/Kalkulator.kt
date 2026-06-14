package ba.dejan.postonosa

import android.content.Context
import org.json.JSONObject
import java.io.File

data class FiksnaProvizija(val id: String, val naziv: String, val iznos: Double)
object Kalkulator {
    // Zaokruži na 2 decimale da procentualna pravila ne proizvedu pod-centne iznose
    private fun zaokruziNaFeninge(iznos: Double): Double = Math.round(iznos * 100.0) / 100.0
    private fun procitajJson(context: Context): String {
        val lokalniFajl = File(context.filesDir, "naknada.json")
        return if (lokalniFajl.exists()) {
            lokalniFajl.readText()
        } else {
            context.assets.open("naknada.json").bufferedReader().use { it.readText() }
        }
    }
    fun dohvatiFiksneProvizije(context: Context): List<FiksnaProvizija> {
        val jsonString = try {
            procitajJson(context)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
        return dohvatiFiksneProvizijeIzJsona(jsonString)
    }
    fun dohvatiFiksneProvizijeIzJsona(jsonString: String): List<FiksnaProvizija> {
        val lista = mutableListOf<FiksnaProvizija>()
        try {
            val json = JSONObject(jsonString)
            if (json.has("fiksne_naknade")) {
                val niz = json.getJSONArray("fiksne_naknade")
                for (i in 0 until niz.length()) {
                    val obj = niz.getJSONObject(i)
                    lista.add(FiksnaProvizija(obj.getString("id"), obj.getString("naziv"), obj.getDouble("iznos")))
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
        return lista
    }
    fun dohvatiIznosFiksne(context: Context, fiksnaId: String): Double? {
        val fiksneListe = dohvatiFiksneProvizije(context)
        return fiksneListe.find { it.id == fiksnaId }?.iznos
    }
    fun izracunaj(context: Context, iznos: Double): Double {
        if (iznos <= 0.0) return 0.0
        return try {
            izracunajIzJsona(procitajJson(context), iznos)
        } catch (e: Exception) {
            // Sigurnosni fallback AKO nekim čudom nema fajla
            when {
                iznos <= 50.0 -> 1.80
                iznos <= 100.0 -> 2.10
                iznos <= 300.0 -> 2.40
                iznos <= 500.0 -> 2.70
                iznos <= 1000.0 -> 3.00
                iznos <= 2000.0 -> 3.50
                iznos <= 5000.0 -> 4.00
                iznos <= 10000.0 -> 5.00
                else -> zaokruziNaFeninge(iznos * (0.1 / 100.0))
            }
        }
    }
    fun izracunajIzJsona(jsonString: String, iznos: Double): Double {
        if (iznos <= 0.0) return 0.0
        val json = JSONObject(jsonString)
        val provizijeNiz = json.getJSONArray("naknada")
        var izracunataProvizija = 0.0
        // Parse iz jsona
        for (i in 0 until provizijeNiz.length()) {
            val pravilo = provizijeNiz.getJSONObject(i)
            val doIznosa = pravilo.getDouble("do_iznosa")
            // Fallback
            val fiksno = pravilo.optDouble("fiksno", 0.0)
            val procenat = pravilo.optDouble("procenat", 0.0)
            // Racunanje
            if (iznos <= doIznosa) {
                izracunataProvizija = if (procenat > 0.0) {
                    iznos * (procenat / 100.0)
                } else {
                    fiksno
                }
                break
            }
        }
        return zaokruziNaFeninge(izracunataProvizija)
    }
}
