package lol.koblizek.bytelens.api.ui;

import com.sun.javafx.scene.EventHandlerProperties;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class PathTextField extends StackPane {

    private ObjectProperty<EventHandler<? super MouseEvent>> onIconClick;

    public PathTextField() {
        getChildren().add(new TextField());
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
}
