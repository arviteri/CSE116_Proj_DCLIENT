package client.java.network.messages;

import client.java.game.objects.Player;
import client.java.network.messages.types.MessageType;

public class PlayerMessage extends GameMessage {
    private Player player;

    public PlayerMessage() {
        this(null, null);
    }

    public PlayerMessage(Player player, MessageType type) {
        this.player = player;
        this.type = type;
    }

    public Player getPlayer() {
        return this.player;
    }
}
