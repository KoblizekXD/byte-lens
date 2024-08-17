package lol.koblizek.bytelens.core;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import lol.koblizek.bytelens.api.ui.ExtendedCodeArea;
import lol.koblizek.bytelens.core.controllers.MainViewController;
import lol.koblizek.bytelens.core.utils.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The context used to work with main window of ByteLens.
 * Provides access to operations done only in the main window, such as:
 * <ul>
 *     <li>Opening files</li>
 *     <li>Creating new files</li>
 *     <li>Processing hotkeys</li>
 *     <li>Closing/Opening tabs</li>
 * </ul>
 * The instance of this class may be obtained by calling {@link ByteLens#getContext()}.
 */
public class ApplicationContext {

    private final MainViewController mainViewController;
    private final ByteLens byteLens;

    ApplicationContext(MainViewController mainViewController, ByteLens byteLens) {
        this.mainViewController = mainViewController;
        this.byteLens = byteLens;
    }

    public ByteLens getByteLens() {
        return byteLens;
    }

    public void openTab(@NotNull Property<String> title, @NotNull Node content) {
        Preconditions.nonNull(title, content);
        mainViewController.open(title, content);
    }

    public void open(@NotNull Path path) {
        Preconditions.nonNull(path);
        if (Files.isDirectory(path) || !Files.isReadable(path)) {
            getByteLens().getLogger().error("Failed to open file: {}, not readable or is directory", path);
            return;
        }
        try {
            openTab(
                    new SimpleStringProperty(path.getFileName().toString()),
                    new ExtendedCodeArea(Files.readString(path), getByteLens())
            );
        } catch (IOException e) {
            getByteLens().getLogger().error("Failed to read file", e);
        }
    }

    public int getActiveTab() {
        return mainViewController.getProjectTabPane().getActiveTab();
    }

    public void closeActiveTab() {
        mainViewController.getProjectTabPane().removeTab(mainViewController.getProjectTabPane().getActiveTab());
    }

    public void setActiveTab(int index) {
        mainViewController.getProjectTabPane().setActiveTab(index);
    }

    public void closeTab(int index) {
        mainViewController.getProjectTabPane().removeTab(index);
    }
}
