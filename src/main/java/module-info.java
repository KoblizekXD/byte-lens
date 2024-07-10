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
    requires com.github.javaparser.core;
    requires org.objectweb.asm;
    requires lol.koblizek.bytelens.core.decompiler.api;
    requires reactfx;

    uses lol.koblizek.bytelens.core.decompiler.api.Decompiler;

    opens lol.koblizek.bytelens.core.controllers to javafx.fxml;
    opens lol.koblizek.bytelens.api.ui to javafx.fxml;
    opens lol.koblizek.bytelens.api.ui.toolwindows to javafx.fxml;
    exports lol.koblizek.bytelens.core;
    exports lol.koblizek.bytelens.core.utils;
    exports lol.koblizek.bytelens.api;
    exports lol.koblizek.bytelens.api.resource;
    exports lol.koblizek.bytelens.api.util;
    exports lol.koblizek.bytelens.core.decompiler;
}