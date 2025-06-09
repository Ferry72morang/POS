module com.situmorang.id.situmorangapps {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.github.librepdf.openpdf;

    exports com.situmorang.id.situmorangapps.app to javafx.graphics;
    exports com.situmorang.id.situmorangapps.controller;
    exports com.situmorang.id.situmorangapps.model;
    exports com.situmorang.id.situmorangapps.util;

    opens com.situmorang.id.situmorangapps.controller to javafx.fxml;
}
