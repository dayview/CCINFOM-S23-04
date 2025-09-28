module businesspermitsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens businesspermitsystem to javafx.fxml;
    exports businesspermitsystem;
}