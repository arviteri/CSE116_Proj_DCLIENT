package client.scala.game.controllers

import client.java.game.objects.PhysicsVector
import client.java.game.types.Direction
import client.scala.game.Game
import client.scala.game.objects.{Player, Projectile}
import client.scala.network.GameSession
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent

object PlayerController {
    final val MOVE_INTERVAL = 5
}

class PlayerController(gameSession: GameSession) extends EventHandler[KeyEvent] {

    var player = gameSession.getMyPlayer()

    def setPlayer(player: Player): Unit = {
        this.player = player
    }

    override def handle(event: KeyEvent): Unit = {
        if (gameSession.getMyPlayer() != null) {
            if (event.getCode.getName == "Down") {
                player.props.lastMove = Direction.DOWN
                var location = player.props.location
                var newY = location.y + PlayerController.MOVE_INTERVAL
                if (newY <= Game.HEIGHT - Player.HEIGHT) {
                    location.y = newY
                }
                gameSession.updateMyPlayerLocation(location)
            }
            if (event.getCode.getName == "Up") {
                player.props.lastMove = Direction.UP
                var location = player.props.location
                var newY = location.y - PlayerController.MOVE_INTERVAL
                if (newY >= 0) {
                    location.y = newY
                }
                gameSession.updateMyPlayerLocation(location)
            }
            if (event.getCode.getName == "Right") {
                player.props.lastMove = Direction.RIGHT
                var location = player.props.location
                var newX = location.x + PlayerController.MOVE_INTERVAL
                if (newX <= Game.WIDTH - Player.WIDTH) {
                    location.x = newX
                }
                gameSession.updateMyPlayerLocation(location)
            }
            if (event.getCode.getName == "Left") {
                player.props.lastMove = Direction.LEFT
                var location = player.props.location
                var newX = location.x - PlayerController.MOVE_INTERVAL
                if (newX >= 0) {
                    location.x = newX
                }
                gameSession.updateMyPlayerLocation(location)
            }
            if (event.getCode.getName == "Space") {
                var x = player.props.location.x + Player.WIDTH / 2
                var y = player.props.location.y + Player.HEIGHT / 2
                var projectileLocation = new PhysicsVector(x, y, 0)
                var projectileProps = new client.java.game.objects.Projectile("", gameSession.getMyPlayer().props.id, projectileLocation); // THe server will set the id.

                if (player.props.lastMove == Direction.DOWN) {
                    projectileProps.setVelocity(0, 10, 0)
                } else if (player.props.lastMove == Direction.UP) {
                    projectileProps.setVelocity(0, -10, 0)
                } else if (player.props.lastMove == Direction.LEFT) {
                    projectileProps.setVelocity(-10, 0, 0)
                } else {
                    projectileProps.setVelocity(10, 0, 0)
                }

                var projectile = new Projectile(projectileProps)
                gameSession.shootProjectile(projectile)
            }
        }
    }
}
