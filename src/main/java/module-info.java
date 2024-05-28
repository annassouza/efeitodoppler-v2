module com.example.efeitodopplerv2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;

    opens com.example.efeitodopplerv2 to javafx.fxml;
    exports com.example.efeitodopplerv2;
}
