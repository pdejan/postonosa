# Poštonoša (Android Aplikacija)

**Poštonoša** je aplikacija napravljen specifično za poštare u Bosni i Hercegovini (i regionu?). Aplikacija omogućava jednostavnu evidenciju i unos uplata komunalnih usluga na dostavnom rejonu koje pošte ovdje nude uz par pogodnosti i sve to naravno offline.

## Glavne funkcije
* **Offline:** Aplikacija radi bez interneta.
* **Kalkulator:** Automatski računa proviziju na osnovu unesenog iznosa.
* **Pregled:** Pregled svih unešenih uplata kao i mogućnost brisanja u slučaju greške.
* **Trenutna stranka:** Aplikacija pokazuje stanje za naplatu trenutne stranke.
* **Eksport izvještaja:** Na kraju rada, jednim klikom generišite **PDF** i/ili **TXT** izvještaj (Pregled uplata) koji se automatski spašava u organizovane, datirane foldere na uređaju i spreman je za printanje.
* **Podešavanja:** Aplikacija nudi par opcija za podešavanje i to:
 1. Modifikacija i unos usluga po želji.
 2. Izbor formata Pregleda uplata koji se generiše.
 3. Automatsko ažuriranje provizije. (Zahtjeva internet konekciju)
* **Sigurnost:** Aplikacija radi potpuno offline, ne prikuplja nikakve podatke pa čak ni analitike bilo o vama, vašem telefonu ili o uplatama koje u nju unesete. Ne komunicira sa vanjskim serverom. Sva pohrana podata se odvija na samom uređaju i nikada ga ne napušta. Aplikacija **mora** imati pristup internetu samo kada morate/želite ažurirati proviziju koja se obračunava.

## Informacije o aplikaciji i kodu
* **Automatsko ažuriranje provizije:** Za računanje provizije kao i stope, aplikacija gleda fajl **naknada.json** u assets folderu. Da bi se omogućilo jednostavno ažuriranje provizije na klik jednog dugmeta u podešavanjima bez potrebnog ažuriranja same aplikacije taj json fajl mora biti hostan negdje online, te varijabla u PodesavanjaEkran.kt mora sadržavati tačnu putanju ka datom fajlu.
* **Boje:** Tema aplikacije je urađena u dvije akcentne boje, GlavnaBoja (u ovom slučaju žuta) i SporednaBoja (crna). Ukoliko se mjenja akcentni izgled aplikacije dovoljno je promjeniti ove dvije varijable u Boje.kt

## Verzija
Trenutna verzija: **v1.3**

![Login screen](https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_login.jpg)
![Dashboard](https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_main.jpg)
![Entry](https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_entry.jpg)
![Overview](https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_overview.jpg)
![End](https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_end.jpg)
![Settings](https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_settings_1.jpg)
![Settings](https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_settings_2.jpg)

# ENGLISH

* **Poštonoša** is an app created specifically for postmen in Bosnia and Herzegovina (and the region?). The application allows for simple logging and entry of utility bill payments within a delivery area, offering a few convenient perks, all of this, of course, completely offline.

## Main Features
* **Offline:** The app works without an internet connection.
* **Calculator:** Automatically calculates the commission based on the entered amount.
* **Overview:** A review of all entered payments, as well as the ability to delete entries in case of an error.
* **Current Customer:** The app displays the current total collection amount for the customer you are currently serving.
* **Report Export:** At the end of the day, generate a PDF and/or TXT report (Payment Overview) with a single click. The files are automatically saved into organized, dated folders on the device and are ready for printing.
* **Settings:** The app offers a few configuration options, including:
1. Custom modification and entry of available services.
2. Choosing the format for the generated Payment Overview.
3. Automatic commission updates (Requires an internet connection).
* **Security & Privacy:** The app works completely offline. It does not collect any data, not even analytics about you, your phone, or the payments you enter. It does not communicate with any external servers. All data storage takes place locally on the device and never leaves it. The app only **requires** internet access when you need/want to update the calculated commission rates.

## App and Code Information
* **Automatic Commission Update:** To calculate the commission and rates, the app reads the **naknada.json** file located in the assets folder. To enable simple commission updates with a single click in the settings without needing to update the app itself, this JSON file must be hosted online, and the corresponding variable in PodesavanjaEkran.kt must contain the exact URL path to that file.
* **Colors:** The app's theme is built around two accent colors: GlavnaBoja (Main Color, yellow in this case) and SporednaBoja (Secondary Color, black). If you want to change the app's visual identity, you simply need to modify these two variables in Boje.kt.

## Version
Current version: **v1.3**
