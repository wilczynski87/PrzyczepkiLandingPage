#FROM openjdk:21-ea-jdk-slim AS builder
#
#WORKDIR /workspace
#
## 1. Skopiuj pliki konfiguracyjne
#COPY gradle/ gradle/
#COPY gradlew .
#COPY gradle.properties .
#COPY settings.gradle.kts .
#COPY build.gradle.kts .
#
## 2. Skopiuj moduły
#COPY shared/ shared/
#COPY composeApp/ composeApp/
#
## 3. Nadaj uprawnienia
#RUN chmod +x gradlew
#
## 4. Zbuduj TYLKO webMain target (WASM)
#RUN ./gradlew :composeApp:jsBrowserDistribution \
#    --no-daemon \
#    --stacktrace \
#    --info
#
## Stage 2: Runtime
#FROM nginx:stable-alpine AS production
#
## Skopiuj zbudowane WASM z composeApp
#COPY --from=builder /workspace/composeApp/build/dist/js/productionExecutable/ /usr/share/nginx/html/
#
## Konfiguracja Nginx
##COPY docker/nginx-wasm.conf /etc/nginx/nginx.conf
#COPY nginx/nginx.conf etc/nginx/nginx.conf
#
## Napraw MIME types dla WASM
#RUN echo $'types {\n    application/wasm    wasm;\n}' > /etc/nginx/conf.d/wasm-mime.conf
#
## Dodaj LABEL dla nazwy/opisu obrazu
#LABEL name="przyczepki-web"
#LABEL version="1.0"
#LABEL description="Landing page dla przyczepek - web"
#
#EXPOSE 80
#
#CMD ["nginx", "-g", "daemon off;"]






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

# 4. Skopiuj moduły projektu **raz** i ustaw właściciela
COPY --chown=gradle:gradle shared/ shared/
COPY --chown=gradle:gradle composeApp/ composeApp/

# 5. Build KMP WASM (production)
RUN ./gradlew clean :composeApp:wasmJsBrowserDistribution \
    --no-daemon \
    --stacktrace \
    --build-cache \
    --refresh-dependencies

# ==============================
# RUNTIME STAGE
# ==============================
FROM nginx:stable-alpine

# 1. Konfiguracja nginx (WASM + SPA)
COPY nginx/nginx.conf /etc/nginx/nginx.conf

# 2. Skopiuj pliki WASM
COPY --from=builder \
  /workspace/composeApp/build/dist/wasmJs/productionExecutable/ \
  /usr/share/nginx/html/

# 3. Metadane obrazu
LABEL name="przyczepki-web"
LABEL version="1.0"
LABEL description="Landing page dla przyczepek - web"

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
