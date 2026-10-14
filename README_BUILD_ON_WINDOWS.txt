ARCADIUM OG9 ANDROID TEST PROJECT

This is SOURCE CODE, NOT an APK. The nine game files and nine cover images are bundled in app/src/main/assets.

BUILD ON WINDOWS
1. Install Android Studio (official Android developer site).
2. Open Android Studio > Open > select this Arcadium_OG9_Android_Project folder.
3. Allow Android Studio to install Android SDK 35 and sync Gradle (internet required).
4. Build > Build Bundle(s) / APK(s) > Build APK(s).
5. Find app-debug.apk in app/build/outputs/apk/debug/.
6. Copy the APK to RG Rotate Downloads using USB MTP.
7. On Mini Vibe Files, tap APK and allow installing unknown apps for Files when asked.
8. Open Arcadium OG9 icon and test the 9 cards and games.

TEST STATUS
- Source files and structure packaged; APK NOT compiled or tested on RG Rotate.
- The scoreboards in the existing HTML are beta placeholders, not verified online rankings.
- Hardware D-pad/game controls need testing; BUTTON_A is mapped to click currently.
- Offline content is served through an internal appassets HTTPS URL, not external network.
- App uses landscape orientation, suitable for the square device if preferred.
