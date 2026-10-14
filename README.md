# Arcadium OG9 - clean controller build

Separate source project for the square Android Mini Vibe. Original repo remains untouched.

## Build
Upload these files and directories (including `.github/workflows`) to a **new** GitHub repo, preserving their paths. On Actions, run **Build Arcadium OG9 Clean APK**, then download the artifact.

## Controller architecture
Native Android `dispatchKeyEvent` maintains a held-button bitmask, exported via `ArcadiumNative.getHeld()` to bundled, trusted offline HTML frames. `controller.js` produces a virtual Gamepad and keyboard transitions. Galactic Riot reads the gamepad state continuously, so holding A is not lost between frames.

Confirmed Mini Vibe codes: A96 B97 X99 Y100 Start108 Select109 L1 102 R1 103, Back4. Direction keys use Android DPAD codes.

## Current validation
Source checks only. APK build and physical A-fire / all-nine gameplay still require GitHub Actions and handheld playtesting. No game balance, score correctness, or touch-control removal is guaranteed.

## Safety
The JS interface is exposed only to locally bundled pages: do not add untrusted remote content or remote iframes. This is a test/debug build and must be security-reviewed before deployment.
