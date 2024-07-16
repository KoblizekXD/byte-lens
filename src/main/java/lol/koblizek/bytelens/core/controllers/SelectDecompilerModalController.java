/*
 * This file is part of byte-lens.
 *
 * Copyright (c) 2024 KoblizekXD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package lol.koblizek.bytelens.core.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lol.koblizek.bytelens.core.ByteLens;
import lol.koblizek.bytelens.core.decompiler.DecompilationManager;
import lol.koblizek.bytelens.core.utils.StringUtils;

import java.util.Arrays;

public class SelectDecompilerModalController extends Controller {

    @FXML private Button doneButton;
    @FXML private ComboBox<String> decompilerSelector;

    public SelectDecompilerModalController(ByteLens byteLens) {
        super(byteLens);
    }

    private static DecompilationManager.Providers getProvider(String version) {
        for (DecompilationManager.Providers value : DecompilationManager.Providers.values()) {
            if (version.toUpperCase().contains(value.toString()))
                return value;
        }
        return null;
    }

    public static void open(ByteLens bl) {
        Stage stage = new Stage();
        stage.setTitle("Select decompiler");
        stage.initOwner(bl.getPrimaryStage());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(bl.getResourceManager().getScene("select-decompiler-modal"));
        stage.setOnHiding(e -> {
            String[] arr = (String[]) ((Stage) e.getTarget()).getScene().lookup("#comboBox")
                    .getUserData();
            bl.getDecompilationManager().setDecompiler(
                    DecompilationManager.Providers.valueOf(arr[0].toUpperCase()),
                    arr[1]
            );
        });
        stage.showAndWait();
    }

    @Override
    public void initialize() {
        var cached = getByteLens().getDecompilationManager().getCachedDecompilers();
        decompilerSelector.getItems().addAll(StringUtils.reverseArray(Arrays.stream(DecompilationManager.Providers.VINEFLOWER.getVersions())
                .map(v -> "Vineflower " + v)
                .map(str -> str + (StringUtils.contains(cached, str) ? " (Cached)" : "")).toArray(String[]::new)));
        decompilerSelector.getSelectionModel().select(0);
        decompilerSelector.getSelectionModel().selectedItemProperty().subscribe(string -> {
            var provider = getProvider(string).toString();
            decompilerSelector.setUserData(new String[] {
                    provider,
                    string.substring(provider.length() + 1).replace(" (Cached)", "")
            });
        });
    }

    @FXML
    public void promptModalClose(MouseEvent mouseEvent) {
        ((Stage)doneButton.getScene().getWindow()).close();
    }
}
