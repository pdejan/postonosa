package com.popovic.postonosa

import android.content.Context
import org.json.JSONObject
import java.io.File

object Kalkulator {
    private fun procitajJson(context: Context): String {
        val lokalniFajl = File(context.filesDir, "naknada.json")
        return if (lokalniFajl.exists()) {
            lokalniFajl.readText()
        } else {
            context.assets.open("naknada.json").bufferedReader().use { it.readText() }
        }
    }
    fun izracunaj(context: Context, iznos: Double): Double {
        if (iznos <= 0.0) return 0.0

        return try {
            val jsonString = procitajJson(context)
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
            izracunataProvizija
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
                else -> iznos * (0.1 / 100.0)
            }
        }
    }
    fun dohvatiUsluge(context: Context): List<String> {
        return try {
            val jsonString = procitajJson(context)
            val json = JSONObject(jsonString)
            val niz = json.getJSONArray("usluge")
            List(niz.length()) { niz.getString(it) }
        } catch (e: Exception) {
            listOf("Platni promet")
        }
    }
}