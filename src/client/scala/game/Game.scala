package client.scala.game

import client.scala.network.GameSession
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.scene.Node

object Game {
    var WIDTH = 1024
    var HEIGHT = 768
}

class Game {
    val session = new GameSession();

    def start(): Unit = {
        session.joinGame()
    }

    def updateSprites(sprites: ObservableList[Node]): Unit = {
        addPlayerSprites(sprites)
        removePlayerSprites(sprites)
        addProjectilesSprites(sprites)
        removeProjectileSprites(sprites)
        addZombieSprites(sprites)
        removeZombieSprites(sprites)
    }

    def addPlayerSprites(sprites: ObservableList[Node]): Unit = {
        if (session.newPlayers.size > 0) {
            sprites.add(0, session.newPlayers.remove().sprite)
        }

    }

    def removePlayerSprites(sprites: ObservableList[Node]): Unit = {
        if (session.disconnectedPlayers.size > 0) {
            sprites.remove(session.disconnectedPlayers.remove().sprite)
        }
    }

    def addProjectilesSprites(sprites: ObservableList[Node]): Unit = {
        if (session.newProjectiles.size > 0) {
            sprites.add(0, session.newProjectiles.remove().sprite) // Add to zero index so they're behind everything
        }
    }

    def removeProjectileSprites(sprites: ObservableList[Node]): Unit = {
        if (session.projectilesToRemove.size > 0) {
            sprites.remove(session.projectilesToRemove.remove().sprite)
        }
    }

    def addZombieSprites(sprites: ObservableList[Node]): Unit = {
        if (!session.newZombies.isEmpty) {
            var zombie = session.newZombies.remove()
            /* Checking queue size doesn't prevent dequeue from occurring when queue is empty because of threads clashing.
            * This problem happens when the client (this) player is the first to join after the game server has started.
            * */
            if (zombie != null) {
                sprites.add(0, zombie.sprite)
            }
        }
    }

    def removeZombieSprites(sprites: ObservableList[Node]): Unit = {
        if (session.zombiesToRemove.size > 0) {
            sprites.remove(session.zombiesToRemove.remove().sprite)
        }
    }

    def updatePlayerLocations(): Unit = {
        if (session.updatedPlayers.size > 0) {
            var updatedPlayer = session.updatedPlayers.remove()
            updatedPlayer.sprite.relocate(updatedPlayer.props.location.x, updatedPlayer.props.location.y)
        }
    }

    def updateZombieLocations(): Unit = {
        if (session.updatedZombeis.size > 0) {
            var updatedZombie = session.updatedZombeis.remove()
            updatedZombie.sprite.relocate(updatedZombie.props.location.x, updatedZombie.props.location.y)
        }
    }

    def updateProjectileLocations(): Unit = {
        if (session.updatedProjectiles.size > 0) {
            var updatedProjectile = session.updatedProjectiles.remove()
            updatedProjectile.sprite.relocate(updatedProjectile.props.location.x, updatedProjectile.props.location.y)
        }
    }
}
