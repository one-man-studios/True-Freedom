# True-Freedom — You Are Being Used

A horror-themed recovery tracker for quitting pornography, built on the **Easy Peasy Method** (which itself adapts Allen Carr's EasyWay). This repo contains:

**A native Android app** (`True-Freedom-android/`) — built with Kotlin + Jetpack Compose. **No WebView.** Every screen is rendered natively. Pre-built APK included (`True-Freedom-debug.apk`).

Both apps share the same content, the same dark horror aesthetic, and the same mission: replace the question *"Am I resisting?"* with *"The question no longer arises."*

---

## Privacy

**All data is stored locally on your device. No server. No tracking. Fully offline.**

- **Android app**: saves to AndroidX DataStore (encrypted-at-rest on supported devices). No `INTERNET` permission — the APK only requests `VIBRATE` (for habit check-off haptics). Source links open in the external browser via `Intent.ACTION_VIEW`, which doesn't require the app to have network permission.

---

## Developer

**One Man Studios**
- Website: [comfyinsta.neocities.org](https://comfyinsta.neocities.org)
- GitHub: [github.com/one-man-studios](https://github.com/one-man-studios)

Tap the **ℹ icon** next to the app title in the topbar to see this info in-app.

---

## Features

- **Live streak tracker** — days clean, hours saved (updates live every second), urges beaten, win rate
- **72-hour chaser window** — countdown to when the amplified-craving debt expires
- **Milestone progression** — 7 milestones from Day 1 to Day 180
- **The Warning carousel** — 12 fact cards with auto-scroll + manual nav
- **The Damage carousel** — 9 damage cards with auto-scroll
- **Brain View modal** — animated neuron-network visualization showing three brain states. Opens synced to your **current recovery state** (not relapse). Custom-drawn Canvas — no images.
- **Mark Today** — URGE BEATEN / RELAPSE buttons with instant optimistic updates
- **Backdate start** — native DatePicker + TimePicker with 12-hour AM/PM format
- **Erase all** — full reset
- **100% offline** — no network, no account, no telemetry

### Android app

- Built with **Kotlin + Jetpack Compose** (Material 3)
- **No WebView** — every screen, animation, and the brain canvas are native
- Adaptive layout: narrow (<600dp stacked), medium (600–899dp tracker+split), wide (≥900dp 3-column)
- Optimistic UI updates — state changes instantly, DataStore persistence happens in background
- Edge-to-edge disabled — status bar and navigation bar remain visible
- minSdk 24 (Android 7.0+) — covers ~98% of active devices

---

## The SOURCES page — what's in it

54 resources organized into 7 categories, each with its own accent color:

| Category | Count | Color | What's in it |
|---|---|---|---|
| **METHOD & BOOKS** | 4 | Red | Easy Peasy Method, Allen Carr's EasyWay, Rational Recovery |
| **DNS PORNOGRAPHY BLOCKERS** | 11 | Green | AdGuard, CleanBrowsing, OpenDNS FamilyShield, Cloudflare 1.1.1.3, NextDNS, Pi-hole, AdGuard Home, OISD/EasyList Adult blocklists |
| **BLOCKING APPS & SOFTWARE** | 12 | Amber | BlockerX, Cold Turkey, Freedom, Covenant Eyes, Accountable2You, Fortify, Brainbuddy, Truple, BuluBox, Net Nanny, Qustodio, Bark |
| **BROWSER EXTENSIONS** | 7 | Blue | uBlock Origin, BlockSite, StayFocusd, FoxFilter, uBlacklist, Simple Blocker, Brave |
| **COMMUNITIES** | 7 | Purple | r/NoFap, r/pornfree, r/NoSurf, NoFap Forum, Reboot Nation, Fight The New Drug, NoFap Discord |
| **EDUCATIONAL** | 3 | Yellow | Your Brain On Porn research, Fight The New Drug resources, Reboot Nation PIED |
| **DECLASSIFIED & STUDIES** | 10 | Red | CIA MKUltra, Senate hearings, Cambridge brain-scan studies, Volkow D2 research |

Every source card opens its link in the external browser.

---

## Project structure

```
.
├── README.md                      # This file
├── LICENSE                        # MIT
├── True-Freedom-debug.apk           # Pre-built debug APK (ready to install)
└── True-Freedom-android/            # Native Android app source
    ├── build.gradle.kts
    ├── settings.gradle.kts
    ├── gradle.properties
    ├── .gitignore
    ├── gradlew / gradlew.bat       # Gradle wrapper (included)
    ├── gradle/
    │   ├── libs.versions.toml
    │   └── wrapper/
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    └── app/
        ├── build.gradle.kts
        ├── proguard-rules.pro
        └── src/main/
            ├── AndroidManifest.xml           # No INTERNET permission
            ├── java/com/True-Freedom/recovery/
            │   ├── MainActivity.kt              # Entry point, topbar, viewnav, dialogs
            │   ├── data/
            │   │   ├── Content.kt               # All static content (facts, damage, sources, etc.)
            │   │   └── AppDataStore.kt          # DataStore persistence
            │   ├── viewmodel/
            │   │   └── AppViewModel.kt          # Optimistic state + computed values
            │   └── ui/
            │       ├── theme/                   # Color, Theme, Type
            │       ├── components/              # BrainCanvas, Icons, StyledText, BorderExtensions
            │       └── screens/                 # Dashboard, Sources, Benefits, NoBenefits, TMaxx
            └── res/                             # drawable icons, values, xml
```

---

## Getting started

### Android app

**Option A — Pre-built debug APK (fastest)**

A pre-built debug APK is included: `True-Freedom-debug.apk`

```bash
# Transfer to your phone, tap to install.
# Enable "Install unknown apps" for your file manager.

# OR via adb:
adb install True-Freedom-debug.apk
```

**Option B — Build from source**

The Gradle wrapper is included — no system Gradle needed.

```bash
git clone https://github.com/one-man-studios/true-freedom.git
cd true-freedom/True-Freedom-android

export ANDROID_HOME=/path/to/android-sdk

./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

Or open the `True-Freedom-android/` folder in Android Studio.

#### System requirements

- Android 7.0 (API 24) or newer
- ~17 MB install size
- Only permission: `VIBRATE` (for habit check-off haptics)

---

## Architecture notes (Android)

### Optimistic updates

The ViewModel holds an in-memory `MutableStateFlow<AppState>` that is updated **instantly** when the user takes an action (mark urge, relapse, toggle habit, etc.). The UI recomposes immediately with the new value. DataStore persistence happens in the background via `viewModelScope.launch`. This eliminates the 50–100ms delay that pure DataStore read-modify-write cycles would cause.

### Brain canvas

A custom `Canvas` composable builds a neuron network via Poisson-disk sampling inside a parametric brain silhouette. Three states (`relapse`, `current`, `recovery`) differ in color, chaos, dimRatio, pulseSpeed, and fireChance. The modal opens on the **"current"** tab, which interpolates between relapse and recovery based on the user's streak.

### Adaptive layout

Uses `LocalConfiguration.current.screenWidthDp` to select between three layouts:
- **Narrow** (<600dp): tracker + warning + damage stacked vertically
- **Medium** (600–899dp): tracker on top, warning + damage side-by-side below
- **Wide** (≥900dp): 3-column side-by-side

---

## Credits

- **The Easy Peasy Method** by Hackauthor — [easypeasymethod.org](https://easypeasymethod.org/)
- **Allen Carr's EasyWay** — the original method
- **Cambridge brain-scan research** (Voon et al, 2014; Park et al, 2016)
- **Nora Volkow's D2 receptor research**
- **The Church Committee and 1977 Senate MKUltra hearings**

All cited sources are listed in the app's SOURCES page with direct links.

---

## Disclaimer

This app is a self-help tool, not medical advice. If you are experiencing severe depression, anxiety, or sexual dysfunction, consult a licensed healthcare provider.

---

## License

MIT — see https://github.com/one-man-studios/True-Freedom/blob/main/LICENSE. The code is yours. The mission is everyone's.
