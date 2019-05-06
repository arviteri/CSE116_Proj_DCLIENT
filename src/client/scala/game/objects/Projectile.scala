package client.scala.game.objects

import client.java.game.objects.PhysicsVector
import javafx.scene.CacheHint
import javafx.scene.shape.{Circle, Shape}
import javafx.scene.paint.Color._

object Projectile {
    val RADIUS: Double = 2.5

    def createSprite(props: client.java.game.objects.Projectile): Shape = {
        var sprite = new Circle(RADIUS);
        sprite.setTranslateX(props.location.x)
        sprite.setTranslateY(props.location.y)
        sprite.setFill(BLACK)
        return sprite
    }
}

class Projectile(var props: client.java.game.objects.Projectile) {
    var sprite = Projectile.createSprite(props)
    sprite.setCache(true)
    sprite.setCacheHint(CacheHint.SPEED)

    def setLocation(location: PhysicsVector): Unit = {
        props.setLocation(location)
        this.sprite.setTranslateX(location.x)
        this.sprite.setTranslateY(location.y)
    }
}
