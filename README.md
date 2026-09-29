# Girá y Adiviná — Sala TV para Android TV

Aplicación de **prueba**, instalable manualmente. La TV muestra la partida del juego
existente y cada persona juega y escribe las respuestas desde su celular.
No exige otra app en el celular. Requiere internet.

## Descargar e instalar

1. Abrir [Releases](https://github.com/gvelazcamp/gira-y-adivina-tv/releases) y descargar
   `gira-y-adivina-tv-prueba.apk` de la prueba más reciente (no «Source code»).
2. Pasar el archivo a la tele por USB o por el método de transferencia de la TV.
3. Abrir el APK con el gestor de archivos y permitir temporalmente instalar desde
   esa fuente cuando Android lo solicite.
4. Abrir **Girá y Adiviná TV · Prueba** en la tele y elegir **Mostrar código de sala**.
5. En cada celular abrir https://gvelazcamp.github.io/gira-y-adivina-rioplatense/tv.html
   e ingresar nombre y código de seis caracteres.
6. Con 2 a 6 jugadores conectados, comenzar y sortear quién arranca.

La TV muestra tablero, ruleta y resultados. Las acciones y respuestas se envían desde
los celulares. Atrás en el mando permite seguir, crear otra sala o salir.
Cerrar/reiniciar la TV termina la partida; recargar la pestaña del mismo celular
recupera su lugar. No cerrar la pestaña ni borrar sus datos de sesión.

Si una actualización del APK falla por firma distinta, desinstalar la prueba anterior
e instalar la nueva. La firma de depuración se conserva en caché de Actions cuando
está disponible; no es una clave de distribución de producción.

## Implementación y compilación

Proyecto Android WebView, horizontal, con entrada Leanback, banner, navegación con
mando, pantalla encendida y manejo de errores. No depende de Chrome/Custom Tabs.
La navegación queda limitada al sitio del juego; no hay puente JavaScript nativo.

- Paquete de prueba: `io.github.gvelazcamp.giratv.prueba`.
- URL: `index.html?tvhost=1` del juego actual.
- Android mínimo: API 23; WebView actualizado requerido por el juego web.
- `twa-manifest.json` es histórico y **no se usa** para compilar.
- No se modifica la ficha Play Console ni el paquete de la aplicación de celular.

Actions → **APK de prueba Android TV** compila con `lintDebug assembleDebug` y publica
APK y checksum SHA-256 en una prerelease. Para compilar localmente: JDK 17, Gradle 8.13,
Android SDK 36/Build Tools 35.0.0 y `gradle lintDebug assembleDebug`.

## Estado

Compilación y lint Android verificados. Flujo web probado con dos jugadores y broker
real; partida completa y revancha verificadas con transporte simulado. Ver
[pruebas del juego](https://github.com/gvelazcamp/gira-y-adivina-rioplatense/blob/main/SALA_TV_PRUEBA.md).

**Pendiente:** prueba física el fin de semana: mando, imagen, sonido, suspensión de
celulares y red doméstica. Los brokers públicos existentes no garantizan disponibilidad
ni privacidad de salas. Cerrar el anfitrión pierde la partida; no hay guardado de sala.

No se ha publicado en Play Store ni preparado firma de producción. Integrarla en la
ficha existente exige revisar el proyecto/firma Android actual. Este paquete separado
es solo para probar. No se configuró un dominio propio: si cambia, actualizar `GAME_URL`
y el filtro de navegación de `MainActivity` y recompilar.
