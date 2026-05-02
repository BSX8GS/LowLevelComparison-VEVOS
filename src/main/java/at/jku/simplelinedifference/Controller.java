package at.jku.simplelinedifference;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Controller {
    @FXML
    private TextField tf_PreviousGT;

    @FXML
    private TextField tf_CurrentGT;

    @FXML
    protected void onRunComparison() throws IOException {
        //try {
            Compare compare = new Compare(Path.of(this.tf_PreviousGT.getText()), Path.of(this.tf_CurrentGT.getText()), Path.of("D:\\_Thesis"));
            compare.getNewList();
            compare.compare();
        //} catch(Exception e) {
        //    showInfoDialog(e.getMessage());
        //}

    }
    public void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        tf_PreviousGT.setText(null);
        tf_CurrentGT.setText(null);

        alert.showAndWait();
    }

    @FXML
    public void onSelectPrevFileButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        Stage fileDialogue = new Stage();
        File selectedFile = fileChooser.showOpenDialog(fileDialogue);

        if (selectedFile != null) {
            this.tf_PreviousGT.setText(selectedFile.getAbsolutePath());
            fileDialogue.close();
        }
    }

    @FXML
    public void onSelectCurrFileButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        Stage fileDialogue = new Stage();
        File selectedFile = fileChooser.showOpenDialog(fileDialogue);

        if (selectedFile != null) {
            this.tf_CurrentGT.setText(selectedFile.getAbsolutePath());
            fileDialogue.close();
        }
    }
}