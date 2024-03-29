package com.piggybank.app.backend;

import com.piggybank.app.backend.customers.*;
import com.piggybank.app.backend.customers.money_operations.Credit;
import com.piggybank.app.backend.customers.money_operations.Loan;
import com.piggybank.app.backend.employees.*;
import com.piggybank.app.backend.exceptions.*;
import com.piggybank.app.backend.utils.*;

import java.util.HashMap;
import java.util.Calendar;
import java.time.LocalDate;

public class Bank {

	// Constants
	private final int MAX_CUSTOMER_ID_LENGTH = 4;
	private final int SSN_LENGTH = 10;

	// attributes:
    private ContactCard contactInfo;
    private HashMap<String, Customer> customers;
    private HashMap<String, Employee> employees;

    // Bare constructor used by Jackson-Databind for Json deserializing
    public Bank() {}

    public Bank(ContactCard contactInfo) {
        this.contactInfo = contactInfo;
        customers = new HashMap<>();
        employees = new HashMap<>();
    }

    //-----------------------GETTERS-----------------------
	public ContactCard getContactInfo() {
        return this.contactInfo;
    }

	public HashMap<String, Customer> getCustomers(){
        return this.customers;
    }

	public HashMap<String, Employee> getEmployees() {
        return this.employees;
    }

	//-----------------------SETTERS-----------------------
	// The setter methods are used by jackson library to serialize json files

    public void setCustomers(HashMap<String, Customer> customers) {
        this.customers = customers;
    }

    public void setEmployees(HashMap<String, Employee> employees) {
        this.employees = employees;
    }

    public void setContactInfo(ContactCard contactInfo) {
        this.contactInfo = contactInfo;
    }

    //-----------------------GETTERS FOR SPECIFIC OBJECTS-----------------------
    public Customer getCustomer(String userId) {
        return this.customers.get(userId);
    }

	public Employee getEmployee(String userId){
        return employees.get(userId);
    }

    public Customer getCustomerByIdOrSsn(String inputString) throws Exception {
        /* Checks for 10 characters, if it is then SSN,
        otherwise if it is 4 characters then it is a userId
        */

        if(inputString.length() == SSN_LENGTH) { // checks if input is SSN
            for(Customer customer : customers.values()) { // loops over all customers
                if(customer instanceof CustomerPrivate privateCustomer) {
                    if(privateCustomer.getSsn().equals(inputString)) {
                        return customer;
                    }
                }
            }
        } else if (inputString.length() == MAX_CUSTOMER_ID_LENGTH) { // checks if input is customerId
            if (!customers.containsKey(inputString)) {
                throw new UserNotFoundException("Customer not found by ID or SSN.");
            }
            return customers.get(inputString);
        }
        throw new UserNotFoundException("Customer not found by ID or SSN.");
    }

    public Account getAccountById(String accountId) throws Exception {
        for (Customer customer : customers.values()) { // loops over all customers
            boolean accountExists = customer.checkIfAccountExists(accountId);
            if (accountExists) {
                return customer.getAccount(accountId);
            }
        }
        throw new AccountNotFoundException("Account ID " + accountId + " was not found.");
    }

	//-----------------------CREATOR METHODS-----------------------

    // creates a new private customer and add it to customers hashmap:
    public String createCustomerPrivate(String ssn, String firstName, String lastName, String password, ContactCard contactCard) throws Exception {
        if (ssn.length() != SSN_LENGTH) {
            throw new Exception();
        }

        // finds the highest customerId in the HashMap customers
        String customerHighestId = findHighestCustomerId();

        // generates a new ID for a customer, then updates customerIdCounter
        String userId = IdGenerator.generateCustomerID(customerHighestId);

        CustomerPrivate newCustomer = new CustomerPrivate(ssn, firstName, lastName, userId, password, contactCard);
        this.customers.put(userId, newCustomer);
        return userId;
    }

    // creates a new corporate customer and add it to customers hashmap:
    public String createCustomerCorporate(String orgNumber, String companyName, String password, ContactCard contactCard) throws Exception {
        // finds the highest customerId in the HashMap customers
        String customerHighestId = findHighestCustomerId();

        // Generates a new ID for a corporate customer, then updates customerIdCounter
        String userId = IdGenerator.generateCustomerID(customerHighestId);

        CustomerCorporate newCustomer = new CustomerCorporate(orgNumber, companyName, userId, password, contactCard);
        this.customers.put(userId, newCustomer);
        return userId;
    }

    // creates a new employee and add it to employees hashmap:
    public void createEmployee(String firstName, String lastName, String password, ContactCard contactCard) throws Exception {
        // finds the highest employeeId in the HashMap employees
        String employeeHighestId = findHighestEmployeeId();

        // Generates a new ID for an employee
        String userId = IdGenerator.generateEmployeeID(employeeHighestId);

        Employee newEmployee = new Employee(firstName, lastName, userId, password, contactCard);
        this.employees.put(userId, newEmployee);
    }

    // creates a new account for customer:
    public void createAccount(String userId, String accountName) throws Exception {
        if(accountName.isBlank()) {
            throw new Exception("ERROR: INCORRECTLY ENTERED CRITERIA FOR ACCOUNT CREATION");
        } else if (!accountName.isBlank()) {
            Customer customer = customers.get(userId);

            // finds the highest accountId in the HashMap customers
            String accountHighestId = findHighestAccountId();

            //Generates accountId
            String accountId = IdGenerator.generateAccountId(accountHighestId);

            Account newAccount = new Account(accountId, accountName);
            customer.addAccount(newAccount);
        }
    }

	// creates a new loan account for customer:
    public Loan createLoanAccount(String userId, String accountName, double loanAmount) throws Exception {
        if(userId.isBlank() || accountName.isBlank() || loanAmount == 0) {
            throw new Exception("ERROR: INCORRECTLY ENTERED CRITERIA FOR ACCOUNT CREATION");
        }
        Customer customer = customers.get(userId);

        // finds the highest accountId in the HashMap customers
        String accountHighestId = findHighestAccountId();

        //Generates accountId
        String accountId = IdGenerator.generateAccountId(accountHighestId);

        Loan newLoan = new Loan(accountId, accountName, loanAmount);
        customer.addAccount(newLoan);

        return newLoan;
    }

	// creates a new credit account for customer:
	public Credit createCredit(String userId, String accountName, Calendar initialCreditDate, double amount) throws Exception{
        if(userId.isBlank() || accountName.isBlank() || amount == 0) {
            throw new Exception("ERROR: INCORRECTLY ENTERED CRITERIA FOR ACCOUNT CREATION");
        }
        Customer customer = customers.get(userId);

        // finds the highest accountId in the HashMap customers
        String accountHighestId = findHighestAccountId();

        //Generates accountId
        String accountId = IdGenerator.generateAccountId(accountHighestId);

        Credit newAccount = new Credit(accountId, accountName, initialCreditDate, amount);
        customer.addAccount(newAccount);
        return newAccount;
    }

    //-----------------------REMOVAL METHODS-----------------------

    // remove customer from bank:
    public void removeCustomer(String customerId) {
        this.customers.remove(customerId);
    }

    //remove employee from bank:
    public void removeEmployee(String employeeId) {
        this.employees.remove(employeeId);
    }

    // removes account from customer
    public void removeAccount(String customerId, String accountToRemove) {
        customers.get(customerId).removeAccount(accountToRemove);
    }

    //-----------------------VARIOUS-----------------------

    // loops through HashMap customers for their userIds and returns which is highest
    public String findHighestCustomerId() {
        int highestIdCount = 0;
        String highestCustomerId = "C000";
        for(Customer customer : customers.values()) {
            int tempIdCount = Integer.parseInt(customer.getUserId().substring(1));
            if(tempIdCount > highestIdCount) {
                highestIdCount = tempIdCount;
                highestCustomerId = customer.getUserId();
            }
        }
        return highestCustomerId;
    }

    // loops through HashMap employees for their userIds and returns which is highest
    public String findHighestEmployeeId() {
        int highestIdCount = 0;
        String highestEmployeeId = "E000";
        for(Employee employee : employees.values()) {
            int tempIdCount = Integer.parseInt(employee.getUserId().substring(1));
            if(tempIdCount > highestIdCount) {
                highestIdCount = tempIdCount;
                highestEmployeeId = employee.getUserId();
            }
        }
        return highestEmployeeId;
    }

    // loops through HashMap employees for their HashMap of accounts, then gets the highest accountId of them all
    public String findHighestAccountId() throws Exception {
        int highestIdCount = 0;
        String highestAccountId = "A00000";
        for(Customer customer : customers.values()) {
            HashMap<String, Account> customerAccounts = customer.getAccounts();
            for(Account account : customerAccounts.values()) {
                int tempIdCount = Integer.parseInt(account.getAccountId().substring(1));
                if(tempIdCount > highestIdCount) {
                    highestIdCount = tempIdCount;
                    highestAccountId = account.getAccountId();
                }
            }
        }
        return highestAccountId;
    }

    // transfer money between accounts; the actual account and a target account
    public void transfer(String senderAccountId, String targetAccountId, double amount, String message, LocalDate date) throws Exception {
        Account senderAccount = getAccountById(senderAccountId);
        Account targetAccount = getAccountById(targetAccountId);

        senderAccount.withdraw(targetAccountId, amount, message, date);
        targetAccount.deposit(senderAccountId, amount, message, date);
    }

    public void transferFromLoanAccount(String senderAccountId, String targetAccountId, double amount, String message, LocalDate date) throws Exception {
        Loan senderAccount = (Loan) getAccountById(senderAccountId);
        Account targetAccount = getAccountById(targetAccountId);

        senderAccount.withdraw(targetAccountId, amount, message, date);
        targetAccount.deposit(senderAccountId, amount, message, date);
    }

    public void transferFromCreditAccount(String senderAccountId, String targetAccountId, double amount, String message, LocalDate date) throws Exception {
        Credit senderAccount = (Credit) getAccountById(senderAccountId);
        Account targetAccount = getAccountById(targetAccountId);

        senderAccount.withdraw(targetAccountId, amount, message, date);
        targetAccount.deposit(senderAccountId, amount, message, date);
    }

    //verify customer login information:
    public boolean verifyCustomer(String userId, String password) throws PasswordException {
        boolean correctUserId = false;
        boolean correctPassword = false;
        if (!customers.containsKey(userId)) {
            throw new PasswordException("Invalid user ID or password. Please try again.");
        } else {
            correctUserId = true;
            Customer customer = customers.get(userId);
            if (!customer.getPassword().equals(password)) {
                throw new PasswordException("Invalid user ID or password. Please try again.");
            } else {
                correctPassword = true;
            }
            return correctUserId && correctPassword;
        }
    }

    //verify employee login information:
    public boolean verifyEmployee(String userId, String password) throws PasswordException {
        boolean correctUserId = false;
        boolean correctPassword = false;
        if (!employees.containsKey(userId)) {
            throw new PasswordException("Invalid user ID or password. Please try again.");
        } else {
            correctUserId = true;
            Employee employee = employees.get(userId);
            if (!employee.getPassword().equals(password)) {
                throw new PasswordException("Invalid user ID or password. Please try again.");
            } else {
                correctPassword = true;
            }
        }
        return correctUserId && correctPassword;
    }

    @Override
    public String toString() {
        return String.format("Bank{ contactInfo: %s, customers: %s, employees: %s", contactInfo, customers, employees);
    }
}
