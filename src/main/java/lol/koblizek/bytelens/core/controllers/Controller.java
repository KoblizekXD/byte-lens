package lol.koblizek.bytelens.core.controllers;

import javafx.fxml.FXML;
import lol.koblizek.bytelens.core.ByteLens;
import org.slf4j.Logger;

public abstract class Controller {

    private Logger logger;
    private final ByteLens byteLens;

    @FXML
    public abstract void initialize();

    public Controller(ByteLens byteLens) {
        this.byteLens = byteLens;
    }

    ByteLens getByteLens() {
        return byteLens;
    }

    Logger getLogger() {
        return logger;
    }
}
