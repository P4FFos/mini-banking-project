package com.piggybank.app.ui.employee_controllers.manage_controllers;

import com.piggybank.app.backend.customers.money_operations.Credit;
import com.piggybank.app.backend.customers.money_operations.Loan;
import com.piggybank.app.ui.employee_controllers.customer_operation.EmpCustomerOverviewController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class EmpManageFundsController extends EmpCustomerOverviewController implements Initializable {
    @FXML
    private Button okButton;
    @FXML
    private CheckBox withdrawCheckBox;
    @FXML
    private CheckBox depositCheckBox;
    @FXML
    private Label accountLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private TextField amountTextField;

    public void initialize(URL arg0, ResourceBundle arg1) {
        super.showCurrentEmployee();
        super.showCurrentCustomer();

        accountLabel.setText(currentAccount.getAccountName());
        balanceLabel.setText(Double.toString(currentAccount.getBalance()));
        errorMessageLabel.setVisible(false);

        System.out.println("Employee Manage Funds (deposit/withdraw) Page. Logged in as: " + currentEmployee.getInitials());
    }

    public void showError(String msg){
        errorMessageLabel.setVisible(true);
        errorMessageLabel.setText(msg);
    }

    public void adjustFunds(){ // okButton
        double currentBalance = currentAccount.getBalance();
        double amount = 0.0;
        String enteredAmount = amountTextField.getText();

        if(!withdrawCheckBox.isSelected() && !depositCheckBox.isSelected()){
            showError("You must choose either withdraw or deposit.");
        } else if (enteredAmount.isEmpty()){
            showError("You must enter an amount.");
        } else {
            amount = extractAmount(enteredAmount);
        }

        if(withdrawCheckBox.isSelected()){
            if(currentAccount instanceof Credit || currentAccount instanceof Loan){
                showError("You cannot withdraw from a credit or loan account.");
            } else if (currentBalance < amount){
                showError("You do not have enough funds in this account.");
            } else {
                currentAccount.setBalance(currentBalance - amount);
            }
        } else if (depositCheckBox.isSelected()){
            currentAccount.setBalance(currentBalance + amount);
        }
        balanceLabel.setText(Double.toString(currentAccount.getBalance()));
    }

    public double extractAmount(String input){
        String amountStr = "";
        if(input.charAt(0) == '-'){
            showError("You cannot deposit or withdraw a negative amount.");
            return 0.0;
        }

        for(int i = 0; i < input.length(); i++){
            if(!Character.isDigit(input.charAt(i))){
                showError("You must enter only digits.");
                return 0.0;
            } else {
                amountStr += input.charAt(i);
                errorMessageLabel.setVisible(false);
            }
        }
        double amount = Double.parseDouble(amountStr);
        return amount;
    }

    public void toggleWithdraw(){
        depositCheckBox.setSelected(false);
    }

    public void toggleDeposit(){
        withdrawCheckBox.setSelected(false);
    }
}