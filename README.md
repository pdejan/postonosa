# Poštonoša (Android Aplikacija)

**Poštonoša** je aplikacija napravljen specifično za poštare u Bosni i Hercegovini (i regionu?). Aplikacija omogućava jednostavnu evidenciju i unos uplata komunalnih usluga na dostavnom rejonu koje pošte ovdje nude uz par pogodnosti i sve to naravno offline.

## Glavne funkcije
* **Offline:** Aplikacija radi bez interneta.
* **Kalkulator:** Automatski računa proviziju na osnovu unesenog iznosa.
* **Pregled:** Pregled svih unešenih uplata kao i mogućnost brisanja u slučaju greške.
* **Trenutna stranka:** Aplikacija pokazuje stanje za naplatu trenutne stranke.
* **Eksport izvještaja:** Na kraju rada, jednim klikom generišite **PDF** i/ili **TXT** izvještaj (Pregled uplata) koji se automatski spašava u organizovane, datirane foldere na uređaju i spreman je za printanje.
* **Podešavanja:** Aplikacija nudi par opcija za podešavanje i to:
* 1. Modifikacija i unos usluga po želji.
  2. Izbor formata Pregleda uplata koji se generiše.
  3. Automatsko ažuriranje provizije. (Zahtjeva internet konekciju)
* **Sigurnost:** Aplikacija radi potpuno offline, ne prikuplja nikakve podatke pa čak ni analitike bilo o vama, vašem telefonu ili o uplatama koje u nju unesete. Ne komunicira sa vanjskim serverom. Sva pohrana podata se odvija na samom uređaju i nikada ga ne napušta. Aplikacija **mora** imati pristup internetu samo kada morate/želite ažurirati proviziju koja se obračunava.

## Informacije o aplikaciji i kodu
* **Automatsko ažuriranje provizije:** Za računanje provizije kao i stope, aplikacija gleda fajl **naknada.json** u assets folderu. Da bi se omogućilo jednostavno ažuriranje provizije na klik jednog dugmeta u podesavanjima bez potrebnog ažuriranja same aplikacije taj json fajl mora biti hostan online negdje, te varijabla u PodesavanjaEkran.kt mora sadržavati tačnu putanju ka datom fajlu.
* **Boje:** Tema aplikacije je urađena u dvije akcentne boje, GlavnaBoja (u ovom slučaju žuta) i SporednaBoja (crna). Ukoliko se mjenja akcentni izgled aplikacije dovoljno je promjeniti ove dvije varijable u Boje.kt

## Verzija
Trenutna verzija: **v1.3**
