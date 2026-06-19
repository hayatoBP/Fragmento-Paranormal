module com.mycompany.fragmentoparanormal {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens com.mycompany.fragmentoparanormal.controller to javafx.fxml;
    opens com.mycompany.fragmentoparanormal.model to javafx.fxml;
    opens com.mycompany.fragmentoparanormal.util to javafx.fxml;
    opens com.mycompany.fragmentoparanormal.service to javafx.fxml;
    opens com.mycompany.fragmentoparanormal.dao to javafx.fxml;

    exports com.mycompany.fragmentoparanormal.controller;
    exports com.mycompany.fragmentoparanormal.model;
    exports com.mycompany.fragmentoparanormal.util;
    exports com.mycompany.fragmentoparanormal.service;
    exports com.mycompany.fragmentoparanormal.dao;
}