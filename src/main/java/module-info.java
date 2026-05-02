module at.jku.simplelinedifference {
    requires javafx.controls;
    requires javafx.fxml;


    opens at.jku.simplelinedifference to javafx.fxml;
    exports at.jku.simplelinedifference;
}