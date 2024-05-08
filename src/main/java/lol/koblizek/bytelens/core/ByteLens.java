package lol.koblizek.bytelens.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.resource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ByteLens extends Application {

    private static ByteLens instance;

    public static ByteLens getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch();
    }

    private Stage currentStage;
    private final List<ToolWindow> toolWindows;
    private final Logger logger;

    @Override
    public void start(Stage stage) throws Exception {
        currentStage = stage;
        Scene scene = getResourceManager().getScene("/lol/koblizek/bytelens/views/main-view.fxml");
        stage.setTitle("ByteLens");
        stage.setScene(scene);
        stage.show();
    }

    public ByteLens() {
        logger = LoggerFactory.getLogger(getClass());

        Thread.setDefaultUncaughtExceptionHandler(new ExecutionExceptionHandler());
        ResourceManager.init();
        getResourceManager().get("/lol/koblizek/bytelens/fonts/inter-font.ttf")
                .toFont();

        toolWindows = new ArrayList<>();
        toolWindows.add(new ToolWindow("Project", null, getResourceManager()
                .getJBIcon("AllIcons.Expui.Toolwindow.Project", true).toSVG(), ToolWindow.Placement.LEFT));
        toolWindows.add(new ToolWindow("Project", null, getResourceManager()
                .getJBIcon("AllIcons.Expui.Toolwindow.Project", true).toSVG(), ToolWindow.Placement.BOTTOM));

        instance = this;
    }

    public ResourceManager getResourceManager() {
        return ResourceManager.getInstance();
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public List<ToolWindow> getToolWindows() {
        return Collections.unmodifiableList(toolWindows);
    }

    public Logger getLogger() {
        return logger;
    }
}
