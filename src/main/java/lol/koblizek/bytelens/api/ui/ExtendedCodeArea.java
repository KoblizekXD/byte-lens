package lol.koblizek.bytelens.api.ui;

import lol.koblizek.bytelens.core.ByteLens;
import org.fxmisc.richtext.CodeArea;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExtendedCodeArea extends CodeArea {

    private ExecutorService highlighter;

    public ExtendedCodeArea(ByteLens bl) {
        super();
        highlighter = Executors.newSingleThreadExecutor();
        bl.getExecutors().add(highlighter);
    }
}
