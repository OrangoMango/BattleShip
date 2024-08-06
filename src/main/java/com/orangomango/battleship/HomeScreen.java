package com.orangomango.battleship;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;

import java.util.HashMap;

import com.orangomango.battleship.client.GameScreen;
import com.orangomango.battleship.server.Server;

public class HomeScreen{
	private HashMap<KeyCode, Boolean> keys = new HashMap<>();
	private Scene scene;
	private Stage stage;
	private AnimationTimer loop;

	public HomeScreen(Stage stage){
		this.stage = stage;
		StackPane pane = new StackPane();
		Canvas canvas = new Canvas(Util.WIDTH, Util.HEIGHT);
		pane.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(e -> this.keys.put(e.getCode(), true));
		canvas.setOnKeyReleased(e -> this.keys.put(e.getCode(), false));

		this.loop = new AnimationTimer(){
			@Override
			public void handle(long time){
				update(gc);
			}
		};
		this.loop.start();

		this.scene = new Scene(pane, Util.WIDTH, Util.HEIGHT);
		this.scene.setFill(Color.BLACK);
	}

	private void startGameScreen(){
		this.loop.stop();
		GameScreen gameScreen = new GameScreen();
		Scene scene = gameScreen.getScene();
		this.stage.setScene(scene);
		this.stage.setTitle("BattleShip client - player: "+gameScreen.getClient().getId());
	}

	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, Util.WIDTH, Util.HEIGHT);
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, Util.WIDTH, Util.HEIGHT);

		if (this.keys.getOrDefault(KeyCode.SPACE, false)){
			startGameScreen();
			this.keys.put(KeyCode.SPACE, false);
		} else if (this.keys.getOrDefault(KeyCode.S, false)){
			// Start server
			Server server = new Server(Util.getLocalAddress(), 1234);
			server.listen();
			System.out.println("Server started");
			this.keys.put(KeyCode.S, false);
		}
	}

	public Scene getScene(){
		return this.scene;
	}
}