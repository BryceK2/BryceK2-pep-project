package DAO;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.*;

public class AccountDAO {

    public Account createAccount(Account account) {
        //connect to db using ConnectionUtil class
        Connection connection = ConnectionUtil.getConnection();

        //try/catch block to insert account and catch any SQLException
        try {
            //insert account into account db using preparedStatement's paramaterization
            String sql = "INSERT INTO account (username, password) VALUES (?, ?);";
            //pk set to auto-increment, but we need generated pk returned
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //preparedStatement's set methods
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            preparedStatement.executeUpdate();

            ResultSet pkeyRS = preparedStatement.getGeneratedKeys();
            
            //check if record was inserted in db, if so extract generated pk and return account containing new pk
            if(pkeyRS.next()) {
                int generated_account_id = (int) pkeyRS.getLong(1);
                return new Account(generated_account_id, account.getUsername(), account.getPassword());
            }
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        //return null if account wasn't added to account db
        return null;
    }

    //method to check if username exists in account db
    public boolean getValidUsername(String username) {
        //connect to db using ConnectionUtil class
        Connection connection = ConnectionUtil.getConnection();

        //try/catch block to execute query and catch any SQLException
        try {
            //query to get all usernames from account db where username is passed in as parameter
            String sql = "SELECT username FROM account WHERE username=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();

            //if no record is returned, we know username doesn't exist and we can return true
            if(!rs.next()) return true;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        //return false if username exists
        return false;
    }


    //method to check if username and password exist in account db and to return full account details
    public Account getValidAccount(String username, String password) {
        //connect to db using ConnectionUtil class
        Connection connection = ConnectionUtil.getConnection();

        //try/catch block to execute query and catch any SQLException
        try {
            //query to check if username and password found in account db
            String sql = "SELECT username FROM account WHERE username=? AND password=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet rs = preparedStatement.executeQuery();

            //if record is returned, we know credentials valid and we can return the account details
            if(rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password")); 
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        //return null if no user with provided credentials exist
        return null;
    }

    
}
