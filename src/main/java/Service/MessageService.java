package Service;

import Model.Message;
import DAO.MessageDAO;

import java.util.List;

public class MessageService {
    private MessageDAO messageDAO;

    // constructor initializing messageDAO
    public MessageService(){
        messageDAO = new MessageDAO();
    }

    //service method to get all messages
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    //service method to add message if valid
    public Message addMessage(Message message) {
        //message valid if message_text not blank(not empty or just whitespace), 255 char's or less, and posted_by refers to real user
        if(message.getMessage_text().trim().length() > 0 && message.getMessage_text().length() <= 255 && getValidUserPostedBy(message)) {
            return messageDAO.insertMessage(message);
        }
        //if invalid message return null
        return null;
    }

    //service method to validate posted_by exists in message db
    public boolean getValidUserPostedBy(Message message) {
        return messageDAO.getValidUserPostedBy(message.getPosted_by());
    }

    //service method to get message by message_id in message db
    public Message getMessageById(int message_id) {
        return messageDAO.getMessageById(message_id);
    }

    //service method to delete message by message_id in message db
    public Message deleteMessageById(int message_id) {
        //first we need to get and store message using getMessageById service because
        //Delete statement doesnt return deleted message, it returns (int) number of rows deleted
        Message message = getMessageById(message_id);
        //delete message by message_id from db
        boolean deleteSuccess = messageDAO.deleteMessageById(message_id);
        
        //check if we were able to get a message, but unable to delete that message from db,
        //if true we should return null because deletion was unsuccessful and response body should be empty
        if(message != null && !deleteSuccess) return null;
        return message;
    }

    //service method to patch message by message_id in message db
    public Message patchMessageById(String message_text, int message_id) {
        //first we need to get and store message using getMessageById service because
        //Update statement doesnt return updated message, it returns (int) number of rows updated
        Message message = getMessageById(message_id);

        //check if message exists to be updated and if valid message_text provided
        if(message != null && message_text.trim().length() > 0 && message_text.length() <= 255) {
            boolean patchSuccess = messageDAO.patchMessageById(message_text, message_id);
            
            //return message if successful patch, null if unsuccessful
            return patchSuccess ? message : null;
        }

        //if invalid message_text or message_id doesn't exist in message table, return null
        return null;
    }

}
