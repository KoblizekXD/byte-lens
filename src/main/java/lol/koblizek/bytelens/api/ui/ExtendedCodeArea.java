package lol.koblizek.bytelens.api.ui;

import javafx.concurrent.Task;
import lol.koblizek.bytelens.core.ByteLens;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;

import static lol.koblizek.bytelens.api.util.JavaPatterns.PATTERN;

public class ExtendedCodeArea extends CodeArea {

    public ExtendedCodeArea() {
        super();
        setParagraphGraphicFactory(LineNumberFactory.get(this));
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync(ExecutorService service) {
        String text = getText();
        Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(text);
            }
        };
        service.submit(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        setStyleSpans(0, highlighting);
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

    static String[] groups = {"KEYWORD", "PAREN", "BRACE", "BRACKET", "SEMICOLON", "STRING", "COMMENT"};

    private static @NotNull String getStyleClass(Matcher matcher) {
        return Arrays.stream(groups)
                .filter(group -> matcher.group(group) != null)
                .findFirst()
                .map(String::toLowerCase).orElseThrow();
    }

    /**
     * For the proper functioning of the code area, it is necessary to bridge it with the ByteLens instance.
     * This is because the code area is using executor which needs to be tracked by the ByteLens instance.
     * This operation should be done as soon as possible after the creation of the code area.
     * @param byteLens ByteLens instance to bridge with
     */
    public void bridge(@NotNull ByteLens byteLens) {
        multiPlainChanges().successionEnds(Duration.ofMillis(5))
                .retainLatestUntilLater()
                .supplyTask(() -> computeHighlightingAsync(byteLens.getCachedExecutor()))
                .awaitLatest(multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
        appendText("""
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """);
    }
}
