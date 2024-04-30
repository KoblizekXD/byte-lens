package lol.koblizek.bytelens.api.ui;

import com.sun.javafx.scene.EventHandlerProperties;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class PathTextField extends StackPane {

    private ObjectProperty<EventHandler<? super MouseEvent>> onIconClick;
    private StringProperty placeholder = new SimpleStringProperty();

    public PathTextField() {
        super();
        TextField t = new TextField();
        t.promptTextProperty().bind(placeholder);
        getChildren().add(t);
        IconButton i = new IconButton(new SimpleStringProperty("/lol/koblizek/bytelens/resources/wallet.png"));
        getChildren().add(i);
        setAlignment(i, Pos.CENTER_RIGHT);
        onIconClick = new SimpleObjectProperty<>();
        onIconClick.bind(i.onMouseClickedProperty());
    }

    public EventHandler<? super MouseEvent> getOnIconClick() {
        return onIconClick.get();
    }

    public void setOnIconClick(EventHandler<? super MouseEvent> onIconClick) {
        this.onIconClick.set(onIconClick);
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder.set(placeholder);
    }

    public String getPlaceholder() {
        return placeholder.get();
    }
}
