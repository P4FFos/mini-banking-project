package com.piggybank.app.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class EmployeeMainController implements Initializable {
    //.....................FXML ELEMENTS...........................

    @FXML
    private AnchorPane employeeStart;

    //---------------HEADER MENU------------------------------------

    @FXML
    private Label userIdLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Button logoutButton;

    //---------------LEFT MENU------------------------------------

    @FXML
    private Label searchMsg;
    @FXML
    private Button employeeStartButton;
    @FXML
    private Button customerCardButton;
    @FXML
    private Button searchCustomer;
    @FXML
    private Button addCustomerButton;
    @FXML
    private TextField customerSearchTextField;

    //---------------CUSTOMER CARD------------------------------------

    @FXML
    private AnchorPane customerCard;
    @FXML
    private Label customerIdLabel;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Button selectAccountButton;
    @FXML
    private ListView<String> accountsListView; //will be list of accounts
    @FXML
    private ListView<String> accountHistoryListView; //will be list of the current account's transactions

    //---------------CUSTOMER CREATION---------------------------------

    @FXML
    private AnchorPane customerCreationPane;

    //.................................................................

    private Parent root;
    private Stage stage;
    private Scene scene;

    //---------------------PLACEHOLDER STUFF-------------------------------

    private Map<String, String> customers = new HashMap<>();
    private String customerID;
    private String customerName;
    private String[] accounts = {"main_account", "savings", "emergency", "travel"};
    private String currentAccount = "";

    //----------------------------METHODS----------------------------------

    public void showEmployeeStart(){
        employeeStart.setVisible(true);
        customerCard.setVisible(false);
        customerCreationPane.setVisible(false);
        customerSearchTextField.setPromptText("Enter customerID/SSN"); //Resets TextField
        customerID = ""; //Resets TextField
        customerName = ""; //Resets TextField

        fillCustomers(); //Placeholder mock customer data. Will search through backend hashmap of customers when backend is ready.
    } //Shows employeeStart AnchorPane in content area of EmployeeMainScene
    public void displayUser(String id, String name){
        userIdLabel.setText(id);
        userNameLabel.setText(name);
    } //Shows employee's user id and name in header menu
    public void logout(ActionEvent event) throws IOException {
        //Later: add alert with options to save before logging out, cancelling logout and logging out without saving
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScene.fxml"));
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    } //Logs out employee and switches to StartScene
    public void fillCustomers(){
        customers.put("010101-1234", "Anna Andersson");
        customers.put("020202-2345", "Babben Borg");
        customers.put("030303-3456", "Charles Choco");
        customers.put("040404-4567", "David Dancer");
        customers.put("050505-5678", "Eve Ericson");
    } // Fills String[] customers with some mock customers
    public void searchCustomer(ActionEvent event){
        //Placeholder logic.
        if(customers.containsKey(customerSearchTextField.getText())){
            customerID = customerSearchTextField.getText();
            customerName = customers.get(customerID);
            showCustomerCard(customerID, customerName);
            searchMsg.setText("");
        } else {
            searchMsg.setText("Not found.");
        }
    }

    public void showCustomerCreation(){
        employeeStart.setVisible(false);
        customerCard.setVisible(false);
        customerCreationPane.setVisible(true);
    } //Shows customerCreationPane AnchorPane in content area of EmployeeMainScene
    public void addNewCustomer(ActionEvent event){
        //HEJ HEJ
    }

    public void showCustomerCard(){
        //Implement check to see if there is an active customer, user must otherwise enter ssn/customerId in search field
        customerCard.setVisible(true);
        employeeStart.setVisible(false);
        customerCreationPane.setVisible(false);
    } //Shows customerCard AnchorPane in content area of EmployeeMainScene when clicking customerCardButton
    public void showCustomerCard(String id, String name){
        //When backend is eady, use Customer currentCustomer instead of id and name
        customerIdLabel.setText(id);
        customerNameLabel.setText(name);
        customerCard.setVisible(true);
        employeeStart.setVisible(false);
        customerCreationPane.setVisible(false);
    } //Shows customerCard AnchorPane in content area of EmployeeMainScene when doing a customer search
    public void initialize(URL arg0, ResourceBundle arg1){
        accountsListView.getItems().addAll(accounts);
        accountsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2){
                currentAccount = accountsListView.getSelectionModel().getSelectedItem();
                System.out.println("Current account: " + currentAccount);
            }
        });
    } //Initializes accountsListView with elements in accounts, selection sets currentAccount
    public void showAccount(){
        accountHistoryListView.getItems().add(currentAccount);
    } //Just a test to fill the ListView that will show transaction history from selecting an account in accountsListView

}
