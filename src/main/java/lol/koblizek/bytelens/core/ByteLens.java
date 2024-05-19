package lol.koblizek.bytelens.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.resource.ResourceManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

public final class ByteLens extends Application {

    private static ByteLens instance;

    /**
     * Obtains the currently running instance of the application.
     * It is never null.
     *
     * @return The singleton instance of the application.
     */
    public static @NotNull ByteLens getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch();
    }

    private Stage currentStage;
    private final List<ToolWindow> toolWindows;
    private final Logger logger;
    private final List<ExecutorService> executors;

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
        executors = new ArrayList<>();

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

    @Contract(pure = true)
    public @NotNull @UnmodifiableView List<ToolWindow> getToolWindows() {
        return Collections.unmodifiableList(toolWindows);
    }

    /**
     * @return Main ByteLens logger
     */
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void stop() {
        logger.warn("Stopping...");
        executors.forEach((k -> {
            logger.info("\tStopping executor {}", k.toString());
            k.shutdown();
        }));
    }

    public List<ExecutorService> getExecutors() {
        return executors;
    }
}
