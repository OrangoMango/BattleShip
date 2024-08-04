package com.orangomango.battleship.client;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.animation.*;

import java.util.HashMap;

import com.orangomango.battleship.Util;
import com.orangomango.battleship.core.Board;

public class ClientApplication extends Application{
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;

	private Client client;
	private Board board;
	private int[][] enemyBoard = new int[10][10];
	private HashMap<KeyCode, Boolean> keys = new HashMap<>();
	private boolean gameStarted = false;

	@Override
	public void start(Stage stage){
		StackPane pane = new StackPane();
		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		pane.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(e -> this.keys.put(e.getCode(), true));
		canvas.setOnKeyReleased(e -> this.keys.put(e.getCode(), false));

		canvas.setOnMousePressed(e -> {
			int px = (int)((e.getX()-100) / 60);
			int py = (int)((e.getY()-100) / 60);
			if (this.gameStarted){
				if (this.client.isCurrentTurn()){
					String pos = Util.convertPos(px, py);
					this.client.send(Util.SHOOT_MESSAGE);
					this.client.send(Util.SHOOT_MESSAGE+":"+pos);
				}
			} else {
				this.board.toggleCell(px, py);
			}
		});

		this.board = new Board();

		this.client = new Client(Util.getLocalAddress(), 1234);
		this.client.listen(this.board, this.enemyBoard);

		AnimationTimer loop = new AnimationTimer(){
			@Override
			public void handle(long time){
				update(gc);
			}
		};
		loop.start();

		Scene scene = new Scene(pane, WIDTH, HEIGHT);
		scene.setFill(Color.BLACK);

		stage.setTitle("Battleship client [player"+this.client.getId()+"]");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, WIDTH, HEIGHT);
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, WIDTH, HEIGHT);

		if (this.keys.getOrDefault(KeyCode.SPACE, false)){
			// Send board and be ready
			this.client.send(Util.BOARD_DATA);
			this.client.send("player"+this.client.getId());
			this.client.send(this.board.toString());
			this.gameStarted = true;
			this.keys.put(KeyCode.SPACE, false);
		}

		this.board.render(gc, this.client.isCurrentTurn() ? this.enemyBoard : null);
	}
}