module businesspermitsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    opens businesspermitsystem to javafx.fxml;
    exports businesspermitsystem;
}