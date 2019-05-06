package client.scala.game.objects

import client.java.game.objects.PhysicsVector
import javafx.scene.CacheHint
import javafx.scene.shape.{Rectangle, Shape}
import javafx.scene.paint.Color._

object Zombie {
    val WIDTH:Double = client.java.game.objects.Zombie.WIDTH
    val HEIGHT:Double = client.java.game.objects.Zombie.HEIGHT

    def createSprite(props: client.java.game.objects.Zombie): Shape = {
        var sprite = new Rectangle(WIDTH, HEIGHT);
        sprite.setTranslateX(props.location.x)
        sprite.setTranslateY(props.location.y)
        sprite.setFill(RED)
        return sprite
    }
}

class Zombie(var props: client.java.game.objects.Zombie) {
    var sprite = Zombie.createSprite(props)
    sprite.setCache(true)
    sprite.setCacheHint(CacheHint.SPEED)

    def setLocation(location: PhysicsVector): Unit = {
        props.setLocation(location)
        this.sprite.setTranslateX(location.x)
        this.sprite.setTranslateY(location.y)
    }

    def updateHealth(newHealth: Double): Unit = {
        props.health = newHealth
        this.sprite.setOpacity(newHealth/100.0)
    }
}
