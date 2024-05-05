package lol.koblizek.bytelens.core.controllers;

import javafx.fxml.FXML;
import lol.koblizek.bytelens.api.util.InstanceAccessor;

public interface Controller extends InstanceAccessor {
    @FXML
    void initialize();
}
