# Farsça Kartlar

Türkçe konuşanlar için Farsça öğrenme uygulaması (Android). Görünüm Rutin uygulamasıyla aynı: krem kâğıt,
siyah mürekkep, kırmızı vurgu, Playfair Display başlıklar; Ayarlar'dan Telefona uy / Açık / Koyu tema.

**APK:** [farsca-kartlar.apk](https://github.com/kinatawo00/DUKKAN2/releases/download/apk-latest/farsca-kartlar.apk)
Yeni sürüm eskisinin üzerine kurulur, ilerleme silinmez.

- **Kartlar:** Farsça yazı, Latin harfli okunuş, Türkçe anlam ve kayıtlı ses
- **Test:** anlamını, okunuşunu ya da Farsçasını bulma
- **Alfabe:** 32 harf, her harfin dört biçimi ve sesli örnek
- **Sözlük:** aranabilir kelime listesi
- **Rehber:** â, x, ğ gibi okunuş işaretleri ve yazı ipuçları
- **Sesli telaffuz:** her kelime ve cümle için kayıt (`audio/`), Piper fa_IR (gyro) sesiyle üretildi

## Yapı

- `app/src/main/assets/www/index.html`: uygulamanın tamamı (çevrimdışı çalışır)
- `app/src/main/assets/www/fonts/`, `audio/`: gömülü yazı tipleri ve sesler
- `app/src/main/java/com/farsca/kartlar/MainActivity.java`: WebView kabuğu, sistem çubuğu renkleri, geri tuşu
- `.github/workflows/apk.yml`: her push'ta APK'yı derleyip `apk-latest` sürümüne koyar
- `app/farsca.keystore`: sabit imza anahtarı (yeni sürümler eskisinin üzerine kurulabilsin diye)
