module com.example.sysmatdesktop {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sysmatdesktop to javafx.fxml;
    exports com.example.sysmatdesktop;
}