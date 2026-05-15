{ pkgs ? import <nixpkgs> {
    config.allowUnfree = true;
    config.android_sdk.accept_license = true;
  }
}:

let
  androidSdk = pkgs.androidenv.composeAndroidPackages {
    platformVersions = [ "36" ];
    buildToolsVersions = [ "36.0.0" "35.0.0" "34.0.0" ];
    cmakeVersions = [ "3.22.1" ];
    includeNDK = true;
    ndkVersion = "27.0.12077973";
    includeEmulator = false;
    includeSystemImages = false;
    includeSources = false;
    abiVersions = [ "x86_64" ];
  };
in
pkgs.mkShell {
  packages = with pkgs; [
    jdk17
  ] ++ [ androidSdk.androidsdk ];

  ANDROID_HOME = "${androidSdk.androidsdk}/libexec/android-sdk";
  ANDROID_SDK_ROOT = "${androidSdk.androidsdk}/libexec/android-sdk";
  ANDROID_NDK_HOME = "${androidSdk.androidsdk}/libexec/android-sdk/ndk-bundle";

  shellHook = ''
    _bt() { ls -d "$ANDROID_HOME"/build-tools/*/ 2>/dev/null | tail -1; }
    sign() {
      if [ $# -lt 2 ]; then
        echo "Usage: sign <keystore.jks> <key-alias> [apk-path]"
        echo "  Signs the release APK with your keystore."
        echo "  Default APK: app/build/outputs/apk/release/app-release-unsigned.apk"
        echo "  Signed output: same directory as input, with -signed suffix"
        return 1
      fi
      local bt="$(_bt)"
      if [ -z "$bt" ]; then
        echo "Error: build-tools not found in $ANDROID_HOME"
        return 1
      fi
      local ks="$1"
      local keyalias="$2"
      local apk="''${3:-app/build/outputs/apk/release/app-release-unsigned.apk}"
      local dir="$(dirname "$apk")"
      local base="$(basename "$apk" .apk)"
      local aligned="$dir/${base}-aligned.apk"
      local out="$dir/${base%-unsigned}-signed.apk"
      echo "=== Aligning $apk..."
      "$bt"zipalign -v -p 4 "$apk" "$aligned"
      echo "=== Signing -> $out"
      "$bt"apksigner sign --ks "$ks" --ks-key-alias "$keyalias" --out "$out" "$aligned"
      echo "=== Cleaning up..."
      rm -f "$aligned"
      echo "=== Done: $out"
    }

    echo "=== Athena Liberated Build Environment ==="
    echo "JDK: $(java --version 2>&1 | head -1)"
    echo "Android SDK: $ANDROID_HOME"
    echo "Ready. Build with: ./gradlew assembleRelease"
    echo "Sign with: sign <keystore.jks> <key-alias> [apk-path]"
  '';
}
