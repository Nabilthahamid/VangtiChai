# VangtiChai

An Android change calculator built for **CSE 489** using **Kotlin, XML Views, ConstraintLayout, and View Binding**.

Enter an amount in Taka with the custom keypad to see its breakdown into **500, 100, 50, 20, 10, 5, 2, and 1 Taka** denominations.

## Features

- Custom XML buttons for digits 0–9 and Clear; no system keyboard for amount entry.
- Immediate updates to the amount and all eight note counts, including zeros.
- Portrait layout with the note-count table to the left of the keypad.
- Compact landscape layout with all controls visible at the tested 731×411dp screen size.
- Amount restoration after rotation.
- System-bar insets, shared styles, string resources, and dimension resources.

The amount starts at zero. Leading zeros do not accumulate. Input is limited to **999,999,999**; further digits are ignored to prevent overflow and keep the display manageable.

## Examples

| Denomination | Amount 234 | Amount 786 | Amount 500 |
|---|---:|---:|---:|
| 500 | 0 | 1 | 1 |
| 100 | 2 | 2 | 0 |
| 50 | 0 | 1 | 0 |
| 20 | 1 | 1 | 0 |
| 10 | 1 | 1 | 0 |
| 5 | 0 | 1 | 0 |
| 2 | 2 | 0 | 0 |
| 1 | 0 | 1 | 0 |

Clear resets both the amount and every count to zero.

## Technology and configuration

| Setting | Value |
|---|---|
| Package / namespace / application ID | `com.example.vangtichai` |
| Language | Kotlin |
| Interface | XML Views, ConstraintLayout, Material components |
| View access | View Binding |
| Minimum SDK | API 21 (Android 5.0 Lollipop) |
| Compile / target SDK | API 37 |
| Android Gradle Plugin | 9.4.1 |
| Gradle wrapper | 9.6.0 |
| Gradle daemon JVM toolchain | Java 25 |
| AndroidX Core KTX | 1.16.0 |
| AndroidX Activity KTX | 1.10.1 |
| AndroidX AppCompat | 1.7.1 |
| Material Components | 1.13.0 |

The Java 25 daemon setting comes from `gradle/gradle-daemon-jvm.properties`; the app's Java source/target compatibility is configured as Java 11. The minimum SDK is API 21 to match the assignment's suggested Android 5.0 baseline. Core, Activity, AppCompat and Material use API-21-compatible versions because their previously selected newer releases require API 23. ConstraintLayout, Gradle, AGP and the compile/target SDK were retained.

## Open and run

1. Clone or download this repository.
2. In Android Studio, choose **File → Open** and select the repository root.
3. Ensure Android SDK Platform 37 is installed and the SDK location is configured for your machine.
4. Allow Gradle sync and any required toolchain/dependency downloads to finish.
5. Select an emulator or connected Android device running API 21 or newer, then click **Run**.

SDK paths belong in the local, ignored `local.properties` file. The original development environment used `D:\Android\Sdk` and `D:\Android\Gradle`; other machines can use their own locations.

## Build and test

Test sources and local verification scripts are excluded from this repository. The recorded unit-test results below come from the development checkout, where those files remain available. A fresh clone can build the app and run lint, but cannot reproduce the 13 unit tests without those sources.


From the project root in Windows PowerShell:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

The test task runs the local tests when their sources are present. To build and inspect a fresh clone:

```powershell
.\gradlew.bat lintDebug assembleDebug
```

To execute the development checks again instead of reusing up-to-date results:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug --rerun-tasks
```

Generated outputs:

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Unit-test report: `app/build/reports/tests/testDebugUnitTest/index.html`
- Lint report: `app/build/reports/lint-results-debug.html`

Build outputs and local test reports are ignored by Git and are not included in the repository.

## How the code works

```text
Digit button → AmountEntry → updated amount
                              ↓
                      ChangeCalculator
                              ↓
              MainActivity updates the screen
```

| File | Responsibility |
|---|---|
| [MainActivity.kt](app/src/main/java/com/example/vangtichai/MainActivity.kt) | Handles clicks, renders results, applies insets, and restores state. |
| [AmountEntry.kt](app/src/main/java/com/example/vangtichai/AmountEntry.kt) | Appends digits and checks the input limit before multiplication. |
| [ChangeCalculator.kt](app/src/main/java/com/example/vangtichai/ChangeCalculator.kt) | Calculates an ordered list of denomination/count pairs independently of Android views. |
| [Portrait layout](app/src/main/res/layout/activity_main.xml) | Amount header, note table, and conventional four-row keypad. |
| [Landscape layout](app/src/main/res/layout-land/activity_main.xml) | Compact table beside a three-row keypad: 1–4, 5–8, then 9 / 0 / Clear. |
| [Default dimensions](app/src/main/res/values/sizes.xml) | Sizes, spacing, padding, text sizes, and match-constraint zero. |
| [Landscape dimensions](app/src/main/res/values-land/sizes.xml) | Compact overrides for the shorter screen. |
| [Strings](app/src/main/res/values/strings.xml) and [styles](app/src/main/res/values/styles.xml) | Visible text, accessibility labels, and shared view appearance. |

### Calculation

For each denomination, from largest to smallest:

```kotlin
val count = remaining / denomination
remaining %= denomination
```

For 234, two 100s leave 34; one 20 leaves 14; one 10 leaves 4; two 2s leave zero. Every denomination still produces a row, even when its count is zero.

### Layout selection and state

Android automatically selects `layout-land/activity_main.xml` in landscape and the default layout otherwise. Both contain matching view IDs. Landscape dimensions override defaults by resource name. Tablets reuse these responsive layouts.

The amount is saved in `onSaveInstanceState()`, restored in `onCreate()`, and used to recalculate counts. Orientation is not locked, and Activity recreation is not bypassed with `configChanges`.

### Appearance

The app follows the device's light or dark appearance through its Material DayNight theme. There is no in-app theme switch or saved appearance override.

## Verification record

### API 21 compatibility update (2026-10-18)

- `minSdk` changed to 21; compile/target SDK remains 37.
- The debug APK was inspected with AAPT: `sdkVersion: 21`, `targetSdkVersion: 37`.
- All 44 merged dependency manifests were inspected; none requires a minimum above API 21. No manifest override was used to bypass compatibility.
- `testDebugUnitTest lintDebug assembleDebug` passed, including all 13 local unit tests. The local instrumentation-test APK also built with `assembleDebugAndroidTest`.
- The API-23-only `windowLightStatusBar` setting lives in `values-v23/themes.xml`; API 21/22 use the base theme.
- The updated build passed the full core runtime sequence on Pixel Tablet, Android 15/API 35, in native portrait and landscape.
- Pixel 10 Pro XL (Android 17/API 37) installed the same APK, verified by SHA-256, but Android's package-update overlay blocked launch. Runtime verification on that profile was not completed for this dependency update. Only blocked ADB test clients were stopped; the emulator remained running.
- **Android 5.0/API 21 runtime: NOT TESTED.** No API 21 system image or emulator is installed. Build metadata and lint checks do not establish runtime correctness on Lollipop.
- To verify Lollipop, create an API 21 AVD in Device Manager, install the generated debug APK and run the manual sequence below. Record the actual device/profile and results afterward.

The following earlier records describe checks performed before the API 21 dependency update.

Full revalidation after removing the in-app theme toggle (2026-10-17):

- Command: .\gradlew.bat testDebugUnitTest lintDebug assembleDebug --rerun-tasks
  Result: BUILD SUCCESSFUL; all 52 actionable tasks executed.
- All 13 unit tests passed. Lint: zero errors and the existing single
  empty-values-v23 warning.
- Command: .\gradlew.bat connectedDebugAndroidTest
  Result: BUILD SUCCESSFUL. The existing app-context instrumentation
  test passed once on each connected emulator (two executions, no
  failures/errors). This test does not verify screen appearance.
- Pixel_10_Pro_XL_2: Android 17/API 37, native 1344x2992px at 480dpi.
- Pixel_Tablet: Android 15/API 35, native 2560x1600px at 320dpi.
- Both profiles passed the full runtime sequence in native portrait and
  landscape and at each of four separate 160dpi display overrides:
  411x731dp, 731x411dp, 800x1280dp and 1280x800dp.
- All 12 configurations passed initial zero, 2 -> 23 -> 234, 786, 500,
  Clear, leading zeros, maximum/excess input, rotation in both
  directions, and continued input after rotation.
- The removed theme icon was absent throughout. All required views were
  visible without scrolling. All 12 maximum-input screenshots were
  visually inspected: no overlap, clipped numbers or system-bar coverage.
- Both emulators' native size/density and free rotation were restored.
- Logs: app/build/reports/runtime-all-phone.txt and runtime-all-tablet.txt.
  Screenshots: app/build/reports/runtime-all-phone/ and runtime-all-tablet/.
  Device-test report: app/build/reports/androidTests/connected/debug/index.html.
- Actual Pixel XL and Nexus 10 profiles remain unavailable and NOT TESTED;
  the similarly sized overrides and other profiles are not substitutes.
- These checks required no further changes to application code. Local
  test sources/scripts and generated reports remain excluded from Git.

Documentation reviewed on **2026-10-18**. The following summarizes checks actually performed; a passing build is not treated as proof of visual correctness.

### Build and unit tests

- A forced Gradle rerun completed successfully.
- **13 unit tests passed:** five input tests, seven calculation tests, and one starter test.
- Coverage includes 234, 786, zero, exact denominations, maximum input, leading zeros, invalid inputs, and reconstruction of amounts 0–2000.
- The latest standard build/test/lint command also succeeded, reusing up-to-date outputs where applicable.
- With the API 21 dependencies, lint reports **zero errors and four dependency-update warnings**. The newer suggested releases require API 23, so the compatible versions are intentional. The earlier empty-folder warning is resolved: `values-v23/themes.xml` now contains the API-23-only light status-bar icon setting.

### Actual emulator profiles

| Actual profile | Android | Native screen configuration | Result |
|---|---|---|---|
| Pixel 10 Pro XL (`Pixel_10_Pro_XL`, `Pixel_10_Pro_XL_2`) | Android 17 / API 37 | 1344×2992px, 480dpi; about 448×997dp portrait and the rotated equivalent | Input, counts, Clear, rotation, limits, and visual checks passed in both orientations. |
| Pixel Tablet (`Pixel_Tablet`) | Android 15 / API 35 | 2560×1600px, 320dpi; 800×1280dp portrait and 1280×800dp landscape | Full core runtime sequence and visual checks passed in both orientations. |

### Display overrides — separate from hardware-profile tests

All four logical screen sizes below passed the full core runtime sequence using temporary 160dpi overrides on the available Pixel 10 Pro XL and Pixel Tablet emulators:

| Logical display size | Orientation | Result |
|---|---|---|
| 411×731dp | Portrait | PASS |
| 731×411dp | Landscape | PASS; all rows and controls visible without scrolling |
| 800×1280dp | Portrait | PASS |
| 1280×800dp | Landscape | PASS |

Screenshots were inspected at maximum input for clipping, overlap, missing controls, and system-bar coverage at default font size. These sizes describe the full display before insets.

### Remaining verification

- **Actual Pixel XL profile, portrait and landscape: NOT TESTED.**
- **Actual Nexus 10 profile, portrait and landscape: NOT TESTED.**
- Pixel 10 Pro XL is not Pixel XL; Pixel Tablet is not Nexus 10. Display overrides do not replace those profile checks.
- Large-font, display-zoom, and comprehensive accessibility checks remain unperformed.

Submission verification remains incomplete until the required actual profiles are tested and their results recorded.

## Manual runtime checks

Run the app on a connected device or emulator, then follow this sequence. The ADB automation scripts used during development are not included in this repository.


1. Confirm initial amount 0 and eight zero counts.
2. Tap 2, 3, 4 and check each intermediate amount.
3. Verify 234 and 786 against the example table.
4. Enter 786, rotate both ways, then append 2; expect 7862.
5. Verify 500 and Clear.
6. Enter 007; expect 7. Enter nine 9s, then another digit; expect 999999999.
7. Inspect every row/button for clipping, overlap, and system-bar coverage.

To create the remaining profiles, open **Tools → Device Manager → + → Create Virtual Device**. Select **Phone → Pixel XL** or **Tablet → Nexus 10**, choose a compatible system image, and finish setup. Run the sequence above in both orientations and record the actual profile, Android version, size, density, and result.

`README.txt` contains the same project information in plain text for the assignment.
