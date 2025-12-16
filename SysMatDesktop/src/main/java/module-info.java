module com.institut.sysmat.desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.slf4j;

    opens com.institut.sysmat.desktop.model to com.google.gson;
    opens com.institut.sysmat.desktop.controller to javafx.fxml;


    opens com.institut.sysmat.desktop to javafx.fxml;
    exports com.institut.sysmat.desktop;
}