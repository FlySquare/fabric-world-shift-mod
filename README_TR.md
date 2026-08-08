# World Shift

**Diller:** [English](README.md) · [Türkçe](README_TR.md)

Sunucudaki tüm oyuncuları ortak bir geri sayımla farklı dünyalar arasında periyodik olarak ışınlayan bir Fabric modu.

---

## İndirme

| | Link |
| --- | --- |
| **Son sürüm** | [GitHub Releases](https://github.com/FlySquare/fabric-world-shift-mod/releases/latest) |
| **v1.0.0 jar** | [change-world-every-second-1.0.0.jar](https://github.com/FlySquare/fabric-world-shift-mod/releases/download/v1.0.0/change-world-every-second-1.0.0.jar) |
| **Kaynak kod** | [FlySquare/fabric-world-shift-mod](https://github.com/FlySquare/fabric-world-shift-mod) |

1. Son sürümden `.jar` dosyasını indirin
2. Minecraft **26.2** için [Fabric Loader](https://fabricmc.net/use/installer/) kurun
3. [Fabric API](https://modrinth.com/mod/fabric-api) kurun
4. World Shift jar dosyasını `mods` klasörüne koyun
5. Oyunu başlatın

Çok oyunculuda modu **sunucuya** kurun (ana menü ayarları / HUD kayması için istemciye de kurmanız önerilir).

---

## Genel bakış

World Shift, Minecraft oturumunu dönen bir çok-dünya mücadelesine çevirir:

- Tüm oyuncular için ortak bir süre sayacı akar
- Süre bitince herkes birlikte sonraki dünyaya ışınlanır
- Envanter korunur
- Her oyuncunun konumu dünya bazında hatırlanır; geri dönüşlerde kaldığınız yere dönersiniz

Tek oyunculu ve çok oyunculu (LAN / dedicated) çalışır; sunucu tarafında modun yüklü olması gerekir.

---

## Özellikler

- Hotbar üstünde ortak geri sayım HUD’u
- Anında dünya geçişi (title kartı yok)
- Özel boyutlar:
  - **Skyblock**
  - **Deep Dark** (otomatik agresif Warden’lar)
  - **Mantar Adası**
- Vanilla boyutlar: Ana Dünya, Nether, End
- İngilizce / Türkçe arayüz dili (varsayılan: İngilizce)
- Ana menüde Singleplayer yanındaki ayar butonu
- Dünyalar arası saniye ayarı
- Başlat / durdur / zorla geçiş / süre / debug komutları

---

## Gereksinimler

| Bağımlılık | Sürüm |
| --- | --- |
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3+ |
| Fabric API | 26.2 ile uyumlu |
| Java | 25+ |

---

## Hızlı başlangıç

1. Minecraft’ı açıp ana menüye gelin
2. **Singleplayer** yanındaki kare **WS** butonuna tıklayın
3. Dili seçin (**İngilizce** / **Türkçe**)
4. **Dünyalar arası saniye** değerini ayarlayın (varsayılan: `60`)
5. Dünya oluşturun veya açın
6. Şunu çalıştırın:

```text
/worldshift start
```

Geri sayım başlar. Sıfırlanınca herkes sonraki dünyaya geçer.

---

## Komutlar

| Komut | Açıklama |
| --- | --- |
| `/worldshift start` | World Shift oturumunu başlatır |
| `/worldshift stop` | Oturumu durdurur |
| `/worldshift next` | Hemen dünya değiştirir |
| `/worldshift timer <saniye>` | Geri sayım süresini ayarlar (1–3600) |
| `/worldshift world <isim>` | Herkesi belirli bir dünyaya ışınlar |
| `/worldshift debug` | Oturum hata ayıklama bilgisini yazar |

Dünya isimleri: `overworld`, `nether`, `end`, `skyblock`, `deep_dark`, `mushroom_island`

---

## Ayarlar

### Ana menü (WS butonu)

Dünya açmadan önce şunları ayarlayabilirsiniz:

- **Dil** — `İngilizce` (varsayılan) veya `Türkçe`
- **Dünyalar arası saniye** — her dünyanın süresi

Ayarlar şuraya kaydedilir:

```text
config/change-world-every-second.json
```

### Config alanları

| Alan | Varsayılan | Anlamı |
| --- | --- | --- |
| `language` | `en` | Arayüz / komut dili (`en` veya `tr`) |
| `countdownSeconds` | `60` | Dünyalar arası saniye |
| `transitionDelaySeconds` | `0` | Uyumluluk için tutulur (geçişler anlıktır) |
| `startingWorld` | `OVERWORLD` | Oturumun başladığı dünya |
| `enabledWorlds` | hepsi | Rotasyona dahil dünyalar |
| `debug` | `false` | Ek debug log bayrağı |

---

## Geçiş nasıl çalışır?

1. Oturum ayarlanan başlangıç dünyasında başlar
2. Geri sayım tüm çevrimiçi oyuncular için akar
3. Sıfırda sıradaki açık dünya seçilir
4. Mevcut dünyadaki konumlar (güvenliyse) kaydedilir
5. Herkes sonraki dünyaya ışınlanır
6. Kayıtlı güvenli konum varsa oraya, yoksa spawn’a gider
7. Geri sayım yeniden başlar

---

## Kaynaktan derleme

```bash
./gradlew build
```

Çıktı jar dosyası `build/libs/` altına yazılır.

Yayımlanan build’ler [GitHub Releases](https://github.com/FlySquare/fabric-world-shift-mod/releases) sayfasında yer alır.

---

## Lisans

Bu proje [MIT License](LICENSE) altındadır.

---

## Katkı

- Yazar: **Flyzen / FlySquare**
- Repo: [github.com/FlySquare/fabric-world-shift-mod](https://github.com/FlySquare/fabric-world-shift-mod)
- Minecraft 26.2 için Fabric ile geliştirilmiştir
