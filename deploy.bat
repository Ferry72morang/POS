jpackage ^
  --type app-image ^
  --input target ^
  --dest dist ^
  --name SitumorangKasir ^
  --main-jar situmorangapps-1.0-SNAPSHOT-shaded.jar ^
  --main-class com.situmorang.id.situmorangapps.app.Main ^
  --module-path "C:\Program Files\Java\javafx-jmods-17.0.15" ^
  --add-modules javafx.controls,javafx.fxml,java.sql,java.management,java.security.sasl ^
  --java-options "--add-modules java.security.sasl"
