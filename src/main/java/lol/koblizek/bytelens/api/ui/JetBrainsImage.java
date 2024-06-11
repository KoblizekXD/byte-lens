package lol.koblizek.bytelens.api.ui;

import javafx.scene.image.ImageView;
import lol.koblizek.bytelens.api.resource.ResourceManager;

public class JetBrainsImage extends ImageView {
    public JetBrainsImage(String icon, int width, int height) {
        setFitHeight(width);
        setFitWidth(height);
        setImage(ResourceManager.getJBIcon(icon, true, width, height));
    }

    public JetBrainsImage(String icon) {
        setFitHeight(16);
        setFitWidth(16);
        setImage(ResourceManager.getJBIcon(icon, true));
    }
}
