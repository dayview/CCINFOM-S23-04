module businesspermitsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;
    requires com.github.librepdf.openpdf;
    requires org.slf4j;
    requires java.desktop;


    opens businesspermitsystem to javafx.fxml;
    exports businesspermitsystem;
}