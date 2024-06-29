package lol.koblizek.bytelens.core.controllers;

import javafx.fxml.FXML;
import lol.koblizek.bytelens.core.ByteLens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Controller {

    private Logger logger;
    private final ByteLens byteLens;

    @FXML
    public abstract void initialize();

    protected Controller(ByteLens byteLens) {
        this.byteLens = byteLens;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    ByteLens getByteLens() {
        return byteLens;
    }

    Logger getLogger() {
        return logger;
    }
}
