# Resume App

Three-step resume form with profile, education, experience and skills; validates name/email, saves a local draft, previews the resume and exports a PDF.

# Resume Studio

Native Java Android resume builder for Pynexia Week 4. The app guides the learner through profile, education/work history, and skills, then shows a live preview and exports a PDF through the Android document picker.

## Build and checks

Open the standalone project root in Android Studio with JDK 17/21 and Android SDK 35. Build with `:resume:assembleDebug`, run `:resume:testDebugUnitTest`, and check `:resume:lintDebug`.

Three JVM unit tests for name/email/content validation passed. Build passed and Android Lint reported no issues. Device UI flows were not run because the emulator rejected installs with 241 MB free internal storage.

Draft data is app-private and local. The app uses no network service or account. PDF layout and long entries should be reviewed after export.


## Published links

- Repository: https://github.com/ENGEyad/pynexia-week4-resume
- Hosted overview: https://engeyad.github.io/pynexia-week4-resume/
- Project report: https://engeyad.github.io/pynexia-week4-resume/downloads/Resume-App-Report.pdf

Build passed; Android Lint reported no issues. Unit tests passed: 3. Emulator UI flows were not run due to low emulator storage. APK and source ZIP are provided in the local submission kit.
