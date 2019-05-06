package client.java.network.messages;

import client.java.network.messages.types.MessageType;

public class GameMessage {
    protected MessageType type;
    public GameMessage() { }

    public MessageType getType() {
        return this.type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
