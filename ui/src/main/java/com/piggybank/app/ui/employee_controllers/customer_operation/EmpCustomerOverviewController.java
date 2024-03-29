package com.piggybank.app.ui.employee_controllers.customer_operation;

import com.piggybank.app.backend.customers.CustomerCorporate;
import com.piggybank.app.backend.customers.CustomerPrivate;
import com.piggybank.app.backend.customers.money_operations.Transaction;
import com.piggybank.app.backend.customers.money_operations.Credit;
import com.piggybank.app.backend.customers.money_operations.Loan;
import com.piggybank.app.ui.employee_controllers.EmpMainController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

public class EmpCustomerOverviewController extends EmpMainController implements Initializable {
    @FXML
    private AnchorPane privateCustomerInfoAnchorPane;
    @FXML
    private AnchorPane corporateCustomerInfoAnchorPane;
    @FXML
    private Label customerIdLabel;
    @FXML
    private Label customerSSNLabel;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Label companyNameLabel;
    @FXML
    private Label companyIdLabel;
    @FXML
    private Label companyOrgNrLabel;
    @FXML
    private Label accountNameLabel;
    @FXML
    private Label accountBalanceLabel;
    @FXML
    private Label balanceDebtLabel;
    @FXML
    private Label interestLabel;
    @FXML
    private Label interestRateLabel;
    @FXML
    private Label initialLabel;
    @FXML
    private Label initialAmountLabel;
    @FXML
    private Label accountTypeLabel;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private CheckBox inTransactionCheckBox;
    @FXML
    private CheckBox outTransactionCheckBox;
    @FXML
    private ListView<String> accountsListView;
    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TableColumn<Transaction, String> senderColumn;
    @FXML
    private TableColumn<Transaction, String> receiverColumn;
    @FXML
    private TableColumn<Transaction, String> amountColumn;
    @FXML
    private TableColumn<Transaction, String> messageColumn;
    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;

    private Parent root;
    private Stage stage;
    private FXMLLoader loader;

    public void initialize(URL arg0, ResourceBundle arg1) {
        super.showCurrentEmployee();
        showCurrentCustomer();
        displayAccounts();

        currentAccount = null; //to avoid another customer's bank account still being active

        interestLabel.setVisible(false);
        interestRateLabel.setVisible(false);
        initialLabel.setVisible(false);
        initialAmountLabel.setVisible(false);
        errorMessageLabel.setVisible(false);

		inTransactionCheckBox.setSelected(true);
		outTransactionCheckBox.setSelected(true);
    }

    public void showCurrentCustomer() { //display customer info above the side menu to the left
        if (currentCustomer instanceof CustomerPrivate) {
            CustomerPrivate currentPrivate = (CustomerPrivate) currentCustomer;
            privateCustomerInfoAnchorPane.setVisible(true);
            corporateCustomerInfoAnchorPane.setVisible(false);
            customerSSNLabel.setText(currentPrivate.getSsn());
            customerNameLabel.setText(currentPrivate.getFullName());
            customerIdLabel.setText(currentPrivate.getUserId());
        } else {
            CustomerCorporate currentCorporate = (CustomerCorporate) currentCustomer;
            privateCustomerInfoAnchorPane.setVisible(false);
            corporateCustomerInfoAnchorPane.setVisible(true);
            companyNameLabel.setText(currentCorporate.getCompanyName());
            companyIdLabel.setText(currentCorporate.getUserId());
            companyOrgNrLabel.setText(currentCorporate.getOrgNumber());
        }
    }

    public void displayAccounts(){ //display the account IDs of the customer's accounts in a listview
        SortedSet<String> accountKeys = new TreeSet<>(currentCustomersAccounts.keySet());
        accountsListView.getItems().addAll(accountKeys);
        accountsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                String currentAccountId = accountsListView.getSelectionModel().getSelectedItem();
                currentAccount = currentCustomersAccounts.get(currentAccountId);
                accountNameLabel.setText(currentAccount.getAccountName());
                accountBalanceLabel.setText(currentAccount.getBalanceString() + " SEK");

                //display account info beneath the listview
                if(currentAccount instanceof Credit){
                    Credit currentCredit = (Credit) currentAccount;
                    accountTypeLabel.setText("CREDIT:");
                    balanceDebtLabel.setText("DEBT:");
                    interestLabel.setVisible(true);
                    interestRateLabel.setVisible(true);
                    interestRateLabel.setText(Double.toString(currentCredit.getInterestRate()));
                } else if(currentAccount instanceof Loan){
                    Loan currentLoan = (Loan) currentAccount;
                    accountTypeLabel.setText("LOAN:");
                    balanceDebtLabel.setText("DEBT");
                    interestLabel.setVisible(true);
                    interestRateLabel.setVisible(true);
                    interestRateLabel.setText(Double.toString(currentLoan.getInterestRate()));
                    initialLabel.setVisible(true);
                    initialAmountLabel.setVisible(true);
                    initialAmountLabel.setText(Double.toString(currentLoan.getInitialAmount()));
                } else {
                    accountTypeLabel.setText("ACCOUNT:");
                    balanceDebtLabel.setText("BALANCE:");
                    interestLabel.setVisible(false);
                    interestRateLabel.setVisible(false);
                    initialLabel.setVisible(false);
                    initialAmountLabel.setVisible(false);
                }

                //factory method used when populating transactionsTable
                senderColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("senderAccountId"));
                receiverColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("receiverAccountId"));
                amountColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("amountString"));
                messageColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("message"));
                dateColumn.setCellValueFactory(new PropertyValueFactory<Transaction, LocalDate>("date"));

                transactionsTable.setItems(currentAccount.getTransactions());

                sortTransactionsTable();
                // Stop from scrolling horizontally in TableView
                transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            }
        });
    }

    public void sortTransactionsTable() {
        // Sort dates from lowest to highest
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        transactionsTable.getSortOrder().add(dateColumn);
    }


    public void showError(String message){
        errorMessageLabel.setVisible(true);
        errorMessageLabel.setText(message);
    }

    public void manageFunds(ActionEvent event) throws IOException { //manageFundsButton, if an account is selected -> switches to scene: EmpManageFunds
        if(currentAccount == null){
            showError("You must select an account.");
        } else {
            loader = new FXMLLoader(getClass().getResource("/com/piggybank/app/ui/employee_scenes/manage_controllers/EmpManageFunds.fxml"));
            root = loader.load();

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void addAccount(ActionEvent event) throws IOException { //addAccountButton, switches to scene: EmpAddAccount
        loader = new FXMLLoader(getClass().getResource("/com/piggybank/app/ui/employee_scenes/customer_operation/EmpAddAccount.fxml"));
        root = loader.load();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void makeTransaction(ActionEvent event) throws IOException { // makeTransactionButton, switches to scene: EmpMakeTransaction
        loader = new FXMLLoader(getClass().getResource("/com/piggybank/app/ui/employee_scenes/EmpMakeTransaction.fxml"));
        root = loader.load();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

	public void toggleIncomingTransactions(){
		if((!inTransactionCheckBox.isSelected() && outTransactionCheckBox.isSelected())){
			ObservableList<Transaction> outgoingTransactions = FXCollections.observableArrayList();
			for(Transaction transaction : currentAccount.getTransactions()){
				if(transaction.getAmount() < 0){
					outgoingTransactions.add(transaction);
				}
			}
			transactionsTable.setItems(outgoingTransactions);
            sortTransactionsTable();
		}else if(!inTransactionCheckBox.isSelected() && !outTransactionCheckBox.isSelected()){
			inTransactionCheckBox.setSelected(true);
		}else{
			transactionsTable.setItems(currentAccount.getTransactions());
            sortTransactionsTable();
		}
	}

	public void toggleOutgoingTransactions(){
		if(!outTransactionCheckBox.isSelected() && inTransactionCheckBox.isSelected()){
			ObservableList<Transaction> incomingTransactions = FXCollections.observableArrayList();
			for(Transaction transaction : currentAccount.getTransactions()){
				if(transaction.getAmount() > 0){
					incomingTransactions.add(transaction);
				}
			}
			transactionsTable.setItems(incomingTransactions);
            sortTransactionsTable();
		} else if(!outTransactionCheckBox.isSelected() && !inTransactionCheckBox.isSelected()){
			outTransactionCheckBox.setSelected(true);
		}else{
			transactionsTable.setItems(currentAccount.getTransactions());
            sortTransactionsTable();
		}
	}

}
