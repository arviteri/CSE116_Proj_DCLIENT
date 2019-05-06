package client.scala.network.handlers

import java.lang.reflect.Type

import client.java.network.messages.MapMessage
import client.scala.game.objects.Player
import client.scala.network.GameSession
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.messaging.simp.stomp.{StompFrameHandler, StompHeaders}

import scala.collection.JavaConverters._

class GetAllPlayersHandler(gameSession: GameSession) extends StompFrameHandler {
    override def getPayloadType(stompHeaders: StompHeaders): Type = {
        classOf[MapMessage[String, client.java.game.objects.Player]]
    }

    override def handleFrame(stompHeaders: StompHeaders, o: Any): Unit = {
        var objectMapper = new ObjectMapper()
        var message = o.asInstanceOf[MapMessage[String, client.java.game.objects.Player]]
        var playersMap = message.getMap.asScala
        for ((id, p) <- playersMap) {
            var playerProps = objectMapper.convertValue(message.getMap.get(id), classOf[client.java.game.objects.Player])
            var newPlayer = new Player(playerProps)
            if (id != gameSession.getSessionId()) {
                gameSession.players.put(id, newPlayer)
                gameSession.newPlayers.add(newPlayer)
            }
        }
    }

    def handleMessage(o:Any): Unit = {
    }
}
