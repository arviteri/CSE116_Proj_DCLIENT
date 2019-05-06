package client.java.network.messages.types;

public enum MessageType {
    ADD(1), REMOVE(2), UPDATE(3);

    private int value;

    private MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

