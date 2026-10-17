VangtiChai

An Android change calculator built for CSE 489 using Kotlin, XML Views, ConstraintLayout, and View Binding.

Enter an amount in Taka with the custom keypad to see its breakdown into 500, 100, 50, 20, 10, 5, 2, and 1 Taka denominations.

Features

- Custom XML buttons for digits 0-9 and Clear; no system keyboard for amount entry.
- Immediate updates to the amount and all eight note counts, including zeros.
- Portrait layout with the note-count table to the left of the keypad.
- Compact landscape layout with all controls visible at the tested 731x411dp screen size.
- Amount restoration after rotation and theme changes.
- Sun/moon button for light and dark appearance, with the selected theme saved across reopening.
- System-bar insets, shared styles, string resources, and dimension resources.

The amount starts at zero. Leading zeros do not accumulate. Input is limited to 999,999,999; further digits are ignored to prevent overflow and keep the display manageable.

Examples

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

Technology and configuration

| Setting | Value |
|---|---|
| Package / namespace / application ID | com.example.vangtichai |
| Language | Kotlin |
| Interface | XML Views, ConstraintLayout, Material components |
| View access | View Binding |
| Minimum SDK | API 24 |
| Compile / target SDK | API 37 |
| Android Gradle Plugin | 9.4.1 |
| Gradle wrapper | 9.6.0 |
| Gradle daemon JVM toolchain | Java 25 |

The Java 25 daemon setting comes from gradle/gradle-daemon-jvm.properties; the app's Java source/target compatibility is configured as Java 11. API 21 is suggested by the assignment, but this project's existing API 24 minimum was preserved.

Open and run

1. Clone or download this repository.
2. In Android Studio, choose File -> Open and select the repository root.
3. Ensure Android SDK Platform 37 is installed and the SDK location is configured for your machine.
4. Allow Gradle sync and any required toolchain/dependency downloads to finish.
5. Select an emulator or connected Android device running API 24 or newer, then click Run.

SDK paths belong in the local, ignored local.properties file. The original development environment used D:\Android\Sdk and D:\Android\Gradle; other machines can use their own locations.

Build and test

Test sources and local verification scripts are excluded from this repository. The recorded unit-test results below come from the development checkout, where those files remain available. A fresh clone can build the app and run lint, but cannot reproduce the 13 unit tests without those sources.


From the project root in Windows PowerShell:

.\gradlew.bat testDebugUnitTest lintDebug assembleDebug

The test task runs the local tests when their sources are present. To build and inspect a fresh clone:

.\\gradlew.bat lintDebug assembleDebug

To execute the development checks again instead of reusing up-to-date results:

.\gradlew.bat testDebugUnitTest lintDebug assembleDebug --rerun-tasks

Generated outputs:

- Debug APK: app/build/outputs/apk/debug/app-debug.apk
- Unit-test report: app/build/reports/tests/testDebugUnitTest/index.html
- Lint report: app/build/reports/lint-results-debug.html

Build outputs and local test reports are ignored by Git and are not included in the repository.

How the code works

Digit button -> AmountEntry -> updated amount
                              ↓
                      ChangeCalculator
                              ↓
              MainActivity updates the screen

| File | Responsibility |
|---|---|
| MainActivity.kt (app/src/main/java/com/example/vangtichai/MainActivity.kt) | Handles clicks, renders results, applies insets, restores state, and controls appearance. |
| AmountEntry.kt (app/src/main/java/com/example/vangtichai/AmountEntry.kt) | Appends digits and checks the input limit before multiplication. |
| ChangeCalculator.kt (app/src/main/java/com/example/vangtichai/ChangeCalculator.kt) | Calculates an ordered list of denomination/count pairs independently of Android views. |
| Portrait layout (app/src/main/res/layout/activity_main.xml) | Amount header, note table, conventional four-row keypad, and theme icon. |
| Landscape layout (app/src/main/res/layout-land/activity_main.xml) | Compact table beside a three-row keypad: 1-4, 5-8, then 9 / 0 / Clear. |
| Default dimensions (app/src/main/res/values/sizes.xml) | Sizes, spacing, padding, text sizes, and match-constraint zero. |
| Landscape dimensions (app/src/main/res/values-land/sizes.xml) | Compact overrides for the shorter screen. |
| Strings (app/src/main/res/values/strings.xml) and styles (app/src/main/res/values/styles.xml) | Visible text, accessibility labels, and shared view appearance. |

Calculation

For each denomination, from largest to smallest:

val count = remaining / denomination
remaining %= denomination

For 234, two 100s leave 34; one 20 leaves 14; one 10 leaves 4; two 2s leave zero. Every denomination still produces a row, even when its count is zero.

Layout selection and state

Android automatically selects layout-land/activity_main.xml in landscape and the default layout otherwise. Both contain matching view IDs. Landscape dimensions override defaults by resource name. Tablets reuse these responsive layouts.

The amount is saved in onSaveInstanceState(), restored in onCreate(), and used to recalculate counts. Orientation is not locked, and Activity recreation is not bypassed with configChanges.

Appearance

The app initially follows the device theme. Tapping the moon or sun saves an explicit choice in SharedPreferences and applies it through AppCompatDelegate.localNightMode. The Material DayNight theme supplies the corresponding colors.

Theme switching recreates the Activity, so the same saved-state mechanism preserves the amount. The theme preference survives reopening; an amount is not promised to survive a force-stop/relaunch.

Verification record

Documentation reviewed on 2026-10-17. The following summarizes checks actually performed; a passing build is not treated as proof of visual correctness.

Build and unit tests

- A forced Gradle rerun completed successfully.
- 13 unit tests passed: five input tests, seven calculation tests, and one starter test.
- Coverage includes 234, 786, zero, exact denominations, maximum input, leading zeros, invalid inputs, and reconstruction of amounts 0-2000.
- The latest standard build/test/lint command also succeeded, reusing up-to-date outputs where applicable.
- Lint reported zero errors and one warning for the empty values-v23 folder. Its former theme resources were merged into the default theme. Empty directories are not tracked by Git, so this local warning may not occur in a fresh clone.

Actual emulator profiles

| Actual profile | Android | Native screen configuration | Result |
|---|---|---|---|
| Pixel 10 Pro XL (Pixel_10_Pro_XL, Pixel_10_Pro_XL_2) | Android 17 / API 37 | 1344x2992px, 480dpi; about 448x997dp portrait and the rotated equivalent | Input, counts, Clear, rotation, limits, and visual checks passed in both orientations. |
| Pixel Tablet (Pixel_Tablet) | Android 15 / API 35 | 2560x1600px, 320dpi; 800x1280dp portrait and 1280x800dp landscape | Full core runtime sequence and visual checks passed in both orientations before the theme icon was added. |

The theme icon was subsequently tested on Pixel 10 Pro XL: switching both ways preserved amounts/counts, the choice survived reopening, rotation worked, and Clear reset the results. The updated theme icon has not been runtime-tested on Pixel Tablet.

Display overrides - separate from hardware-profile tests

All four logical screen sizes below passed the full core runtime sequence using temporary 160dpi overrides on the available Pixel 10 Pro XL and Pixel Tablet emulators:

| Logical display size | Orientation | Result |
|---|---|---|
| 411x731dp | Portrait | PASS |
| 731x411dp | Landscape | PASS; all rows and controls visible without scrolling |
| 800x1280dp | Portrait | PASS |
| 1280x800dp | Landscape | PASS |

Screenshots were inspected at maximum input for clipping, overlap, missing controls, and system-bar coverage at default font size. These sizes describe the full display before insets.

After the theme icon was added, 411x731dp and 731x411dp were rechecked on Pixel 10 Pro XL, including theme changes at maximum input. Both passed.

Remaining verification

- Actual Pixel XL profile, portrait and landscape: NOT TESTED.
- Actual Nexus 10 profile, portrait and landscape: NOT TESTED.
- Pixel 10 Pro XL is not Pixel XL; Pixel Tablet is not Nexus 10. Display overrides do not replace those profile checks.
- Large-font, display-zoom, and comprehensive accessibility checks remain unperformed.

Submission verification remains incomplete until the required actual profiles are tested and their results recorded.

Manual runtime checks

Run the app on a connected device or emulator, then follow this sequence. The ADB automation scripts used during development are not included in this repository.


1. Confirm initial amount 0 and eight zero counts.
2. Tap 2, 3, 4 and check each intermediate amount.
3. Verify 234 and 786 against the example table.
4. Enter 786, rotate both ways, then append 2; expect 7862.
5. Verify 500 and Clear.
6. Enter 007; expect 7. Enter nine 9s, then another digit; expect 999999999.
7. Switch themes, check preserved results, and reopen to check the saved theme.
8. Inspect every row/button for clipping, overlap, and system-bar coverage.

To create the remaining profiles, open Tools -> Device Manager -> + -> Create Virtual Device. Select Phone -> Pixel XL or Tablet -> Nexus 10, choose a compatible system image, and finish setup. Run the sequence above in both orientations and record the actual profile, Android version, size, density, and result.

README.md provides the formatted version for GitHub.
