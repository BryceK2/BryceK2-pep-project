package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    //constructor to initialize service objects
    public SocialMediaController(){
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    //starts Javalin API and sets up endpoint routes
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        //register new account
        app.post("/register", this::postRegisterHandler);

        //login with existing account
        app.post("/login", this::postLoginHandler);

        //get all messages
        app.get("/messages", this::getAllMessagesHandler);

        //post new message
        app.post("/messages", this::postMessageHandler);

        //get message by message_id
        app.get("/messages/{message_id}", this::getMessageByIdHandler);

        //delete message by message_id
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);

        //update message by message_id
        app.patch("/messages/{message_id}", this::patchMessageByIdHandler);
        
        //get message(s) that correspond with account_id
        app.get("accounts/{account_id}/messages", this::getAllMessagesByUserHandler);

        return app;
    }

    //register new account handler
    private void postRegisterHandler(Context context) throws JsonProcessingException {
        //get account object from request body
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        
        //call createAccount() in AccountService sending the Account passed by request body
        Account createdAccount = accountService.createAccount(account);

        //if account created, return JSON string of account object, else return 400 as response
        if(createdAccount != null) context.json(createdAccount);
        else context.status(400);
    }

    //login with existing account handler
    private void postLoginHandler(Context context) throws JsonProcessingException {
        //get account object from request body
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);

        //call loginAccount() in AccountService sending the Account passed by request body
        Account loginSuccessAccount = accountService.loginAccount(account);

        //if account credentials vaild, return JSON string of account object, else return 401 as response
        if(loginSuccessAccount != null) context.json(loginSuccessAccount); 
        else context.status(401);
    }

    //get all messages handler
    private void getAllMessagesHandler(Context context) {
        //initialize messages list
        List<Message> messages = messageService.getAllMessages();
        
        //return messages with 200 status (even if messages list is empty)
        context.json(messages);
    }

    //add message handler
    private void postMessageHandler(Context context) throws JsonProcessingException {
        //get message object from request body
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);

        //call addMessage() in MessageService sending the Message passed by request body
        Message addedMessage = messageService.addMessage(message);

        //if message added, return JSON string of added message object, else return 400 as response
        if(addedMessage != null) context.json(addedMessage);
        else context.status(400);
    }

    //get message by message_id handler
    private void getMessageByIdHandler(Context context) {
        //in future, should add NumberFormatException to check for invalid passed in ID
        //cast passed in parameter (String) to int
        int message_id = Integer.parseInt(context.pathParam("message_id"));
        Message message = messageService.getMessageById(message_id);

        //return message with 200 status if message is not null
        if(message != null) context.json(message);
        //return empty response if message is null
        else context.result("");
    }

    //delete message by message_id handler
    private void deleteMessageByIdHandler(Context context) {
        //in future, should add NumberFormatException to check for invalid passed in ID
        //cast passed in parameter (String) to int
        int message_id = Integer.parseInt(context.pathParam("message_id"));
        Message message = messageService.deleteMessageById(message_id);

        //return deleted message with 200 status as response if message is not null
        if(message != null) context.json(message);
        //return empty response if message is null
        else context.result("");
    }

    //update message by message_id handler
    private void patchMessageByIdHandler(Context context) throws JsonProcessingException{
        //in future, should add NumberFormatException to check for invalid passed in ID
        //cast passed in parameter (String) to int
        int message_id = Integer.parseInt(context.pathParam("message_id"));

        //this only works if we know Message obj sent as body, unclear if Message obj sent or if String message_text sent
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
        String message_text = message.getMessage_text();
        Message patchedMessage = messageService.patchMessageById(message_text, message_id);

        //return updated message with 200 status as response if message is not null
        if(patchedMessage != null) context.json(patchedMessage);
        //return 400 as response
        else context.status(400);
    }

    //get all messages from specific user 
    private void getAllMessagesByUserHandler(Context context) {
        //in future, should add NumberFormatException to check for invalid passed in ID
        //cast passed in parameter (String) to int
        int account_id = Integer.parseInt(context.pathParam("account_id"));
        List<Message> messages = messageService.getAllMessagesByAccountId(account_id);
        
        //return messages with 200 status as response (even if messages list is empty)
        context.json(messages);
    }
}