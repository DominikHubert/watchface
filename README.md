# OmegaStyle Watchface (Wear OS)

Erste Projektversion im gewünschten Stil:
- All black Hintergrund
- graue digitale Uhrzeit
- Datum
- Infozeilen für Schritte, Puls, Schlaf, Akku
- AOD-freundliches, schlichtes Rendering
- Omega-Style Logo als drawable eingebunden

## Build in Android Studio
1. Projektordner `watchface-tagstyle` in Android Studio öffnen.
2. Gradle Sync laufen lassen.
3. Signierte/Debug-APK bauen.

## Hinweise
- Datenquellen für Schritte/Puls/Schlaf sind aktuell als Platzhalter vorgesehen (`--`) und können über Health Services / Complications erweitert werden.
- Akku wird bereits live angezeigt.
