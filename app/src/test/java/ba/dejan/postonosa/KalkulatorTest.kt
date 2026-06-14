package ba.dejan.postonosa

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class KalkulatorTest {

    // Stvarni cjenovnik koji se isporučuje uz aplikaciju — testovi pucaju ako se granice pomjere nenamjerno
    private val stvarniJson = File("src/main/assets/naknada.json").readText()

    @Test
    fun nulaINegativanIznosDajuNultuProviziju() {
        assertEquals(0.0, Kalkulator.izracunajIzJsona(stvarniJson, 0.0), 0.0)
        assertEquals(0.0, Kalkulator.izracunajIzJsona(stvarniJson, -5.0), 0.0)
    }

    @Test
    fun graniceStvarnogCjenovnika() {
        assertEquals(1.80, Kalkulator.izracunajIzJsona(stvarniJson, 0.01), 1e-9)
        assertEquals(1.80, Kalkulator.izracunajIzJsona(stvarniJson, 50.0), 1e-9)
        assertEquals(2.10, Kalkulator.izracunajIzJsona(stvarniJson, 50.01), 1e-9)
        assertEquals(2.10, Kalkulator.izracunajIzJsona(stvarniJson, 100.0), 1e-9)
        assertEquals(2.40, Kalkulator.izracunajIzJsona(stvarniJson, 300.0), 1e-9)
        assertEquals(2.70, Kalkulator.izracunajIzJsona(stvarniJson, 500.0), 1e-9)
        assertEquals(3.00, Kalkulator.izracunajIzJsona(stvarniJson, 1000.0), 1e-9)
        assertEquals(3.50, Kalkulator.izracunajIzJsona(stvarniJson, 2000.0), 1e-9)
        assertEquals(4.00, Kalkulator.izracunajIzJsona(stvarniJson, 5000.0), 1e-9)
        assertEquals(5.00, Kalkulator.izracunajIzJsona(stvarniJson, 10000.0), 1e-9)
    }

    @Test
    fun preko10000PrimjenjujeProcenat() {
        // Zadnje pravilo: 0.10%
        assertEquals(20.0, Kalkulator.izracunajIzJsona(stvarniJson, 20000.0), 1e-9)
        // Maksimalan iznos koji UnosEkran dozvoljava (regex limit od 999999.99)
        // 999999.99 * 0.001 = 999.99999 → zaokruženo na feninge = 1000.00
        assertEquals(1000.00, Kalkulator.izracunajIzJsona(stvarniJson, 999999.99), 1e-9)
    }

    @Test
    fun provizijaSeUvijekVracaNaFeningPreciznost() {
        // 0.10% od 10005 = 10.005 (pola feninga) → mora se zaokružiti na 10.01,
        // inače zbir više uplata ne može da se poklopi sa fizičkim novcem na kraju dana
        val provizija = Kalkulator.izracunajIzJsona(stvarniJson, 10005.0)
        assertEquals(10.01, provizija, 1e-9)
        // Tri takve uplate moraju dati zbir koji je tačno na fening
        val ukupnoPoUplati = 10005.0 + provizija
        val zbir = 3 * ukupnoPoUplati
        assertEquals(0.0, Math.round(zbir * 100.0) / 100.0 - zbir, 1e-9)
    }

    @Test
    fun procenatImaPrednostNadFiksnimAkoSuObaNavedena() {
        val json = """{"naknada":[{"do_iznosa":100.0,"fiksno":5.0,"procenat":2.0}]}"""
        assertEquals(1.0, Kalkulator.izracunajIzJsona(json, 50.0), 1e-9)
    }

    @Test
    fun pravilaBezFiksnogIProcentaDajuNulu() {
        val json = """{"naknada":[{"do_iznosa":100.0}]}"""
        assertEquals(0.0, Kalkulator.izracunajIzJsona(json, 50.0), 0.0)
    }

    @Test
    fun iznosPrekoZadnjegPravilaDajeNulu() {
        // Dokumentovan edge case: bez catch-all pravila provizija je 0. U praksi nedostižno —
        // stvarni cjenovnik pokriva do 9.999.999, a UnosEkran ograničava unos na 999999.99.
        val json = """{"naknada":[{"do_iznosa":100.0,"fiksno":1.0,"procenat":0.0}]}"""
        assertEquals(0.0, Kalkulator.izracunajIzJsona(json, 200.0), 0.0)
    }

    @Test
    fun neispravanJsonBacaIzuzetak() {
        // izracunaj(context, ...) ovo hvata i koristi hardkodirani fallback cjenovnik
        assertThrows(Exception::class.java) {
            Kalkulator.izracunajIzJsona("ovo nije json", 50.0)
        }
        assertThrows(Exception::class.java) {
            Kalkulator.izracunajIzJsona("""{"pogresan_kljuc":[]}""", 50.0)
        }
    }

    @Test
    fun fiksneNaknadeIzStvarnogCjenovnika() {
        val fiksne = Kalkulator.dohvatiFiksneProvizijeIzJsona(stvarniJson)
        assertEquals(2, fiksne.size)
        assertEquals(FiksnaProvizija("krediti", "Uplate rate kredita", 1.8), fiksne[0])
        assertEquals(FiksnaProvizija("registracijaVozila", "Registracija vozila", 1.6), fiksne[1])
    }

    @Test
    fun fiksneNaknadeNedostajuIliNeispravanJsonDajuPraznuListu() {
        assertTrue(Kalkulator.dohvatiFiksneProvizijeIzJsona("""{"naknada":[]}""").isEmpty())
        assertTrue(Kalkulator.dohvatiFiksneProvizijeIzJsona("ovo nije json").isEmpty())
    }
}
