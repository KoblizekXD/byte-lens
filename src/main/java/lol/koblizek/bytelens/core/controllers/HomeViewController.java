package lol.koblizek.bytelens.core.controllers;

import javafx.scene.control.ListView;
import lol.koblizek.bytelens.api.DefaultProject;
import lol.koblizek.bytelens.core.ByteLens;

public class HomeViewController extends Controller {

    public ListView<String> projectListing;

    public HomeViewController(ByteLens byteLens) {
        super(byteLens);
    }

    @Override
    public void initialize() {
        for (DefaultProject project : getByteLens().getProjects()) {
            projectListing.getItems().add(project.getName());
        }
    }
}
