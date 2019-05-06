package client.scala.network.handlers

import java.lang.reflect.Type

import client.java.network.messages.types.MessageType
import client.scala.game.objects.Player
import client.scala.network.GameSession
import javafx.application.Platform
import org.springframework.messaging.simp.stomp.{StompFrameHandler, StompHeaders}

class PlayerMessageHandler(gameSession: GameSession) extends StompFrameHandler {
    override def getPayloadType(stompHeaders: StompHeaders): Type = {
        classOf[client.java.network.messages.PlayerMessage]
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
        var message = o.asInstanceOf[client.java.network.messages.PlayerMessage]
        var messageType:MessageType = message.getType()
        if (message.getPlayer() == null) {
            return;
        }
        var playerId = message.getPlayer().id
        /* Only update if when the player message doesn't refer to this client's player
        * Local updates for this client's player are done through the GameSession object.
        */
        if (playerId != gameSession.getSessionId() && (gameSession.connectedToGame.get() || gameSession.attemptingToJoin.get())) {
            if (messageType == MessageType.ADD) {
                var player = new Player(message.getPlayer())
                gameSession.players.put(playerId ,player)
                gameSession.newPlayers.add(player)
            } else if (messageType == MessageType.REMOVE) {
                if (gameSession.players.get(playerId) != null) {
                    var player = gameSession.players.get(playerId)
                    gameSession.disconnectedPlayers.add(player)
                    gameSession.players.remove(playerId)
                }
            } else if (messageType == MessageType.UPDATE) {
                if (gameSession.players.get(playerId) != null) {
                    var player = gameSession.players.get(playerId)
                    Platform.runLater(new Runnable {
                        override def run(): Unit = {
                            player.setLocation(message.getPlayer().location) // This updates the sprite location as well.
                            player.updateHealth(message.getPlayer().health)
                        }
                    })
                }
            }
        }
    }
}
