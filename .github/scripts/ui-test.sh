#!/usr/bin/env bash
# Runs the instrumented smoke test on the running emulator and pulls its screenshots.
set -u
PKG=$(grep -oE 'applicationId *=? *"[^"]+"' app/build.gradle* | grep -oE '"[^"]+"' | tr -d '"')
chmod +x gradlew
# Fresh emulators often show "System UI / Launcher isn't responding" dialogs that steal focus.
adb shell settings put global hide_error_dialogs 1 || true
sleep 15
adb shell am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS >/dev/null 2>&1 || true
adb shell input keyevent KEYCODE_HOME || true
adb logcat -c || true
./gradlew --no-daemon connectedDebugAndroidTest -Pandroid.injected.androidTest.leaveApksInstalledAfterRun=true
status=$?
mkdir -p screens
adb exec-out run-as "$PKG" tar c -C files screens 2>/dev/null | tar x -C . || echo "No screenshots found"
adb logcat -d > screens/logcat.txt || true
if [ $status -ne 0 ]; then
  echo "::group::Failed tests"
  grep -h -A 30 '<failure' app/build/outputs/androidTest-results/connected/debug/*.xml 2>/dev/null | head -80
  echo "::endgroup::"
fi
exit $status
