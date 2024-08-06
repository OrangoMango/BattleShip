package com.orangomango.battleship;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;

import java.util.HashMap;
import java.util.ArrayList;

import com.orangomango.battleship.client.Client;
import com.orangomango.battleship.client.GameScreen;
import com.orangomango.battleship.server.Server;

public class HomeScreen{
	private static final Font FONT = Font.loadFont(HomeScreen.class.getResourceAsStream("/font.ttf"), 25);

	private HashMap<KeyCode, Boolean> keys = new HashMap<>();
	private Scene scene;
	private Stage stage;
	private AnimationTimer loop;
	private ArrayList<UiChoice> buttons = new ArrayList<>();
	private int selectedButton = 0;
	private boolean inputDisabled;
	private Server server;
	private boolean showCredits = false;
	private double offset = 0;

	public HomeScreen(Stage stage){
		this.stage = stage;
		StackPane pane = new StackPane();
		Canvas canvas = new Canvas(Util.WIDTH, Util.HEIGHT);
		pane.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(e -> this.keys.put(e.getCode(), true));
		canvas.setOnKeyReleased(e -> this.keys.put(e.getCode(), false));

		canvas.setOnMousePressed(e -> this.showCredits = false);

		//Client.discover();

		UiChoice startServerButton = new UiChoice(400, 200, "START SERVER", () -> {
			this.server = new Server(Util.getLocalAddress(), Util.GAME_PORT);
			if (this.server.isServerStarted()){
				System.out.println("Server started");
				this.server.listen();
				this.inputDisabled = true;
				this.server.doWhenReady(() -> Platform.runLater(() -> startGameScreen()));
			}
		});
		startServerButton.setSelected(true);
		UiChoice connectButton = new UiChoice(400, 300, "CONNECT TO GAME", () -> {
			// TODO: Use user's choice ip and port
			startGameScreen();
		});
		UiChoice creditsButton = new UiChoice(400, 400, "CREDITS", () -> {
			this.showCredits = true;
			this.offset = 0;
		});
		UiChoice quitButton = new UiChoice(400, 500, "QUIT", () -> System.exit(0));

		this.buttons.add(startServerButton);
		this.buttons.add(connectButton);
		this.buttons.add(creditsButton);
		this.buttons.add(quitButton);

		this.loop = new AnimationTimer(){
			private long lastTime = System.nanoTime();

			@Override
			public void handle(long time){
				update(gc, (time-this.lastTime)/1000000.0);
				this.lastTime = time;
			}
		};
		this.loop.start();

		this.scene = new Scene(pane, Util.WIDTH, Util.HEIGHT);
		this.scene.setFill(Color.BLACK);
	}

	private void startGameScreen(){
		GameScreen gameScreen = new GameScreen(this.stage, this.server);
		Scene scene = gameScreen.getScene();
		if (scene != null){
			this.loop.stop();
			Client.stopDiscovering();
			this.stage.setScene(scene);
			this.stage.setTitle("BattleShip client - player: "+gameScreen.getClient().getId());
		}
	}

	private void update(GraphicsContext gc, double dt){
		gc.clearRect(0, 0, Util.WIDTH, Util.HEIGHT);
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, Util.WIDTH, Util.HEIGHT);

		if (this.showCredits){
			this.offset += 0.05*dt;
			if (this.offset >= Util.HEIGHT) this.offset = 0;
			gc.setFill(Color.WHITE);
			gc.setFont(FONT);
			gc.setTextAlign(TextAlignment.CENTER);
			gc.fillText(Util.CREDITS, 400, 400-this.offset);
		} else {
			if (this.inputDisabled){
				if (this.keys.getOrDefault(KeyCode.SPACE, false)){
					this.inputDisabled = false;
					this.server.destroy(); // Cancel game
					this.keys.put(KeyCode.SPACE, false);
				}
			} else {
				if (this.keys.getOrDefault(KeyCode.UP, false)){
					this.buttons.get(this.selectedButton).setSelected(false);
					this.selectedButton = (this.selectedButton + this.buttons.size()-1) % this.buttons.size();
					this.buttons.get(this.selectedButton).setSelected(true);
					this.keys.put(KeyCode.UP, false);
				} else if (this.keys.getOrDefault(KeyCode.DOWN, false)){
					this.buttons.get(this.selectedButton).setSelected(false);
					this.selectedButton = (this.selectedButton + 1) % this.buttons.size();
					this.buttons.get(this.selectedButton).setSelected(true);
					this.keys.put(KeyCode.DOWN, false);
				} else if (this.keys.getOrDefault(KeyCode.RIGHT, false)){
					// Select current button
					this.buttons.get(this.selectedButton).fire();
					this.keys.put(KeyCode.RIGHT, false);
				}
			}

			for (UiChoice btn : this.buttons){
				btn.render(gc);
			}

			if (this.inputDisabled){
				gc.save();
				gc.setGlobalAlpha(0.75);
				gc.setFill(Color.YELLOW);
				gc.fillRect(200, 200, 400, 400);
				gc.restore();
			}
		}
	}

	public Scene getScene(){
		return this.scene;
	}
}