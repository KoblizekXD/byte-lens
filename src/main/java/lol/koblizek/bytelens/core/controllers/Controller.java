package lol.koblizek.bytelens.core.controllers;

import javafx.fxml.FXML;
import lol.koblizek.bytelens.core.ByteLens;

public abstract class Controller {

    protected ByteLens byteLens;

    @FXML
    public abstract void initialize();

    public void setByteLens(ByteLens byteLens) {
        if (this.byteLens != null)
            throw new IllegalStateException("App instance already set");
        this.byteLens = byteLens;
    }
}
