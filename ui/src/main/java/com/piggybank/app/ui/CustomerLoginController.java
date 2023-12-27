package com.piggybank.app.ui;

import com.piggybank.app.backend.Bank;
import com.piggybank.app.backend.exceptions.PasswordException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CustomerLoginController {
    //.....................FXML ELEMENTS...........................
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label incorrectDetailsLabel;

    //..........................METHODS.............................

    public void login(ActionEvent event) throws Exception {
        Stage stage;
        Scene scene;
        Parent root;
        String password;
        String userId;

        try {
            Bank bank = UIMain.bank;
            userId = usernameTextField.getText();
            password = passwordField.getText();

            if (bank.verifyCustomer(userId, password)) { //using method from bank
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerStart.fxml"));
                root = loader.load();

                CustomerStartController controller = loader.getController();
                controller.setCurrentCustomer(bank.getCustomer(userId));
				controller.showStartAccountOverviews();

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            if(e instanceof PasswordException) {
                incorrectDetailsLabel.setText(e.getMessage());
                incorrectDetailsLabel.setVisible(true);
            }
            System.out.println(e.getMessage()); // remove at prod
        }
    }
}
