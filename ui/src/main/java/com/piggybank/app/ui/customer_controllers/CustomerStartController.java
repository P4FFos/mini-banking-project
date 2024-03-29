package com.piggybank.app.ui.customer_controllers;

import com.piggybank.app.backend.Bank;
import com.piggybank.app.backend.customers.*;
import com.piggybank.app.backend.customers.money_operations.Credit;
import com.piggybank.app.backend.customers.money_operations.Loan;
import com.piggybank.app.backend.utils.FileHandler;
import com.piggybank.app.ui.UIMain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;



public class CustomerStartController implements Initializable {

    @FXML
    private Label headerActualIdLabel;
    @FXML
    private Label headerCustomerNameLabel;
    @FXML
    private Label startActualTotalBalanceLabel;
    @FXML
    private Label startActualTotalDebtLabel;
    @FXML
    private Label infoNameLabel;
    @FXML
    private Label infoActualUserIdLabel;
    @FXML
    private TableView<Account> assetsTableView;
    @FXML
    private TableColumn<Account, String> assetsNameTableColumn;
    @FXML
    private TableColumn<Account, String> assetsBalanceTableColumn;
	@FXML
    private TableView<Account> debtsTableView;
    @FXML
    private TableColumn<Account, String> debtsNameTableColumn;
    @FXML
    private TableColumn<Account, String> debtsBalanceTableColumn;

    private FXMLLoader loader;
    private Parent root;
    private Stage stage;
    private Scene scene;

    public static Bank bank = UIMain.bank; // refer to bank object created in UIMain
    public static Customer currentCustomer; // refers to the customer object that is logged in
    public static Account currentAccount; // refers to current account that is operated on, if there is one
    public static HashMap<String, Account> currentCustomersAccounts; // refers to the hashmaps of accounts connected to the customer

	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showCurrentCustomer();
        showStartAccountOverviews();
    }

    public void showCurrentCustomer(){ // displays the current customer information to the left
        // different view depending on if current customer is corporate or private
        if (currentCustomer instanceof CustomerPrivate) {
            CustomerPrivate privateCustomer = (CustomerPrivate) currentCustomer;
            headerCustomerNameLabel.setText(privateCustomer.getFullName());
            infoNameLabel.setText(privateCustomer.getFullName());
            infoActualUserIdLabel.setText(privateCustomer.getUserId());
            headerActualIdLabel.setText(privateCustomer.getUserId());
        } else {
            CustomerCorporate corporateCustomer = (CustomerCorporate) currentCustomer;
            headerCustomerNameLabel.setText(corporateCustomer.getCompanyName());
            infoNameLabel.setText(corporateCustomer.getCompanyName());
            infoActualUserIdLabel.setText(corporateCustomer.getUserId());
            headerActualIdLabel.setText(corporateCustomer.getUserId());
        }
    }

	public void showStartAccountOverviews() {
		// set table columns and placeholder
		assetsNameTableColumn.setCellValueFactory(new PropertyValueFactory<Account, String>("accountName"));
		assetsBalanceTableColumn.setCellValueFactory(new PropertyValueFactory<Account, String>("balanceString"));
		assetsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		assetsTableView.setPlaceholder(new Label("No assets"));
		assetsTableView.setSelectionModel(null);

		debtsNameTableColumn.setCellValueFactory(new PropertyValueFactory<Account, String>("accountName"));
		debtsBalanceTableColumn.setCellValueFactory(new PropertyValueFactory<Account, String>("balanceString"));
		debtsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		debtsTableView.setPlaceholder(new Label("No debts"));
		debtsTableView.setSelectionModel(null);

		// observable lists needed to track changes
		ObservableList<Account> assetsOL = FXCollections.observableArrayList();
		ObservableList<Account> debtsOL = FXCollections.observableArrayList();

        // split the hashmap of accounts into two separate ArrayLists
		for(Account account : currentCustomersAccounts.values()){
			if(account instanceof Credit || account instanceof Loan){
				debtsOL.add(account);
			}else{
				assetsOL.add(account);
			}
		}

        // populates the two different table views with data
		assetsTableView.setItems(assetsOL);
		debtsTableView.setItems(debtsOL);
        // Sort TableViews
        assetsBalanceTableColumn.setSortType(TableColumn.SortType.ASCENDING);
        assetsTableView.getSortOrder().add(assetsBalanceTableColumn);
        debtsBalanceTableColumn.setSortType(TableColumn.SortType.DESCENDING);
        debtsTableView.getSortOrder().add(debtsBalanceTableColumn);

		// set total account balances (label below the tables)
		double totalAssetsBalance = 0;
		double totalDebtsBalance = 0;
		for(Account account : currentCustomersAccounts.values()){
			if(account instanceof Credit || account instanceof Loan){
				totalDebtsBalance = totalDebtsBalance + account.getBalance();
			}else{
				totalAssetsBalance = totalAssetsBalance + account.getBalance();
			}
		}

        // display total balance & use string format to avoid scientific numbers if large balance
		startActualTotalBalanceLabel.setText(String.format("%.2f SEK", totalAssetsBalance));
		startActualTotalDebtLabel.setText(String.format("%.2f SEK", totalDebtsBalance));
	}

    //-----------------SIDE MENU NAVIGATION----------------
    public void goToStart(ActionEvent event) throws IOException { //sideMenuStartButton
        loader = new FXMLLoader(getClass().getResource("/com/piggybank/app/ui/customer_scenes/CustomerStart.fxml"));
        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goToAccountsOverview(ActionEvent event) throws IOException { //sideMenuAccountsOverviewButton
        loader = new FXMLLoader(getClass().getResource("/com/piggybank/app/ui/customer_scenes/CustomerAccountsOverview.fxml"));
        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goToTransferFunds(ActionEvent event) throws IOException { //sideMenuTransferFundsButton
        loader = new FXMLLoader(getClass().getResource("/com/piggybank/app/ui/customer_scenes/CustomerTransferFunds.fxml"));
        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goToLoans(ActionEvent event) throws IOException { //sideMenuLoansButton
        loader = new FXMLLoader(getClass().getResource("/com/piggybank/app/ui/customer_scenes/loan_scenes/CustomerLoansOverview.fxml"));
        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goToCredits(ActionEvent event) throws IOException { //sideMenuCreditsButton
        loader = new FXMLLoader(getClass().getResource("/com/piggybank/app/ui/customer_scenes/credit_scenes/CustomerCreditsOverview.fxml"));
        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goToFaq(ActionEvent event) throws IOException { //sideMenuFaqButton
        loader = new FXMLLoader(getClass().getResource("/com/piggybank/app/ui/customer_scenes/CustomerFaq.fxml"));
        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goToSupport(ActionEvent event) throws IOException { //sideMenuSupportButton
        loader = new FXMLLoader(getClass().getResource("/com/piggybank/app/ui/customer_scenes/CustomerSupport.fxml"));
        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

//-----------------HEADER MENU NAVIGATION----------------

    public void logout(ActionEvent event) throws IOException { //headerLogoutButton
        FileHandler.jsonSerializer(UIMain.jsonPath, bank);

        currentCustomer = null;
        loader = new FXMLLoader(getClass().getResource("/com/piggybank/app/ui/StartScene.fxml"));
        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
