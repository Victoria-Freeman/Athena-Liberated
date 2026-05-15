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
    echo "=== Athena Liberated Build Environment ==="
    echo "JDK: $(java --version 2>&1 | head -1)"
    echo "Android SDK: $ANDROID_HOME"
    echo "Ready. Build with: ./gradlew assembleRelease"
  '';
}
