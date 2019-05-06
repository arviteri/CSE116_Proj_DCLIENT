package client.scala.network

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

import client.java.game.objects.PhysicsVector
import client.java.network.connection.{Connection, Session}
import client.scala.game.objects.{Player, Projectile, Zombie}
import client.scala.network.handlers._

import scala.collection.mutable

class GameSession {
    var lastStartTime:Long = 0
    var playerName = new java.lang.String()
    var hostName = new java.lang.String()
    var networkError:AtomicBoolean = new AtomicBoolean(false)

    var networkConnection:Connection = null
    private var networkSession:Session = null
    var subscribed = false
    var attemptingToJoin:AtomicBoolean = new AtomicBoolean(false)
    var connectedToGame:AtomicBoolean = new AtomicBoolean(false)
    private var sessionId:String = null // Connected to this client's instance of Player

    private var myPlayer:Player = null;
    val players:ConcurrentHashMap[String, Player] = new ConcurrentHashMap[String, Player]()
    val newPlayers:java.util.concurrent.LinkedBlockingQueue[Player] = new java.util.concurrent.LinkedBlockingQueue[Player]()
    val updatedPlayers:java.util.concurrent.LinkedBlockingQueue[Player] = new java.util.concurrent.LinkedBlockingQueue[Player]()
    val disconnectedPlayers:java.util.concurrent.LinkedBlockingQueue[Player] = new java.util.concurrent.LinkedBlockingQueue[Player]()

    val projectiles:ConcurrentHashMap[String, Projectile] = new ConcurrentHashMap[String, Projectile]()
    val newProjectiles:java.util.concurrent.LinkedBlockingQueue[Projectile] = new java.util.concurrent.LinkedBlockingQueue[Projectile]()
    val updatedProjectiles:java.util.concurrent.LinkedBlockingQueue[Projectile] = new java.util.concurrent.LinkedBlockingQueue[Projectile]()
    val projectilesToRemove:java.util.concurrent.LinkedBlockingQueue[Projectile] = new java.util.concurrent.LinkedBlockingQueue[Projectile]()

    val zombies:ConcurrentHashMap[String, Zombie] = new ConcurrentHashMap[String, Zombie]()
    val newZombies:java.util.concurrent.LinkedBlockingQueue[Zombie] = new java.util.concurrent.LinkedBlockingQueue[Zombie]()
    val updatedZombeis:java.util.concurrent.LinkedBlockingQueue[Zombie] = new java.util.concurrent.LinkedBlockingQueue[Zombie]()
    val zombiesToRemove:java.util.concurrent.LinkedBlockingQueue[Zombie] = new java.util.concurrent.LinkedBlockingQueue[Zombie]()

    /**
      * Joins the game.
      * Sends message to server to join.
      * Waits until server has responded with a PlayerMessage of type ADD associated with this client instance.
      */
    def joinGame(): Unit = {
        println("Joining game")
        lastStartTime = System.currentTimeMillis()
        attemptingToJoin.set(true)

        try {
            networkConnection = new Connection("ws://"+hostName+"/connect_desktop") // This may throw the error. THIS IS THE REASON FOR THE TRY CATCH
            networkError.set(false)
            networkSession = networkConnection.getSession
            networkSession.subscribeToEvent("/user/queue/join", new JoinGameHandler(this))
            networkSession.subscribeToEvent("/user/queue/all_players", new GetAllPlayersHandler(this))
            networkSession.sendToServer("/app/join", playerName)
            /* Wait until joined. */
            while (attemptingToJoin.get()) {
                println("Sleeping")
                Thread.sleep(1000)
            }

            players.put(sessionId, myPlayer)
            newPlayers.add(myPlayer)

            networkSession.subscribeToEvent("/user/queue/score", new ScoreHandler(this))
            networkSession.subscribeToEvent("/game/players", new PlayerMessageHandler(this))
            networkSession.subscribeToEvent("/game/zombies", new ZombieMessageHandler(this))
            networkSession.subscribeToEvent("/game/projectiles", new ProjectileMessageHandler(this))
        } catch {
            case e: Exception => {
                networkError.set(true)
                attemptingToJoin.set(false)
                connectedToGame.set(false)
            }

        }
    }

    /**
      * Disconnects from game.
      * Sends message to server to remove this client's player instance.
      * Removes this client's player instance locally.
      */
    def leaveGame(): Unit = {
        if (connectedToGame.get()) {
            connectedToGame.set(false)
            networkSession.sendToServer("/app/leave", null)
            networkSession.disconnect()
            myPlayer = null
            sessionId = null
            lastStartTime = 0
            players.clear()
            projectiles.clear()
            zombies.clear()
            println("Left game.")
        }
    }

    /**
      * Updates location locally.
      * Sends request to update it on the server. (The response from this particular request will be ignored in PlayerMessageHandler
      *  because the location is updated locally before the request. Updating location locally after response would be redundant.)
      * @param location
      */
    def updateMyPlayerLocation(location: PhysicsVector): Unit = {
        myPlayer.setLocation(location)
        networkSession.sendToServer("/app/move", location)
    }


    def shootProjectile(projectile: Projectile): Unit = {
        networkSession.sendToServer("/app/shoot", projectile.props)
    }

    def updateHealth(health: Double): Unit = {
        myPlayer.updateHealth(health)
        networkSession.sendToServer("/app/health", health)
    }

    def updateScore(newScore:Double): Unit = {
        myPlayer.props.score = newScore
    }

    def getMyPlayer(): Player = {
        this.myPlayer
    }

    def setMyPlayer(player:Player): Unit = {
        this.myPlayer = player
    }

    def getSessionId(): String = {
        this.sessionId
    }

    def setSessionId(id:String): Unit = {
        this.sessionId = id
    }

    def setPlayerName(name:String): Unit = {
        this.playerName = name
    }

    def setHostName(name:String): Unit = {
        this.hostName = name
    }
}
