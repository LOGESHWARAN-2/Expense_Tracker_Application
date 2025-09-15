module com.isa.expensetracker {
    // JDK
    requires java.sql;

    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // DB + migrations
    requires com.zaxxer.hikari;
    requires flyway.core;
    requires org.postgresql.jdbc;

    // Charts
    requires org.jfree.jfreechart;
    requires org.jfree.chart.fx;

    // UI extras
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    // CSV
    requires com.opencsv;
    requires org.kordamp.ikonli.materialdesign2;

    // Exports
    exports com.isa.expensetracker;
    exports com.isa.expensetracker.db;
    exports com.isa.expensetracker.model;
    exports com.isa.expensetracker.repo;
    exports com.isa.expensetracker.service;
    exports com.isa.expensetracker.ui;
    exports com.isa.expensetracker.util;

    opens com.isa.expensetracker.ui to javafx.fxml;
    opens com.isa.expensetracker to javafx.fxml; // if any controller in this pkg
}
