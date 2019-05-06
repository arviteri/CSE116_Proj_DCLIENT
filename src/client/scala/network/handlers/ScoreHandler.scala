package client.scala.network.handlers

import java.lang.reflect.Type

import client.scala.network.GameSession
import org.springframework.messaging.simp.stomp.{StompFrameHandler, StompHeaders}

class ScoreHandler(gameSession:GameSession) extends StompFrameHandler {
    override def getPayloadType(stompHeaders: StompHeaders): Type = {
        classOf[Double]
    }

    override def handleFrame(stompHeaders: StompHeaders, o: Any): Unit = {
        gameSession.updateScore(o.asInstanceOf[Double])
    }
}
