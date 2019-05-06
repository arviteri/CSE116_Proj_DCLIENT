package client.scala.network.handlers

import java.lang.reflect.Type

import client.scala.game.objects.Player
import client.scala.network.GameSession
import org.springframework.messaging.simp.stomp.{StompFrameHandler, StompHeaders}

class JoinGameHandler(gameSession: GameSession) extends StompFrameHandler {
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

    def handleMessage(o:Any): Unit = {
        var message = o.asInstanceOf[client.java.network.messages.PlayerMessage]
        var sessionId = message.getPlayer().id
        var myPlayer:Player = new Player(message.getPlayer())

        gameSession.setSessionId(sessionId)
        gameSession.setMyPlayer(myPlayer)
        gameSession.attemptingToJoin.compareAndSet(true, false)
        gameSession.connectedToGame.compareAndSet(false, true)
    }
}
