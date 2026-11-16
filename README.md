# Resume Studio

A native Android resume builder for Pynexia Android Development, Week 4. Built with Java and XML/Android platform APIs.

## What it does

- Guides the learner through three steps: profile, education and experience, then skills and review.
- Validates the name and email before continuing and requires resume content before preview.
- Saves each step locally as a private draft so work survives app restarts.
- Shows a live resume preview with the details entered in the form.
- Creates a paginated PDF and lets the learner choose where to save it with Android's document picker.

## Build and run

Open this folder in Android Studio with JDK 17 or 21 and Android SDK 35. Run the `resume` application on Android 8.0 (API 26) or later.

```powershell
.\gradlew.bat :resume:assembleDebug :resume:lintDebug
```

The APK is generated at `resume/build/outputs/apk/debug/resume-debug.apk`.

## Review walkthrough

1. Enter a name and a valid email address. Try an invalid email to see validation.
2. Continue and enter education and work history on separate lines.
3. Add skills, then preview the resume.
4. Return to edit the draft or select **Download PDF** and choose a destination.
5. Close and reopen the app to confirm the draft is restored. Use **Clear saved draft** on the final form step to remove it.

## Implementation notes

Drafts are stored only in app-private SharedPreferences. No account, cloud service or network access is used. PDF generation uses Android's `PdfDocument`; the Storage Access Framework asks the learner where to save the result and avoids broad storage permissions. A generated resume contains the personal information entered by the learner; review the chosen destination before sharing it.

The PDF uses a compact single-column layout and automatically starts a new page between sections when space is low. For long entries, review the generated file before sending it to an employer.

## Published submission

- Source repository: https://github.com/ENGEyad/pynexia-week4-resume
- Hosted showcase: https://engeyad.github.io/pynexia-week4-resume/
- APK, source ZIP and bilingual report: https://github.com/ENGEyad/pynexia-week4-resume/releases/latest

The app built successfully and Android Lint reported no issues. Automated and device tests were not run.
