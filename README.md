# DINUX TOOLBOX PRO

Premium black-and-gold Android utility app starter project.

## Features included
- Animated dashboard with premium gold styling
- Notes saved locally on device
- Basic calculator
- Text tools (word/character count, uppercase/lowercase, copy)
- QR code generator (uses ZXing)
- Flashlight toggle (requests camera permission only when used)
- Color palette with HEX/RGB values and copy
- Notes backup/share and restore from pasted backup text
- PDF/file opening through Android's available apps
- Dark/light theme toggle
- GitHub Actions workflow to build a debug APK

## Build an APK with GitHub Actions
1. Create a new GitHub repository.
2. Upload the contents of this project (not the ZIP file itself) to the repository root.
3. Commit and push to `main` or `master`.
4. Open the repository's **Actions** tab.
5. Select **Build DINUX Toolbox Pro APK** and choose **Run workflow** if it has not started automatically.
6. Open the completed workflow run and download the artifact named `DIN​​UX-TOOLBOX-PRO-debug-apk` (artifact filename shown in Actions is `DIN​​UX-TOOLBOX-PRO-debug-apk`; use the displayed name). 
7. Extract the artifact ZIP to get `app-debug.apk`.

## Build locally
Requires Android SDK and JDK 17. With Gradle 8.9 installed:
```bash
gradle assembleDebug
```
APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Important
- This workflow builds a **debug APK** for testing, not a Play Store release.
- For Play Store publication, create and securely protect a release signing key, configure release signing, check current Google Play target API requirements, add a privacy policy, and test on real devices.
- Do not commit signing keys or passwords to GitHub.
- The flashlight needs camera permission because Android exposes the torch through camera APIs. The app does not take photos.
- QR generation requires Gradle to download ZXing from Maven Central.
