module lol.koblizek.bytelens {
    requires batik.transcoder;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires dev.mccue.resolve;
    requires java.desktop;
    requires java.xml;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires org.apache.commons.io;
    requires org.fxmisc.richtext;
    requires static org.jetbrains.annotations;
    requires jdk.xml.dom;
    requires org.slf4j;
    requires static vineflower;

    exports lol.koblizek.bytelens.core;
    opens lol.koblizek.bytelens.core.controllers to javafx.fxml;
    opens lol.koblizek.bytelens.api.ui to javafx.fxml;
    opens lol.koblizek.bytelens.api.ui.toolwindows to javafx.fxml;
}