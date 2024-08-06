package com.orangomango.battleship.client;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.animation.AnimationTimer;

import java.util.HashMap;
import java.util.ArrayList;

import com.orangomango.battleship.Util;
import com.orangomango.battleship.core.Board;
import com.orangomango.battleship.core.Ship;

public class ClientApplication extends Application{
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;

	private Client client;
	private Board board;
	private int[][] enemyBoard = new int[10][10];
	private HashMap<KeyCode, Boolean> keys = new HashMap<>();
	private boolean gameStarted = false;
	private ArrayList<Ship> ships = new ArrayList<>();
	private Ship dragShip = null;
	private double dragOffsetX, dragOffsetY;
	private double backupDragX, backupDragY;

	@Override
	public void start(Stage stage){
		StackPane pane = new StackPane();
		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		pane.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setImageSmoothing(false);

		// Load ships
		this.ships.add(new Ship(0, 0, 1, 5));
		this.ships.add(new Ship(2, 0, 1, 4));
		this.ships.add(new Ship(4, 0, 1, 3));
		this.ships.add(new Ship(6, 0, 1, 3));
		this.ships.add(new Ship(8, 0, 1, 2));

		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(e -> this.keys.put(e.getCode(), true));
		canvas.setOnKeyReleased(e -> this.keys.put(e.getCode(), false));

		canvas.setOnMousePressed(e -> {
			double px = (e.getX()-100) / 60;
			double py = (e.getY()-100) / 60;
			if (this.gameStarted && this.client.isCurrentTurn()){
				String pos = Util.convertPos((int)px, (int)py);
				this.client.send(Util.SHOOT_MESSAGE);
				this.client.send(Util.SHOOT_MESSAGE+":"+pos);
			} else {
				Ship found = null;
				for (Ship ship : this.ships){
					if (ship.contains(px, py)){
						found = ship;
						break;
					}
				}

				if (found != null){
					if (e.getButton() == MouseButton.PRIMARY){
						this.dragShip = found; // Start dragging
						this.backupDragX = this.dragShip.getX();
						this.backupDragY = this.dragShip.getY();
						this.dragOffsetX = px-this.backupDragX;
						this.dragOffsetY = py-this.backupDragY;
					} else if (e.getButton() == MouseButton.SECONDARY){
						found.rotate();
						if (!found.isValid(this.ships)){
							found.rotate(); // Reset
						}
					}
				}
			}
		});

		canvas.setOnMouseDragged(e -> {
			if (this.dragShip != null){
				double px = (e.getX()-100) / 60;
				double py = (e.getY()-100) / 60;
				this.dragShip.relocate(px-this.dragOffsetX, py-this.dragOffsetY);
			}
		});

		canvas.setOnMouseReleased(e -> {
			if (this.dragShip != null){
				if (this.dragShip.isValid(this.ships)){
					this.dragShip.release();
				} else {
					this.dragShip.relocate(this.backupDragX, this.backupDragY);
				}

				this.dragShip = null;
			}
		});

		this.board = new Board(this.ships);

		this.client = new Client(Util.getLocalAddress(), 1234);
		this.client.listen(this.board, this.enemyBoard, mySide -> {
			System.out.println(mySide);
		}, boardStatus -> {
			if (boardStatus == null){
				System.out.println("GAME OVER (I lost)");
			} else {
				System.out.println("GAME OVER (they lost)");
			}
		});

		if (!this.client.isConnected()){
			System.exit(0); // Connection error
		}

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
			this.board.fillFromShips();
			this.client.send(Util.BOARD_DATA);
			this.client.send("player"+this.client.getId());
			this.client.send(this.board.toString());
			this.gameStarted = true;
			this.keys.put(KeyCode.SPACE, false);
		}

		this.board.render(gc, this.client.isCurrentTurn() ? this.enemyBoard : null);
		if (!this.client.isCurrentTurn()){
			for (Ship ship : this.ships){
				ship.render(gc);
			}
		}

		this.board.renderIndicators(gc, this.client.isCurrentTurn() ? this.enemyBoard : null);
	}
}