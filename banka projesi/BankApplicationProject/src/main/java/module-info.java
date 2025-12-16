module com.example.bankapplicationproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.commons.io;
    requires java.sql;


    opens com.example.bankapplicationproject to javafx.fxml;
    exports com.example.bankapplicationproject;
}