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

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    //constructor to initialize service objects
    public SocialMediaController(){
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }


    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::postRegisterHandler);
        app.post("/login", this::postLoginHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::patchMessageByIdHandler);
        app.get("accounts/{account_id}/messages", this::getAllMessagesByUserHandler);
        app.start(8080);
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void postRegisterHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        Account createdAccount = accountService.createAccount(account);

        if(createdAccount != null) context.json(mapper.writeValueAsString(createdAccount));
        else context.status(400);
    }

    private void postLoginHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        Account loginSuccessAccount = accountService.loginAccount(account);

        if(loginSuccessAccount != null) context.json(mapper.writeValueAsString(loginSuccessAccount)); 
        else context.status(401);
    }

    private void getAllMessagesHandler(Context context) {
        List<Message> messages = messageService.getAllMessages();
        
        //return messages with 200 status (even if messages list is empty)
        context.json(messages);
    }

    private void postMessageHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
        Message addedMessage = messageService.addMessage(message);

        if(addedMessage != null) context.json(mapper.writeValueAsString(addedMessage));
        else context.status(400);
    }

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

    private void deleteMessageByIdHandler(Context context) {
        //in future, should add NumberFormatException to check for invalid passed in ID
        //cast passed in parameter (String) to int
        int message_id = Integer.parseInt(context.pathParam("message_id"));
        Message message = messageService.deleteMessageById(message_id);

        //return deleted message with 200 status if message is not null
        if(message != null) context.json(message);
        //return empty response if message is null
        else context.result("");
    }

    private void patchMessageByIdHandler(Context context) throws JsonProcessingException{
        //in future, should add NumberFormatException to check for invalid passed in ID
        //cast passed in parameter (String) to int
        int message_id = Integer.parseInt(context.pathParam("message_id"));

        //this only works if we know Message obj sent as body, unclear if Message obj sent or if String message_text sent
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
        String message_text = message.getMessage_text();
        Message patchedMessage = messageService.patchMessageById(message_text, message_id);

        if(patchedMessage != null) context.json(patchedMessage);
        else context.status(400);
    }

    private void getAllMessagesByUserHandler(Context context) {
        context.json("sample text");
    }
}