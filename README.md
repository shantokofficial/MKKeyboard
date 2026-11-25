# MKKeyboard

Android input method with the supplied Bangla key order and an English QWERTY layout.

## Language switching
- Tap the globe key to switch between Bangla and English.
- Swipe horizontally across the wide bottom spacebar area to switch language.
- Tap the space key to enter a space.

## Build
Requires JDK 17 and Android SDK Platform 35.

```bash
gradle assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.

The first build is a debug build. The symbol layer, predictive text, and final visual styling are not implemented yet.
