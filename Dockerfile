FROM openjdk:21-ea-jdk-slim AS builder

WORKDIR /workspace

# 1. Skopiuj pliki konfiguracyjne
COPY gradle/ gradle/
COPY gradlew .
COPY gradle.properties .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# 2. Skopiuj moduły
COPY shared/ shared/
COPY composeApp/ composeApp/

# 3. Nadaj uprawnienia
RUN chmod +x gradlew

# 4. Zbuduj TYLKO webMain target (WASM)
RUN ./gradlew :composeApp:jsBrowserDistribution \
    --no-daemon \
    --stacktrace \
    --info

# Stage 2: Runtime
FROM nginx:stable-alpine AS production

# Skopiuj zbudowane WASM z composeApp
COPY --from=builder /workspace/composeApp/build/dist/js/productionExecutable/ /usr/share/nginx/html/

# Konfiguracja Nginx
COPY docker/nginx-wasm.conf /etc/nginx/nginx.conf

# Napraw MIME types dla WASM
RUN echo $'types {\n    application/wasm    wasm;\n}' > /etc/nginx/conf.d/wasm-mime.conf

# Dodaj LABEL dla nazwy/opisu obrazu
LABEL name="przyczepki-web"
LABEL version="1.0"
LABEL description="Landing page dla przyczepek - web"

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]