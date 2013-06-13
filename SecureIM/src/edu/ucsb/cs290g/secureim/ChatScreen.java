package edu.ucsb.cs290g.secureim;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.ucsb.cs290g.secureim.models.Message;

public class ChatScreen extends Fragment implements MessageObserver {


    private TextView contactNameView;
    private EditText messageText;
    private Button sendButton;
    private TextView chatField;

    private String user;
    private String me;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectionHandler.getConnectionHandler(getActivity()).listen(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_chat, container, false);

        me = getArguments().getString("me");
        user = getArguments().getString("user");

        contactNameView = (TextView) view.findViewById(R.id.contact_name);
        messageText = (EditText) view.findViewById(R.id.sendMessage);
        sendButton = (Button) view.findViewById(R.id.sendButton);
        chatField = (TextView) view.findViewById(R.id.chatField);


        contactNameView.setText(user);

        sendButton.setOnClickListener(sendListener);

        return view;
    }

    private View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String toSend = messageText.getText().toString();
            if (!toSend.equals("")) {
                Message message = new Message(me, user, messageText.getText().toString());
                ConnectionHandler.getConnectionHandler(getActivity()).sendMessage(message);
                appendToChatLog(message);
                messageText.setText("");
            }
        }
    };

    private void appendToChatLog(Message message) {
        chatField.append(message.getSFrom() + ": " + message.getSMessage() + "\n");
    }

    @Override
    public void onNewMessage(Message message) {

        getActivity().runOnUiThread(new UpdateChat(message));
    }


    public class UpdateChat implements Runnable {
        private Message message;
        public UpdateChat(Message message) {
            this.message = message;
        }

        public void run() {
            appendToChatLog(message);
        }
    }

}
