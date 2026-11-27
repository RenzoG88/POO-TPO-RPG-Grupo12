FROM debian:trixie-slim AS build

RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-21-jdk-headless \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /build
COPY juego/src/ ./src/
RUN mkdir classes \
    && find src -name '*.java' -print > sources.txt \
    && javac -encoding UTF-8 -d classes @sources.txt \
    && cp -r src/recursos classes/recursos

FROM debian:trixie-slim

# Swing necesita el JRE con soporte grafico, no solo el paquete headless.
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        curl \
        fonts-dejavu-core \
        novnc \
        openbox \
        openjdk-21-jre \
        websockify \
        x11-utils \
        x11vnc \
        xvfb \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --create-home --uid 1000 jugador

ENV DISPLAY=:99 \
    LANG=C.UTF-8 \
    JAVA_MAIN_CLASS=InicioSwing

WORKDIR /app
COPY --from=build /build/classes/ ./classes/
COPY docker/entrypoint.sh /usr/local/bin/iniciar-juego
COPY docker/index.html /usr/share/novnc/index.html
RUN chmod +x /usr/local/bin/iniciar-juego \
    && mkdir -p /tmp/.X11-unix \
    && chmod 1777 /tmp/.X11-unix

USER jugador
EXPOSE 6080
ENTRYPOINT ["/usr/local/bin/iniciar-juego"]
