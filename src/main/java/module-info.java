/*
Copyright (c) 2024 KoblizekXD

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

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