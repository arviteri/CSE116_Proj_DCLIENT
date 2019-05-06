package client.scala.game.objects

import java.util.Random

import client.java.game.objects.PhysicsVector
import javafx.geometry.Pos
import javafx.scene.{CacheHint, Parent}
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.{Rectangle, Shape}
import javafx.scene.paint.Color._
import javafx.scene.text.TextAlignment

object Player {
    val WIDTH:Double = client.java.game.objects.Player.WIDTH
    val HEIGHT:Double = client.java.game.objects.Player.HEIGHT

    def createPlayerLabel(props: client.java.game.objects.Player): Label = {
        var label = new Label(props.name)
        label.setAlignment(Pos.CENTER)
        label.setMaxWidth(100)
        label.setPrefWidth(100)
        label.setMinWidth(100)
        label.setTranslateX(-40)
        label.setTranslateY(-20)
        label
    }

    def createPlayerBox(props: client.java.game.objects.Player): Shape = {
        var sprite = new Rectangle(WIDTH, HEIGHT);
        sprite.setFill(BLUE);
        sprite
    }

    def createSprite(props: client.java.game.objects.Player): Parent = {
        var sprite = new Pane();
        sprite.setTranslateX(props.location.x)
        sprite.setTranslateY(props.location.y)
        sprite.getChildren.add(createPlayerLabel(props))
        sprite.getChildren.add(createPlayerBox(props))
        sprite
    }
}

class Player(var props: client.java.game.objects.Player) {
    var sprite = Player.createSprite(props)
    sprite.setCache(true)
    sprite.setCacheHint(CacheHint.SPEED)

    def setLocation(location: PhysicsVector): Unit = {
        props.setLocation(location)
        this.sprite.setTranslateX(location.x)
        this.sprite.setTranslateY(location.y)
    }

    def updateHealth(newHealth: Double): Unit = {
        props.health = newHealth
        this.sprite.setOpacity(newHealth/100)
    }
}
