package edu.ucsb.cs290g.secureim.interfaces;

import edu.ucsb.cs290g.secureim.models.Message;

/**
 * Created by arnbju on 6/10/13.
 */
public interface MessageObserver {

    public void onNewMessage(Message message);
}
