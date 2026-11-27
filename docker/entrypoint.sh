#!/bin/bash
set -Eeuo pipefail

pids=()

cleanup() {
    trap - EXIT
    if ((${#pids[@]})); then
        kill "${pids[@]}" 2>/dev/null || true
        wait "${pids[@]}" 2>/dev/null || true
    fi
}

trap cleanup EXIT
trap 'exit 143' TERM
trap 'exit 130' INT

# El servidor grafico vive dentro del contenedor y no usa el display del host.
Xvfb "$DISPLAY" -screen 0 1280x900x24 -ac -nolisten tcp &
xvfb_pid=$!
pids+=("$xvfb_pid")

display_ready=false
for ((attempt = 0; attempt < 100; attempt++)); do
    if xdpyinfo -display "$DISPLAY" >/dev/null 2>&1; then
        display_ready=true
        break
    fi
    if ! kill -0 "$xvfb_pid" 2>/dev/null; then
        echo "No se pudo iniciar el escritorio virtual." >&2
        exit 1
    fi
    sleep 0.1
done

if [[ "$display_ready" != true ]]; then
    echo "El escritorio virtual no estuvo listo a tiempo." >&2
    exit 1
fi

openbox --sm-disable &
pids+=("$!")

# Solo el puente web puede acceder al servidor VNC interno.
x11vnc -display "$DISPLAY" -rfbport 5900 -localhost \
    -forever -shared -nopw -xkb -noxdamage -quiet &
pids+=("$!")

websockify --web=/usr/share/novnc 6080 127.0.0.1:5900 &
pids+=("$!")

java -Djava.awt.headless=false -cp /app/classes "${JAVA_MAIN_CLASS:-InicioSwing}" &
game_pid=$!
pids+=("$game_pid")

# Cerrar el juego detiene el contenedor; un fallo del escritorio tambien lo detiene.
status=0
wait -n -p finished_pid "${pids[@]}" || status=$?
if [[ "$finished_pid" != "$game_pid" ]]; then
    echo "Se detuvo un componente del escritorio virtual." >&2
    exit 1
fi
exit "$status"
