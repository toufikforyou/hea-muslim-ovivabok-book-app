#!/usr/bin/env bash
# Runs the instrumented smoke test on the running emulator and pulls its screenshots.
set -u
PKG=$(grep -oE 'applicationId *=? *"[^"]+"' app/build.gradle* | grep -oE '"[^"]+"' | tr -d '"')
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
