package Service;

import Model.Account;
import Model.Message;
import DAO.AccountDAO;

import java.sql.*;
import java.util.List;

public class AccountService {
    private AccountDAO accountDAO;

    //constructor initializing accountDAO
    public AccountService(){
        accountDAO = new AccountDAO();
    }

    //service to process new User
    public Account createAccount(Account account) {
        //new user valid if username is not blank (no whitespace), password is at least 4 char's long, and Account with that username doesnt already exist
        if(account.getUsername().trim().length() > 0 && account.getPassword().length() >= 4 && getValidUsername(account)) {
            return accountDAO.createAccount(account);
        }
        //if invalid account return null
        return null;
    }

    //service to check if user exists in account db
    public boolean getValidUsername(Account account) {
        return accountDAO.getValidUsername(account.getUsername());
    }

    //service to process User logins
    public Account loginAccount(Account account) {
        //check if username and password exist in account db, if yes return Account including account_id
        return accountDAO.getValidAccount(account.getUsername(), account.getPassword());
    }
}
