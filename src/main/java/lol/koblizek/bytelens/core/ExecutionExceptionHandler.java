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
