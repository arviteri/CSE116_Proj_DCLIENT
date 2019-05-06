package client.java.main;

import client.java.network.connection.Connection;
import client.java.network.connection.Session;
import client.scala.game.Game;
import client.scala.game.controllers.PlayerController;
import client.scala.network.GameSession;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class App extends Application {

    Game game = new Game();
    PlayerController controller = new PlayerController(game.session());

    Pane root = new Pane();
    TextField nameInput = createNameInput();
    TextField hostInput = createHostInput();
    Button joinButton = createJoinButton();
    Label scoreLabel = new Label();
    Label networkErrorLabel = new Label();

    public Parent createContent() {
        root.setPrefSize(Game.WIDTH(), Game.HEIGHT());

        scoreLabel.setTranslateX(10);
        scoreLabel.setTranslateY(10);
        scoreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");

        networkErrorLabel.setMinWidth(Game.WIDTH());
        networkErrorLabel.setAlignment(Pos.CENTER);
        networkErrorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: red");
        networkErrorLabel.setLayoutY(Game.HEIGHT()/2 - 85);

        hostInput.setLayoutX(Game.WIDTH()/2 - 125); // 125 because 25- is the width of the input.
        hostInput.setLayoutY(Game.HEIGHT()/2 - 45);

        nameInput.setLayoutX(Game.WIDTH()/2 - 125); // 125 because 25- is the width of the input.
        nameInput.setLayoutY(Game.HEIGHT()/2 - 12);

        joinButton.setLayoutX(Game.WIDTH()/2 - 50); // 50 because 100 is the width of the button.
        joinButton.setLayoutY(Game.HEIGHT()/2 + 25);


        root.getChildren().add(scoreLabel);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (controller.player() == null) {
                    controller.setPlayer(game.session().getMyPlayer());
                }

                if (game.session().connectedToGame().get() || game.session().attemptingToJoin().get()) {
                    root.getChildren().remove(hostInput);
                    root.getChildren().remove(nameInput);
                    root.getChildren().remove(joinButton);
                    if (game.session().connectedToGame().get()) {
                        game.updateSprites(root.getChildren());
                        scoreLabel.setText(Integer.toString((int)game.session().getMyPlayer().props().score));
                        /* Make sure game is still connected to network */
                        if (game.session().networkConnection().getSession().isConnected() == false) {
                            game.session().networkError().set(true);
                            game.session().leaveGame();
                        }
                    }
                } else if (root.getChildren().indexOf(hostInput) < 0) {
                    if (root.getChildren().size() > 0) {
                        root.getChildren().clear();
                    } else {
                        root.getChildren().add(scoreLabel);
                        root.getChildren().add(networkErrorLabel);
                        root.getChildren().add(hostInput);
                        root.getChildren().add(nameInput);
                        root.getChildren().add(joinButton);
                        joinButton.requestFocus();
                    }
                }

                if (game.session().networkError().get() && networkErrorLabel.getText().equals("")) {
                    System.out.println("NetworkError");
                    networkErrorLabel.setText("An error occured while connecting to the host.");
                } else if (!game.session().networkError().get() && !networkErrorLabel.getText().equals("")) {
                    networkErrorLabel.setText("");
                }
            }
        };

        timer.start();
        return root;
    }

    public TextField createNameInput() {
        TextField input = new TextField();
        input.setMinHeight(25);
        input.setMinWidth(250);
        input.setPromptText("Enter a name");
        input.setAlignment(Pos.CENTER);
        return input;
    }

    public TextField createHostInput() {
        TextField input = new TextField();
        input.setMinHeight(25);
        input.setMinWidth(250);
        input.setPromptText("Enter a hostname (i.e localhost:8080)");
        input.setAlignment(Pos.CENTER);
        return input;
    }

    public Button createJoinButton() {
        Button button = new Button("Join Game");
        button.setMinHeight(25);
        button.setMinWidth(100);
        button.setDefaultButton(true);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                game.session().setPlayerName(nameInput.getText());
                game.session().setHostName(hostInput.getText());
                controller.setPlayer(null); // When game starts this will be set to the correct player.
                game.start();
            }
        });
        return button;
    }

    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        scene.addEventHandler(KeyEvent.KEY_PRESSED, controller);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
