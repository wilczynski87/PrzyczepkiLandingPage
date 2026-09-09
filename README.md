# PrzyczepkiLandingPage

Kotlin Multiplatform: Compose (WASM/JS) + Ktor API + MongoDB.
Rezerwacje przyczepek, płatności Przelewy24, brama Supla, e-mail.

## Moduły

* [`composeApp`](./composeApp/src) — entry point WASM/JS
* [`shared`](./shared/src) — UI Compose, modele, klienty HTTP (commonMain)
* [`server`](./server/src/main/kotlin) — API Ktor (port **8090**)

## Tryby: DEV vs PROD

| | Lokalnie **DEV** | Produkcja **PROD** |
|---|---|---|
| `APP_ENV` / `API_ENV` | `dev` | `prod` |
| Płatności | `PAYMENT_MOCK=true` / sandbox | live P24 (`PAYMENT_MOCK=false`) |
| Brama | `GATE_MOCK=true` (domyślnie) | Supla live |
| UI rezerwacji | zawsze dostępne | zawsze dostępne |

Skopiuj env: `cp .env.example .env` i uzupełnij wartości.

---

## DEV — Gradle (codzienna praca)

Frontend na **8080**, API na **8090**. Mongo (i opcjonalnie e-mail) w Dockerze.

```shell
# 1. Env
cp .env.example .env   # jeśli jeszcze nie masz .env

# 2. Baza (+ opcjonalnie e-mail)
docker compose -f docker-compose.dev.yaml up -d przyczepki_db
# docker compose -f docker-compose.dev.yaml up -d przyczepki_email

# 3. API (ładuje .env z katalogu głównego)
./gradlew :server:run

# 4. Web (WASM)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

Alternatywa JS (starsze przeglądarki): `./gradlew :composeApp:jsBrowserDevelopmentRun`

Po płatności P24 użytkownik wraca na `PAYMENT_URL_RETURN`.
W **DEV** domyślnie return/status idą przez tunel ngrok
(`PAYMENT_PUBLIC_BASE`, domyślnie `https://adelyn-unarrestable-amirah.ngrok-free.dev`),
bo sandbox P24 nie dosięga `localhost`. Uruchom np. `ngrok http 8090` (API).
W PROD URL-e to domena produkcyjna (`przyczepkifat.pl`).
(domyślnie w `.env.example`: `http://localhost:8080/podsumowanieRezerwacji`).

Lokalny webpack ma `historyApiFallback` (`composeApp/webpack.config.d/spa-routing.js`) —
ścieżki SPA działają jak w produkcji (nginx `try_files`).

---

## DEV — pełny Docker

Cały stack (web + API + Mongo + e-mail):

```shell
cp .env.example .env
docker compose -f docker-compose.dev.yaml up --build
```

- Web: http://localhost:${WEB_PORT:-80}
- API: http://localhost:8090
- Domyślnie mocki płatności i bramy (`PAYMENT_MOCK` / `GATE_MOCK`)

---

## PROD — Docker

Na VPS zwykle przez CI (`.github/workflows/deploy.yml`). Ręcznie:

```shell
# .env z APP_ENV=prod, API_ENV=prod i pełnymi sekretami
docker compose up -d --build
```

[`docker-compose.yaml`](./docker-compose.yaml) domyślnie ustawia `APP_ENV`/`API_ENV` na **prod**.
Nginx w obrazie web serwuje SPA i proxy `/api/` → API.

Mongo w PROD jest wystawione na `0.0.0.0:27017` (dostęp z zewnątrz).
Na VPS musi być otwarty firewall na TCP 27017. Nazwa bazy: `przyczepki` (nie `przyczepki_db`).

Przykład URI Compass (`!` w haśle → `%21`):

```
mongodb://admin:HASLO@IP_VPS:27017/przyczepki?authSource=admin
```

---

## Uwagi

- `:server:run` wstrzykuje zmienne z `.env` (patrz `server/build.gradle.kts`).
- W DEV API może działać bez credentials P24 (`PAYMENT_MOCK`) i bez bramy (`GATE_MOCK`).
- W PROD brak wymaganych sekretów kończy się błędem startu (`ApiConfig`).

Więcej o KMP: [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform).
