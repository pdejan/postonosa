package ba.dejan.postonosa

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// In-memory zamjena da se ucitajUsluge/spasiUsluge testiraju bez Androida
class FakeSharedPreferences : SharedPreferences {
    private val mapa = mutableMapOf<String, Any?>()

    override fun getString(key: String?, defValue: String?): String? =
        if (mapa.containsKey(key)) mapa[key] as String? else defValue

    override fun getAll(): MutableMap<String, *> = mapa
    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? = defValues
    override fun getInt(key: String?, defValue: Int): Int = defValue
    override fun getLong(key: String?, defValue: Long): Long = defValue
    override fun getFloat(key: String?, defValue: Float): Float = defValue
    override fun getBoolean(key: String?, defValue: Boolean): Boolean = defValue
    override fun contains(key: String?): Boolean = mapa.containsKey(key)
    override fun edit(): SharedPreferences.Editor = FakeEditor()
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

    inner class FakeEditor : SharedPreferences.Editor {
        override fun putString(key: String?, value: String?): SharedPreferences.Editor {
            mapa[key!!] = value
            return this
        }
        override fun remove(key: String?): SharedPreferences.Editor {
            mapa.remove(key)
            return this
        }
        override fun clear(): SharedPreferences.Editor {
            mapa.clear()
            return this
        }
        override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor = this
        override fun putInt(key: String?, value: Int): SharedPreferences.Editor = this
        override fun putLong(key: String?, value: Long): SharedPreferences.Editor = this
        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = this
        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = this
        override fun commit(): Boolean = true
        override fun apply() {}
    }
}

class UslugeTest {

    @Test
    fun prvoPokretanjeDajeDefaultneUsluge() {
        val prefs = FakeSharedPreferences()
        val usluge = ucitajUsluge(prefs)
        assertEquals(8, usluge.size)
        assertEquals("Elektroprivreda", usluge[0].naziv)
        val rtv = usluge.first { it.naziv == "RTV taksa" }
        assertTrue(rtv.bezProvizije)
        assertFalse(usluge.first { it.naziv == "Platni promet" }.bezProvizije)
    }

    @Test
    fun sveObrisanoVracaSamoPlatniPromet() {
        val prefs = FakeSharedPreferences()
        prefs.edit().putString("lista_usluga", "").apply()
        val usluge = ucitajUsluge(prefs)
        assertEquals(listOf(TipUsluge("Platni promet")), usluge)
    }

    @Test
    fun spasiPaUcitajVracaIstuListu() {
        val prefs = FakeSharedPreferences()
        val original = listOf(
            TipUsluge("Elektroprivreda"),
            TipUsluge("RTV taksa", bezProvizije = true),
            TipUsluge("Uplate rate kredita", fiksnaId = "krediti")
        )
        spasiUsluge(prefs, original)
        assertEquals(original, ucitajUsluge(prefs))
    }

    @Test
    fun formatSerijalizacijeOstajeStabilan() {
        // Ovaj format je na uređajima postojećih korisnika — promjena mora imati migraciju u ucitajUsluge
        val prefs = FakeSharedPreferences()
        spasiUsluge(prefs, listOf(TipUsluge("Voda"), TipUsluge("Krediti", fiksnaId = "krediti")))
        assertEquals("Voda|false|null;Krediti|false|krediti", prefs.getString("lista_usluga", null))
    }

    @Test
    fun migracijaStarogFormataSamoNaziv() {
        val prefs = FakeSharedPreferences()
        prefs.edit().putString("lista_usluga", "Elektroprivreda;Mtel").apply()
        val usluge = ucitajUsluge(prefs)
        assertEquals(2, usluge.size)
        assertEquals(TipUsluge("Elektroprivreda", bezProvizije = false, fiksnaId = null), usluge[0])
        assertEquals(TipUsluge("Mtel", bezProvizije = false, fiksnaId = null), usluge[1])
    }

    @Test
    fun migracijaStarogFormataSaDvaDijela() {
        val prefs = FakeSharedPreferences()
        prefs.edit().putString("lista_usluga", "RTV taksa|true;Voda|false").apply()
        val usluge = ucitajUsluge(prefs)
        assertTrue(usluge[0].bezProvizije)
        assertNull(usluge[0].fiksnaId)
        assertFalse(usluge[1].bezProvizije)
    }

    @Test
    fun nazivSaZabranjenimZnakovimaSeCistiPrijeSnimanja() {
        // ; i | su separatori formata — ne smiju preživjeti u nazivu jer bi razbili parsiranje
        val prefs = FakeSharedPreferences()
        spasiUsluge(prefs, listOf(TipUsluge("Stru|ja;komunalije"), TipUsluge("Voda")))
        val usluge = ucitajUsluge(prefs)
        assertEquals(2, usluge.size)
        assertEquals("Strujakomunalije", usluge[0].naziv)
        assertEquals("Voda", usluge[1].naziv)
    }

    @Test
    fun praznNazivSePreskacePriSnimanju() {
        val prefs = FakeSharedPreferences()
        spasiUsluge(prefs, listOf(TipUsluge("   "), TipUsluge("Voda")))
        assertEquals(listOf(TipUsluge("Voda")), ucitajUsluge(prefs))
    }

    @Test
    fun normalizacijaNazivaUsluge() {
        assertEquals("Struja", normalizujNazivUsluge("  Struja  "))
        assertEquals("Platni promet", normalizujNazivUsluge("Platni    promet"))
        assertEquals("ABC", normalizujNazivUsluge("A|B;C"))
        // Limit od 30 karaktera
        assertEquals(30, normalizujNazivUsluge("X".repeat(50)).length)
    }
}
