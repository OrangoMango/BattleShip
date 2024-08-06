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

public class ServerSelectScreen{
	private static final Font FONT = Font.loadFont(HomeScreen.class.getResourceAsStream("/font.ttf"), 30);

	private HashMap<KeyCode, Boolean> keys = new HashMap<>();
	private Scene scene;
	private Stage stage;
	private AnimationTimer loop;
	private volatile boolean updaterRunning = true;
	private ArrayList<UiChoice> buttons = new ArrayList<>();
	private int selectedButton = 0;

	public ServerSelectScreen(Stage stage){
		this.stage = stage;
		StackPane pane = new StackPane();
		Canvas canvas = new Canvas(Util.WIDTH, Util.HEIGHT);
		pane.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(e -> this.keys.put(e.getCode(), true));
		canvas.setOnKeyReleased(e -> this.keys.put(e.getCode(), false));

		Thread updater = new Thread(() -> {
			while (updaterRunning){
				try {
					this.buttons.clear();
					for (String data : Client.servers){
						String[] parts = data.split(";");
						final String ip = parts[0];
						final int port = Integer.parseInt(parts[1]);

						UiChoice btn = new UiChoice(400, 100+this.buttons.size()*100, String.format("%s:%d", ip, port), () -> {
							Platform.runLater(() -> {
								GameScreen gameScreen = new GameScreen(this.stage, null, ip, port);
								Scene scene = gameScreen.getScene();
								if (scene != null){
									quit();
									Client.stopDiscovering();
									this.stage.setScene(scene);
									this.stage.setTitle("BattleShip client - player: "+gameScreen.getClient().getId());
								}
							});
						});
						this.buttons.add(btn);

						if (this.buttons.size() == 1){
							btn.setSelected(true);
							this.selectedButton = 0;
						}
					}

					Thread.sleep(1000);
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
		});
		updater.setDaemon(true);
		updater.start();

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

	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, Util.WIDTH, Util.HEIGHT);
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, Util.WIDTH, Util.HEIGHT);

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
		} else if (this.keys.getOrDefault(KeyCode.LEFT, false)){
			Platform.runLater(() -> {
				HomeScreen homeScreen = new HomeScreen(this.stage);
				Scene scene = homeScreen.getScene();
				if (scene != null){
					quit();
					this.stage.setScene(scene);
				}
			});
			this.keys.put(KeyCode.LEFT, false);
		}

		for (int i = 0; i < this.buttons.size(); i++){
			this.buttons.get(i).render(gc);
		}

		if (this.buttons.size() == 0){
			gc.setFill(Color.WHITE);
			gc.setFont(FONT);
			gc.setTextAlign(TextAlignment.CENTER);
			gc.fillText("No servers found in the same network", Util.WIDTH*0.5, Util.HEIGHT*0.5);
		}
	}

	private void quit(){
		this.loop.stop();
		this.updaterRunning = false;
	}

	public Scene getScene(){
		return this.scene;
	}
}