package client.java.network.messages;

import client.java.game.objects.Projectile;
import client.java.network.messages.types.MessageType;

public class ProjectileMessage extends GameMessage {
    private Projectile projectile;

    public ProjectileMessage() {
        this(null, null);
    }

    public ProjectileMessage(Projectile projectile, MessageType type) {
        this.projectile = projectile;
        this.type = type;
    }

    public Projectile getProjectile() {
        return this.projectile;
    }
}
