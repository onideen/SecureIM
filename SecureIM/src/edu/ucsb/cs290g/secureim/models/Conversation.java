package edu.ucsb.cs290g.secureim.models;

import java.security.PublicKey;
import java.util.List;

/**
 * Created by arnbju on 6/9/13.
 */
public class Conversation {

    private List<Message> messages;
    private String user1;
    private String user2;
    
    
    private PublicKey othersPublickey;

    private Conversation(String user1, String user2) {
        this.user1 = user1;
        this.user2 = user2;
    }


    public static Conversation startConversation(String user1, String user2, List<Conversation> conversations) {

        Conversation existingConversation = findConversation(user1, user2, conversations);
        if (existingConversation == null) {

            Conversation conversation = new Conversation(user1, user2);
            conversations.add(conversation);
            return conversation;
        }
        else {
            return existingConversation;
        }


    }

    private static Conversation findConversation(String user1, String user2, List<Conversation> conversations) {
        for ( Conversation conversation : conversations ) {

            if ((user1.equals(conversation.user1) && user2.equals(conversation.user2)) || (user1.equals(conversation.user2) && user2.equals(conversation.user1)) ) {
                return conversation;
            }
        }

        return null;
    }

}
