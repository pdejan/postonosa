# Poštonoša (Android Aplikacija)

<a href="https://play.google.com/store/apps/details?id=ba.dejan.postonosa">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" width="200">
</a>

**Poštonoša** je aplikacija napravljena za poštare u Bosni i Hercegovini (Pošte Srpske). Aplikacija omogućava jednostavnu evidenciju i unos uplata režijskih računa na dostavnom rejonu te generisanje pregleda uz par pogodnosti i sve to offline.

## Glavne funkcije
* **Offline:** Aplikacija radi bez interneta.
* **Kalkulator:** Automatski računa proviziju na osnovu unesenog iznosa.
* **Pregled:** Pregled svih unešenih uplata kao i mogućnost brisanja u slučaju greške.
* **Trenutna stranka:** Aplikacija pokazuje stanje za naplatu trenutne stranke.
* **Eksport izvještaja:** Na kraju rada, jednim klikom generišite **PDF** i/ili **TXT** izvještaj (Pregled uplata) koji se automatski spašava u organizovane, datirane foldere na uređaju i spreman je za printanje.
* **Podešavanja:** Aplikacija nudi par opcija za podešavanje i to:
 1. Modifikacija i unos usluga po želji.
 2. Pošalji pregled uplata emailom 
 3. Izbor formata Pregleda uplata koji se generiše.
 4. Automatsko ažuriranje provizije. (Zahtjeva internet konekciju)
* **Sigurnost:** Aplikacija radi potpuno offline, ne prikuplja nikakve podatke pa čak ni analitike bilo o vama, vašem telefonu ili o uplatama koje u nju unesete. Ne komunicira sa vanjskim serverom. Sva pohrana podata se odvija na samom uređaju i nikada ga ne napušta. Aplikacija **mora** imati pristup internetu samo kada morate/želite ažurirati proviziju koja se obračunava i poslati pregled uplata emailom.

## Informacije o aplikaciji i kodu
* **Automatsko ažuriranje provizije:** Za računanje provizije kao i stope, aplikacija gleda fajl **naknada.json** u assets folderu. Da bi se omogućilo jednostavno ažuriranje provizije na klik jednog dugmeta u podešavanjima bez potrebnog ažuriranja same aplikacije, taj JSON fajl mora biti hostan online, da bi ga aplikacija mogla povući i spasiti ukoliko je potrebno ažurirati samu proviziju. Varijable u **PodesavanjaEkran.kt** moraju sadržavati tačnu putanju ka datom fajlu kao i njegovom potpisu. Fajl **naknada.json** je potpisan (signed) sa RSA + SHA-256. Javni ključ je dio same aplikacije u **NaknadaPotpis.kt**, a alati za sam potpis u **tools** folderu. Aplikacija će odbaciti JSON fajl koji nije potpisan ovim sistemom.
* **Boje:** Tema aplikacije je urađena u dvije akcentne boje, GlavnaBoja (u ovom slučaju žuta) i SporednaBoja (crna). Ukoliko se mjenja akcentni izgled aplikacije dovoljno je promjeniti ove dvije varijable u ui.theme > Color.kt
* **Resursi trećih lica:** Izvorni kod aplikacije je licenciran pod MIT licencom, osim ako nije drugačije navedeno. Ikona aplikacije i povezani launcher resursi su bazirani na licenciranom **Flaticon/Freepik** resursu i nisu relicencirani pod MIT licencom. Ja posjedujem licencu za ovu primjenu u aplikaciji koju objavljujem, a za svaku drugu upotrebu morate imati vlastito pravo korištenja ili ispoštovati originalnu Flaticon/Freepik licencu. Attribution: Email icons created by [Freepik - Flaticon](https://www.flaticon.com/free-icons/email). Pogledati i [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

<h2 align="center">Screenshots</h2>

<p align="center">
  <img src="https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_login.jpg" width="32%" alt="Login">
  <img src="https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_main.jpg" width="32%" alt="Dashboard">
  <img src="https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_entry.jpg" width="32%" alt="Entry">
</p>

<p align="center">
  <img src="https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_overview.jpg" width="32%" alt="Overview">
  <img src="https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_end.jpg" width="32%" alt="End">
  <img src="https://raw.githubusercontent.com/pdejan/dejan.ba/refs/heads/main/img/postonosa_settings_1.jpg" width="32%" alt="Settings">
</p>

# ENGLISH

* **Poštonoša** is an app created for postmen in Bosnia and Herzegovina (Pošte Srpske). The application allows for simple logging and entry of utility bill payments within a delivery area, generating report, offering a few convenient perks, all while completely offline.

## Main Features
* **Offline:** The app works without an internet connection.
* **Calculator:** Automatically calculates the commission based on the entered amount.
* **Overview:** A review of all entered payments, as well as the ability to delete entries in case of an error.
* **Current Customer:** The app displays the current total collection amount for the customer you are currently serving.
* **Report Export:** At the end of the day, generate a PDF and/or TXT report (Payment Overview) with a single click. The files are automatically saved into organized, dated folders on the device and are ready for printing.
* **Settings:** The app offers a few configuration options, including:
1. Custom modification and entry of available services.
2. Send generated overview by email.
3. Choosing the format for the generated Payment Overview.
4. Automatic commission updates (Requires an internet connection).
* **Security & Privacy:** The app works completely offline. It does not collect any data, not even analytics about you, your phone, or the payments you enter. It does not communicate with any external servers. All data storage takes place locally on the device and never leaves it. The app only **requires** internet access when you need/want to update the calculated commission rates and send generated overview by email.

## App and Code Information
* **Automatic Commission Update:** To calculate commissions and rates, the app references the **naknada.json** file located in the assets folder. To enable seamless commission updates via a single button click in the settings without requiring a full app update this JSON file must be hosted online. This allows the app to fetch and save the latest commission data as needed. The variables in PodesavanjaEkran.kt must contain the exact path to this online file and its signature. File **naknada.json** is signed with RSA + SHA-256. Public key is part of app itself in **NaknadaPotpis.kt**, and tools for signing are in **tools** folder. App will reject JSON file that's not signed.
* **Colors:** The app's theme is built around two accent colors: GlavnaBoja (Main Color, yellow in this case) and SporednaBoja (Secondary Color, black). If you want to change the app's visual identity, you simply need to modify these two variables in ui.theme > Color.kt.
* **Third-party assets:** The application source code is licensed under the MIT License unless otherwise noted. The app icon and related launcher assets are based on a licensed **Flaticon/Freepik** resource and are not relicensed under the MIT License. I hold a valid license for their use in the app I publish, but any other use requires your own right to use the asset or compliance with the original Flaticon/Freepik license. Attribution: Email icons created by [Freepik - Flaticon](https://www.flaticon.com/free-icons/email). See also [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
