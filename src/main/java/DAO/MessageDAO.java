package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.*;

public class MessageDAO {
    
    //method to get all messages from message db
    public List<Message> getAllMessages() {
        //connect to db using ConnectionUtil class
        Connection connection = ConnectionUtil.getConnection();
        
        //initialize messages list
        List<Message> messages = new ArrayList<>();

        //try/catch block to execute query and catch any SQLException
        try {
            //query to get all messages from message db
            String sql = "SELECT * FROM message;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            //iterate through ResultSet obj rs, initializing new messages per record, then adding obj to messages list
            while(rs.next()){
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), 
                    rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        //return messages list that now contains all records of messages in message db
        return messages;
    }
    
    //method to insert new message in message db
    public Message insertMessage(Message message) {
        //connect to db using ConnectionUtil class
        Connection connection = ConnectionUtil.getConnection();

        //try/catch block to insert message and catch any SQLException
        try {
            //insert message into message db using preparedStatement's paramaterization
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?);";
            //pk set to auto-increment, but we need generated pk returned
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //preparedStatement's set methods
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());
            preparedStatement.executeUpdate();

            ResultSet pkeyRS = preparedStatement.getGeneratedKeys();
            
            //check if record was inserted in db, if so extract generated pk and return message containing new pk
            if(pkeyRS.next()) {
                int generated_message_id = (int) pkeyRS.getLong(1);
                return new Message(generated_message_id, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            }
            
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        //return null if message wasn't added to message db
        return null;
    }

    //method to check if posted_by user exists in message db
    public boolean getValidUserPostedBy(int posted_by) {
        //connect to db using ConnectionUtil class
        Connection connection = ConnectionUtil.getConnection();

        //try/catch block to execute query and catch any SQLException
        try {
            //query to get all posted_by from message db where posted_by is passed in as parameter
            String sql = "SELECT posted_by FROM message WHERE posted_by=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, posted_by);

            ResultSet rs = preparedStatement.executeQuery();

            //if any record is returned at all, we know posted_by user exists and we can return true
            if(rs.next()) return true;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        //return false if no records found with posted_by int
        return false;
    }

    //method to get message by message_id in message db
    public Message getMessageById(int message_id) {
        //connect to db using ConnectionUtil class
        Connection connection = ConnectionUtil.getConnection();

        //try/catch block to execute query and catch any SQLException
        try {
            //query to get message from message db where message_id is passed in as parameter
            String sql = "SELECT * FROM message WHERE message_id=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, message_id);

            ResultSet rs = preparedStatement.executeQuery();

            //record returned will be message with passed in message_id, return complete Message
            if(rs.next()){
                return new Message(rs.getInt("message_id"), rs.getInt("posted_by"), 
                    rs.getString("message_text"), rs.getLong("time_posted_epoch"));
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        //return null if no message found with corresponding message_id
        return null;
    }

    //method to delete message by message_id in message db
    public boolean deleteMessageById(int message_id) {
        //connect to db using ConnectionUtil class
        Connection connection = ConnectionUtil.getConnection();

        //try/catch block to execute delete and catch any SQLException
        try {
            //delete message from message db with message_id that equals value passed in as parameter
            String sql = "DELETE FROM message WHERE message_id=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, message_id);

            return preparedStatement.executeUpdate() > 0;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        //return false if no message found with corresponding message_id
        return false;
    }

    //method to update message by message_id in message db
    public boolean patchMessageById(String message_text, int message_id) {
        //connect to db using ConnectionUtil class
        Connection connection = ConnectionUtil.getConnection();

        //try/catch block to execute query and catch any SQLException
        try {
            //update message from message db with message_id that equals value passed in as parameter
            String sql = "UPDATE message SET message_text=? WHERE message_id=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, message_text);
            preparedStatement.setInt(2, message_id);

            return preparedStatement.executeUpdate() > 0;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        //return false if message failed to patch
        return false;
    }

    //method to get all messages by account_id from message db
    public List<Message> getAllMessagesByAccountId(int account_id) {
        //connect to db using ConnectionUtil class
        Connection connection = ConnectionUtil.getConnection();
        
        //initialize messages list
        List<Message> messages = new ArrayList<>();

        //try/catch block to execute query and catch any SQLException
        try {
            //query to get all messages from message db by account_id using inner join
            String sql = "SELECT * FROM message JOIN account ON message.posted_by = account.account_id WHERE account.account_id=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);

            ResultSet rs = preparedStatement.executeQuery();
            //iterate through ResultSet obj rs, initializing new messages per record, then adding obj to messages list
            while(rs.next()){
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), 
                    rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        //return messages list that now contains all records of messages in message db
        return messages;
    }
}
