package com.piggybank.app.ui;

import com.piggybank.app.backend.customers.Customer;
import com.piggybank.app.backend.customers.CustomerCorporate;
import com.piggybank.app.backend.customers.CustomerPrivate;
import com.piggybank.app.backend.exceptions.PasswordException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class EmpCustomerInfoController extends EmpMainController {
    @FXML
    private Label empInitialsLabel;
    @FXML
    private Label empIdLabel;
    @FXML
    private Button editStreetButton;
    @FXML
    private Button editZipButton;
    @FXML
    private Button editCityButton;
    @FXML
    private Button editPhoneButton;
    @FXML
    private Button editEmailButton;
    @FXML
    private Button editPasswordButton;
    @FXML
    private Button saveNewStreetButton;
    @FXML
    private Button saveNewZipButton;
    @FXML
    private Button saveNewCityButton;
    @FXML
    private Button saveNewPhoneButton;
    @FXML
    private Button saveNewEmailButton;
    @FXML
    private Button getSaveNewPasswordButton;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Label customerIdLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField zipField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;


    public void streetEditable(){ //editStreetButton
        streetField.setEditable(true);
    }
    //implement the "Editable" methods the same way as streetEditable() for their corresponding fields
    public void zipEditable(){ //editZipButton
        zipField.setEditable(true);
    }
    public void cityEditable(){ //editCityButton
        cityField.setEditable(true);
    }
    public void phoneEditable(){ //editPhoneButton
        phoneField.setEditable(true);
    }
    public void emailEditable(){ //editEmailButton
        emailField.setEditable(true);
    }
    public void passwordEditable(){ //editPasswordButton
        passwordField.setEditable(true);
    }
    public void setNewStreet(){ //saveNewStreetButton
        String newStreet = streetField.getText();
        streetField.setText(newStreet);
        EmpMainController.currentCustomer.setStreet(newStreet);
        streetField.setEditable(false);
    }
    //implement the "setNew" methods the same way as setNewStreet() and create the necessary setters in Customer and ContactCard
    public void setNewZip(){ //saveNewZipButton
        String newZip = zipField.getText();
        zipField.setText(newZip);
        EmpMainController.currentCustomer.setZipCode(newZip);
        zipField.setEditable(true);
    }
    public void setNewCity(){ //saveNewCityButton
        String newCity = cityField.getText();
        cityField.setText(newCity);
        EmpMainController.currentCustomer.setCity(newCity);
        cityField.setEditable(true);
    }
    public void setNewPhone(){ //saveNewPhoneButton
        String newPhone = phoneField.getText();
        phoneField.setText(newPhone);
        EmpMainController.currentCustomer.setPhoneNumber(newPhone);
        phoneField.setEditable(true);
    }
    public void setNewEmail(){ //saveNewEmailButton
        String newEmail = emailField.getText();
        emailField.setText(newEmail);
        EmpMainController.currentCustomer.setEmail(newEmail);
        emailField.setEditable(false);
    }
    public void setNewPassword() { //saveNewPasswordButton
        try {
            String newPassword = passwordField.getText();
            passwordField.setText(newPassword);
            EmpMainController.currentCustomer.setPassword(newPassword);
            passwordField.setEditable(false);
        } catch (PasswordException passwordException) {
            System.out.println("Password must be at least 8 characters and include uppercase letters and numbers.");
        }
        //If the employee has checked the customer's id in real life,
        //they don't need to enter the old password to be able to update
        //it. It could be a service performed by a bank employee for a
        //customer who has forgotten their password.
    }

    public void showCurrentEmployee(){
        empIdLabel.setText(EmpMainController.currentEmployee.getUserId());
        empInitialsLabel.setText(EmpMainController.currentEmployee.getInitials());
        System.out.println("Customer Info Page. Logged in as: " + EmpMainController.currentEmployee.getInitials());
    }

    public void showCurrentCustomer(){
        customerIdLabel.setText(EmpMainController.currentCustomer.getUserId());
        streetField.setText(EmpMainController.currentCustomer.getStreet());
        zipField.setText(EmpMainController.currentCustomer.getZipCode());
        cityField.setText(EmpMainController.currentCustomer.getCity());
        phoneField.setText(EmpMainController.currentCustomer.getPhoneNumber());
        emailField.setText(EmpMainController.currentCustomer.getEmail());
        if (EmpMainController.currentCustomer instanceof CustomerPrivate) {
            CustomerPrivate currentPrivate = (CustomerPrivate) EmpMainController.currentCustomer;
            customerNameLabel.setText(currentPrivate.getName());
            customerIdLabel.setText(currentPrivate.getUserId());
            //customerSsnLabel.setText(currentPrivate.getSSN());
        } else if (EmpMainController.currentCustomer instanceof CustomerCorporate) {
            CustomerCorporate currentCorporate = (CustomerCorporate) EmpMainController.currentCustomer;
            customerNameLabel.setText(currentCorporate.getCompanyName());
            //customerOrgNrLabel.setText(currentCorporate.getOrgNumber());
            //customerSsnLabel.setText(currentCorporate.getOrgNumber());
        }

    }


}
