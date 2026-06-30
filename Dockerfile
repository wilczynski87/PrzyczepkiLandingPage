# ==============================
# BUILD STAGE
# ==============================
FROM gradle:8.14.3-jdk21-jammy AS builder

# 0.1. Root do instalacji bibliotek i przygotowania katalogu
USER root
WORKDIR /workspace

# 0.2. Potrzebne biblioteki systemowe dla Node/WASM
RUN apt-get update && apt-get install -y \
    libatomic1 \
    ca-certificates \
    curl \
    xz-utils \
    gnupg \
 && rm -rf /var/lib/apt/lists/*

# 1. Skopiuj pliki Gradle i nadaj uprawnienia
COPY gradlew gradlew
COPY gradle/ gradle/
COPY gradle.properties .
COPY settings.gradle.kts .
COPY build.gradle.kts .

RUN chmod +x gradlew

# 2. Utwórz katalog .gradle i nadaj prawa gradle userowi
RUN mkdir -p /workspace/.gradle && chown -R gradle:gradle /workspace

# 3. Przełącz na gradle usera
USER gradle

# Przyśpieszenie  - catch zależności
RUN ./gradlew --no-daemon help

# 4. Skopiuj moduły projektu **raz** i ustaw właściciela
COPY --chown=gradle:gradle shared/ shared/
COPY --chown=gradle:gradle composeApp/ composeApp/

# 4.1. Ogranicz pamięć JVM na czas budowy w kontenerze.
#      Runner CI (ubuntu-latest) ma ~7 GB RAM. Domyślne -Xmx6G z gradle.properties
#      + osobny daemon Kotlina (-Xmx6G) => OOM podczas kompilacji/optymalizacji WASM.
#      Nadpisujemy ustawienia tylko w obrazie (klucze z końca pliku wygrywają)
#      i kompilujemy Kotlin w procesie Gradle (bez drugiej maszyny JVM).
RUN printf '\n# --- Docker build overrides (mniej RAM) ---\norg.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8\norg.gradle.workers.max=2\nkotlin.compiler.execution.strategy=in-process\n' >> gradle.properties

# 5. Build KMP WASM (production)
RUN ./gradlew \
    :shared:compileKotlinWasmJs \
    :composeApp:wasmJsBrowserDistribution \
    --no-daemon \
    --stacktrace


# ==============================
# RUNTIME STAGE
# ==============================
FROM nginx:stable-alpine

# 1. Konfiguracja nginx (WASM + SPA)
COPY nginx/nginx.conf /etc/nginx/nginx.conf

# 2. Skopiuj pliki WASM
#COPY --from=builder \
#  /workspace/composeApp/build/dist/wasmJs/productionExecutable/ \
#  /usr/share/nginx/html/
COPY --from=builder \
  /workspace/composeApp/build/dist/wasmJs/productionExecutable/ \
  /usr/share/nginx/html/


# 3. Metadane obrazu
LABEL name="przyczepki-web"
LABEL version="1.0"
LABEL description="Landing page dla przyczepek - web"

ENV APP_ENV=dev

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
