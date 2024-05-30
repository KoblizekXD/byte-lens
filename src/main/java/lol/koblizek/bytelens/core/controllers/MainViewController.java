package lol.koblizek.bytelens.core.controllers;

import javafx.concurrent.Task;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import lol.koblizek.bytelens.api.ToolWindow;
import lol.koblizek.bytelens.api.resource.ResourceManager;
import lol.koblizek.bytelens.api.ui.PersistentSplitPane;
import lol.koblizek.bytelens.api.ui.SidePane;
import lol.koblizek.bytelens.api.ui.SideToolBar;
import lol.koblizek.bytelens.api.ui.SideToolButton;
import lol.koblizek.bytelens.core.ByteLens;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static lol.koblizek.bytelens.api.util.JavaPatterns.PATTERN;

// This code will have to be eventually rewritten to be more effective and more understandable

public class MainViewController extends Controller {

    public MenuBar menubar;
    public SideToolBar leftToolbar;
    public SidePane leftPanel;
    public PersistentSplitPane splitPaneInner;
    public SidePane bottomPanel;
    public PersistentSplitPane splitPaneOuter;
    public CodeArea codeArea;
    public Menu menu1;

    public MainViewController(ByteLens byteLens) {
        super(byteLens);
    }

    @Override
    public void initialize() {
        ByteLens byteLens = getByteLens();
        ResourceManager rm = byteLens.getResourceManager();

        if (byteLens.getCurrentProject().isEmpty())
            byteLens.getLogger().warn("No project is open, this should not happen. Errors might occur.");

        var tws = new ArrayList<>(byteLens.getToolWindows());
        tws.add(new ToolWindow("Project",
                null,
                ResourceManager.getJBIcon("AllIcons.Expui.Toolwindow.Project", true).toSVG(),
                ToolWindow.Placement.BOTTOM
        ));
        var collected = tws.stream()
                .collect(Collectors.groupingBy(ToolWindow::placement, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<ToolWindow.Placement, List<ToolWindow>> entry : collected.entrySet()) {
            ToolWindow.Placement placement = entry.getKey();
            for (ToolWindow toolWindow : entry.getValue()) {
                SideToolButton tb;
                if (placement == ToolWindow.Placement.LEFT) {
                    leftToolbar.addToolButton((tb = new SideToolButton(toolWindow, leftPanel)), 0);
                    splitPaneInner.addEventHandler(PersistentSplitPane.ON_HIDE, e -> {
                        if (e.getHiding().equals(leftPanel)) {
                            tb.setSelected(false);
                        }
                    });
                    tb.setOnMouseClicked(e -> {
                        if (tb.isSelected()) {
                            splitPaneInner.showPane(leftPanel);
                        } else {
                            splitPaneInner.hidePane(leftPanel);
                        }
                    });
                } else if (placement == ToolWindow.Placement.BOTTOM) {
                    leftToolbar.addToolButton((tb = new SideToolButton(toolWindow, bottomPanel)), 1);
                    splitPaneOuter.addEventHandler(PersistentSplitPane.ON_HIDE, e -> {
                        if (e.getHiding().equals(bottomPanel)) {
                            tb.setSelected(false);
                        }
                    });
                    tb.setOnMouseClicked(e -> {
                        if (tb.isSelected()) {
                            splitPaneOuter.showPane(bottomPanel);
                        } else {
                            splitPaneOuter.hidePane(bottomPanel);
                        }
                    });
                }
            }
        }
        initializeCodeArea();
    }

    private ExecutorService executorService;

    private void initializeCodeArea() {
        executorService = Executors.newSingleThreadExecutor();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.multiPlainChanges().successionEnds(Duration.ofMillis(5))
                .retainLatestUntilLater(executorService)
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
        byteLens.getExecutors().add(executorService);
        codeArea.appendText("""
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """);
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(text);
            }
        };
        executorService.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass = getStyleClass(matcher);
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private static @NotNull String getStyleClass(Matcher matcher) {
        String styleClass =
                matcher.group("KEYWORD") != null ? "keyword" :
                        matcher.group("PAREN") != null ? "paren" :
                                matcher.group("BRACE") != null ? "brace" :
                                        matcher.group("BRACKET") != null ? "bracket" :
                                                matcher.group("SEMICOLON") != null ? "semicolon" :
                                                        matcher.group("STRING") != null ? "string" :
                                                                matcher.group("COMMENT") != null ? "comment" :
                                                                        null; /* never happens */
        assert styleClass != null;
        return styleClass;
    }
}
