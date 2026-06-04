package ba.dejan.postonosa

private const val MAKS_DUZINA_KRATKOG_TEKSTA = 30

fun pripremiKratkiTekstZaUnos(unos: String, maksDuzina: Int = MAKS_DUZINA_KRATKOG_TEKSTA): String {
    return unos
        .filter { it >= ' ' && it != '\u007F' }
        .replace(Regex("\\s{2,}"), " ")
        .take(maksDuzina)
}

fun normalizujKratkiTekst(unos: String, maksDuzina: Int = MAKS_DUZINA_KRATKOG_TEKSTA): String {
    return pripremiKratkiTekstZaUnos(unos, maksDuzina)
        .replace(Regex("\\s+"), " ")
        .trim()
        .take(maksDuzina)
        .trimEnd()
}

fun siguranDioImenaFajla(vrijednost: String, fallback: String): String {
    val ocisceno = normalizujKratkiTekst(vrijednost, maksDuzina = 60)
        .replace(Regex("[\\\\/:*?\"<>|]"), "_")
        .replace(Regex("\\s+"), "_")
        .replace(Regex("_+"), "_")
        .trim('_', '.')

    return ocisceno.ifBlank { fallback }
}
