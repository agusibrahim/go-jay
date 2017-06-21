Berdasarkan beberapa survey, Transportasi berbasis aplikasi menjadi angkutan yang paling digemari masyarakat. Kehadirannya menjadi alternatif bagi warga di kota-kota besar untuk menghindari kemacetan. Terlepas banyak pihak yang menentang  keberadaannya, transportasi online ini makin digemari dan semakin menjamur di beberapa kota.

Di antara kita pasti tidak asing lagi dengan "Go-Jeg", yap! pasukan ijo yang banyak lalu lalang di jalanan ibukota, menyelip di gang-gang sempit dan bahkan mengantarkan makanan, lho?  Ya! bukan hanya mengantarkan penumpang, mereka juga bisa mengantarkan makanan dan masih banyak lagi.

Mengandalkan peta digital dari Google, membuat aplikasi mereka semakin interaktif, memilih tempat jemput dan lokasi tujuan hanya dengan klik.

# Memperkenalkan GO-JAY
Dari beberapa transportasi online yang banyak berseliweran, Ojek atau angkutan yang menggunakan sepeda motor menjadi golongan yang terbanyak ketimbang mobil, heli dan jet.

Namun bagaimana dengan bajaj (bajay) ? Transportasi umum roda tiga ini acap kali di sepelekan, ntah karena bising atau goyang-goyang saya tidak tahu, yang jelas saya sendiri belum pernah merasakan sensasi menumpanginya. lah?
Kelebihan kedaraan roda tiga ini disamping bisa muat sekitar 2 sampai 3 penumpang, juga bisa menyelip ke gang sempit, menerobos kemacetan, masuk jalur Busway, dan berjalan mundur, eh..

Bajay online rasanya sudah ada, namun jika dilihat di Google Play Store, User Interface dari aplikasinya sangat tidak niat, okelah mungkin mungkin cuma di Screenshot. Namun semakin miris setelah saya melihat feedbacknya. lupakan itu..

Bagaimana dengan GO-JAY?

### Mudah digunakan
<img src="https://github.com/agusibrahim/go-jay/blob/master/img/navigasi.gif" width="300"> | <img src="https://github.com/agusibrahim/go-jay/blob/master/img/pencarian.gif" width="300">
------------ | -------------
Navigasi | Pencarian

GO-JAY mudah digunakan, tinggal pilih lokasi jemput dan lokasi tujuan, bisa pilih manual di peta, berdasarkan pencarian atau lokasi pengguna saat ini.

### Koordinasi Animasi
<img src="https://github.com/agusibrahim/go-jay/blob/master/img/animasisearchbar.gif" width="300"> | <img src="https://github.com/agusibrahim/go-jay/blob/master/img/animasitarif.gif" width="300">
------------ | -------------
Searchbar | Tariff View

Tampil profesional dengan koordinasi animasi yang keren. Diharapkan pengguna bisa menggunakan aplikasi dengan nyaman. 

### Driver Terdekat
<img src="https://github.com/agusibrahim/go-jay/blob/master/img/driverterdekat.gif" width="300">

> Menampilkan driver terdekat dari lokasi jemput. 

### Edit Lokasi
<img src="https://github.com/agusibrahim/go-jay/blob/master/img/editlokasi.gif" width="300">

Edit Lokasi jemput maupun lokasi tujuan

### Kamera Pintar
<img src="https://github.com/agusibrahim/go-jay/blob/master/img/autocorrect.gif" width="300">

Bukan hanya animasi kamera yang keren, kamera disini juga pintar. Akan di zoom out otomatis jika point A ataupun point B tertutup widget atau hilang dari pandangan, sehingga tampilan rute tampil jelas.

### Runtime Permission Support
<img src="https://github.com/agusibrahim/go-jay/blob/master/img/Screenshot_20170621-203820.png" width="300"> | <img src="https://github.com/agusibrahim/go-jay/blob/master/img/Screenshot_20170621-203837.png" width="300">
------------ | -------------
Location Perms | Google Location Switch

GO-JAY Mendukung Runtime Permission di Android 23 (Mashmallow) keatas, juga adanya GPS switch instan, sehingga tidak perlu ke Pengaturan lokasi untuk mengaktifkan GPS.

## What Next?
Tentu masih banyak lagi yang harus ditambah dan bahkan dibenahi dari GO-JAY. Yang paling penting adalah membuat Aplikasi untuk driver, semacam pelacak lokasi (GPS Tracker) yang akan mengirim lokasi secara realtime ke server. Backend bisa menggunakan serverless macam [Firebase](https://firebase.google.com/)
 atau layanan dari [AWS](https://aws.amazon.com/)
. Lokasi setiap driver di query berdasarkan jarak terdekat dari lokasi pengguna, mungkin bisa menggunakan [GeoFire](https://github.com/firebase/geofire-java) jika menggunakan Firebase

Punya ide hebat atau kerja sama? [Contact Me](http://telegram.me/agusibrahim)
* [Agus Ibrahim](http://fb.me/mynameisagoes)

<img src="https://i1.sndcdn.com/artworks-000220944135-hb3nko-t500x500.jpg" width="300">

## Credits
* https://github.com/Ereza/CustomActivityOnCrash
* https://github.com/yshrsmz/KeyboardVisibilityEvent
* https://developers.google.com/maps/documentation/android-api/?hl=id
* http://wptrafficanalyzer.in/blog/drawing-driving-route-directions-between-two-locations-using-google-directions-in-google-map-android-api-v2/
* https://github.com/googlesamples/android-play-places

## License
GO-JAY prototype makes use of the [GNU GPL v3.0](http://choosealicense.com/licenses/gpl-3.0/) license. Remember to make public your project source code when reusing GO-JAY code.
