package client.scala.network.handlers

import java.lang.reflect.Type

import client.java.network.messages.types.MessageType
import client.scala.game.objects.{Projectile, Zombie}
import client.scala.network.GameSession
import javafx.application.Platform
import org.springframework.messaging.simp.stomp.{StompFrameHandler, StompHeaders}

class ZombieMessageHandler(gameSession: GameSession) extends StompFrameHandler {
    override def getPayloadType(stompHeaders: StompHeaders): Type = {
        classOf[client.java.network.messages.ZombieMessage]
    }

    override def handleFrame(stompHeaders: StompHeaders, o: Any): Unit = {
        new Thread(new Runnable {
            override def run(): Unit = {
                handleMessage(o)
            }
        }).start()
    }

    /**
      * Handles incoming message from server.
      * Seperated from handle frame in order to run each response on new thread.
      * @param o
      */
    def handleMessage(o: Any): Unit = {
        var message = o.asInstanceOf[client.java.network.messages.ZombieMessage]
        var messageType:MessageType = message.getType()
        var id = message.getZombie().id
        var zombieProps = message.getZombie()

        if (messageType == MessageType.ADD) {
            var zombie = new Zombie(zombieProps)
            gameSession.zombies.put(id, zombie)
            gameSession.newZombies.add(zombie)
        } else if (messageType == MessageType.REMOVE) {
            if (gameSession.zombies.get(id) != null) {
                var zombie = gameSession.zombies.get(id)
                gameSession.zombiesToRemove.add(zombie)
                gameSession.zombies.remove(id)
            }
        } else if (messageType == MessageType.UPDATE) {
            if (gameSession.zombies.get(id) != null) {
                var myPlayer = gameSession.getMyPlayer()
                var zombie = gameSession.zombies.get(id)
                var updatedLocation = zombieProps.location
                Platform.runLater(new Runnable {
                    override def run(): Unit = {
                        zombie.updateHealth(zombieProps.health)
                        zombie.setLocation(updatedLocation)
                    }
                })

                /* Check if zombie collided with client's player */
                if (gameSession.connectedToGame.get()) {
                    if (updatedLocation.x >= myPlayer.props.location.x - client.java.game.objects.Player.WIDTH && updatedLocation.x <= myPlayer.props.location.x + client.java.game.objects.Player.WIDTH) {
                        if (updatedLocation.y >= myPlayer.props.location.y - client.java.game.objects.Player.HEIGHT && updatedLocation.y <= myPlayer.props.location.y + client.java.game.objects.Player.HEIGHT) {
                            var currentHealth = myPlayer.props.health
                            if ((System.currentTimeMillis() - gameSession.lastStartTime)/1000 > 5) {
                                gameSession.updateHealth(currentHealth-2)
                                if (myPlayer.props.health <= 0) {
                                    gameSession.leaveGame()
                                }
                            }
                        }
                    }
                }
            } else {
                var zombie = new Zombie(zombieProps)
                gameSession.zombies.put(id, zombie)
                gameSession.newZombies.add(zombie)
            }
        }
    }
}
