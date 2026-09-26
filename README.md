# Girá y Adiviná — App para Android TV

Este repo arma una app instalable de Android TV para "Girá y Adiviná Rioplatense", usando el modo **Sala TV** que ya existe en el juego: la tele muestra la pantalla compartida (sin necesitar tocarla) y cada jugador se suma desde su celular con un código, igual que Jackbox Party Pack.

La app es un **TWA** (Trusted Web Activity): no tiene juego adentro, solo abre la web ya publicada (`https://gvelazcamp.github.io/gira-y-adivina-rioplatense/`) directo en modo anfitrión de Sala TV (`?tvhost=1`).

## Por qué no está compilada todavía

El entorno donde armé este proyecto tiene bloqueado el acceso a los servidores de Android (`dl.google.com`) por política de red, así que no pude descargar el Android SDK ni compilar el archivo final (.aab). Dejé toda la configuración lista (`twa-manifest.json`) para que el build se termine en una computadora con internet normal.

## Cómo terminar el build (en tu compu, con internet normal)

Necesitás tener instalado [Node.js](https://nodejs.org/) (18 o más nuevo).

```bash
npm install -g @bubblewrap/cli
git clone https://github.com/gvelazcamp/gira-y-adivina-tv
cd gira-y-adivina-tv
bubblewrap init --manifest="https://gvelazcamp.github.io/gira-y-adivina-rioplatense/manifest.json"
```

Cuando pregunte, decile que instale su propio JDK y Android SDK (la opción recomendada, "Y"). Va a tardar unos minutos la primera vez (descarga el SDK).

Al terminar `init`, va a generar todo el proyecto Android. Ahí hay que:

1. **Reemplazar** el `twa-manifest.json` generado por el de este repo (ya tiene la URL de inicio en modo Sala TV y los colores del juego).
2. Volver a correr `bubblewrap update` para que tome esos cambios.
3. Seguir los pasos de "Agregar soporte para TV" más abajo.
4. Compilar con `bubblewrap build` — esto genera `app-release-signed.aab`, listo para subir a Play Console.

## Agregar soporte para TV (importante, no lo hace Bubblewrap solo)

Bubblewrap arma apps para celular. Para que Google Play la reconozca como app de TV y no la rechace, hay que editar a mano `app/src/main/AndroidManifest.xml` (generado por `bubblewrap init`) y agregar:

```xml
<!-- Dentro de <manifest>, junto a los demás <uses-feature> -->
<uses-feature android:name="android.software.leanback" android:required="false" />
<uses-feature android:name="android.hardware.touchscreen" android:required="false" />

<!-- Dentro de <application>, un segundo <intent-filter> en el mismo <activity> principal -->
<intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.LEANBACK_LAUNCHER" />
</intent-filter>

<!-- Dentro de <application>, el banner que pide Android TV (320x180px) -->
<meta-data android:name="android.tv" android:value="true" />
```

Y agregar un ícono banner de 320×180px en `app/src/main/res/drawable/tv_banner.png`, referenciado como `android:banner="@drawable/tv_banner"` en `<application>`.

## ¿Va a ser la misma app que ya está en Play Store, o una nueva?

Depende de qué `packageId` uses:

- Si usás uno **nuevo** (lo que dejé configurado: `io.github.gvelazcamp.giratv`), Play Store la va a tratar como una **app aparte**, con su propia ficha. Es el camino más simple porque no necesita el keystore de firma original.
- Si querés que sea **la misma app** que ya está publicada (`io.github.gvelazcamp.twa`), hay que usar ese mismo `packageId` en el `twa-manifest.json` y firmarla con **el mismo keystore** que se usó para la app actual — sin ese archivo de firma, Google Play va a rechazar la actualización. Ese keystore no está en ningún repo (por seguridad, nunca debe estarlo); si no sabés dónde quedó guardado, lo más simple es publicarla como app nueva.

## Publicar

Una vez que tengas el `.aab` firmado:

1. Andá a [Play Console](https://play.google.com/console).
2. Si es una app nueva: creá una ficha nueva y subí el `.aab` en una prueba interna primero.
3. En la sección de "Dispositivos compatibles" / "Catálogo de dispositivos", confirmá que aparece Android TV como compatible (si el `AndroidManifest.xml` quedó bien configurado, Google lo detecta solo).
4. Probala en un emulador de Android TV o una tele real antes de mandarla a producción.
