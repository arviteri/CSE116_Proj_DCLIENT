package client.scala.network.handlers

import java.lang.reflect.Type

import client.java.network.messages.types.MessageType
import client.scala.game.objects.{Player, Projectile}
import client.scala.network.GameSession
import com.fasterxml.jackson.databind.ObjectMapper
import javafx.application.Platform
import org.springframework.messaging.simp.stomp.{StompFrameHandler, StompHeaders}

import scala.collection.JavaConverters._


class ProjectileMessageHandler(gameSession: GameSession) extends StompFrameHandler {
    override def getPayloadType(stompHeaders: StompHeaders): Type = {
        classOf[client.java.network.messages.ProjectileMessage]
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
        var message = o.asInstanceOf[client.java.network.messages.ProjectileMessage]
        var messageType:MessageType = message.getType()
        var id = message.getProjectile().id
        var projectileProps = message.getProjectile()

        if (messageType == MessageType.ADD) {
            var projectile = new Projectile(projectileProps)
            gameSession.projectiles.put(id, projectile)
            gameSession.newProjectiles.add(projectile)
        } else if (messageType == MessageType.REMOVE) {
            if (gameSession.projectiles.get(id) != null) {
                var projectile = gameSession.projectiles.get(id)
                gameSession.projectilesToRemove.add(projectile)
                gameSession.projectiles.remove(id)
            }
        } else if (messageType == MessageType.UPDATE) {
            if (gameSession.projectiles.get(id) != null) {
                var projectile = gameSession.projectiles.get(id)
                Platform.runLater(new Runnable {
                    override def run(): Unit = {
                        projectile.setLocation(projectileProps.location)
                    }
                })
            }
        }
    }
}
