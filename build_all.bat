@echo off
setlocal enabledelayedexpansion

cd /d %~dp0

del /q target\situmorangapps-1.0-SNAPSHOT-shaded.jar >nul 2>&1

echo === [1] Build project dengan Maven ===
mvn clean package
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Gagal build project!
    pause
    exit /b
)

echo === [2] Hapus folder runtime dan installer lama ===
if exist runtime (
    rmdir /s /q runtime
)
if exist installer (
    rmdir /s /q installer
)

echo === [3] Buat custom runtime image dengan jlink ===
jlink ^
--module-path "%JAVA_HOME%\jmods;C:\Program Files\Java\javafx-jmods-17.0.1" ^
--add-modules java.base,java.desktop,java.logging,javafx.controls,javafx.fxml ^
--output runtime
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Gagal membuat runtime image!
    pause
    exit /b
)

echo === [4] Buat file EXE dengan jpackage ===
jpackage ^
--name SitumorangKasirApp ^
--input target ^
--main-jar situmorangapps-1.0-SNAPSHOT-shaded.jar ^
--main-class com.situmorang.id.situmorangapps.app.Main ^
--type exe ^
--runtime-image runtime ^
--dest installer ^
--verbose > jpackage.log 2>&1

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Gagal membuat installer! Lihat isi jpackage.log berikut:
    echo.
    type jpackage.log
    echo.
    pause
    exit /b
)

echo.
echo ✅ Sukses! File EXE telah dibuat di folder 'installer'
echo 📄 Log proses tersedia di jpackage.log
pause
