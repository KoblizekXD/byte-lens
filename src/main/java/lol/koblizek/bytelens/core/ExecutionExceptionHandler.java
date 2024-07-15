/*
 * This file is part of byte-lens, licensed under the GNU General Public License v3.0.
 *
 * Copyright (c) 2024 KoblizekXD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package lol.koblizek.bytelens.core;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

class ExecutionExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger("Crash Handler");

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Platform.runLater(() -> {
            logger.error("An exception has occurred in the application", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ByteLens has crashed");
            alert.setContentText("An exception has occurred in the application. Please report this to the developers.");
            alert.setHeaderText(null);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("Stacktrace:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);

            VBox expContent = new VBox();
            expContent.getChildren().add(label);
            expContent.getChildren().add(textArea);
            VBox.setVgrow(textArea, Priority.ALWAYS);
            alert.getDialogPane().setExpandableContent(expContent);
            alert.getDialogPane().setExpanded(true);

            alert.showAndWait();
        });
    }
}
