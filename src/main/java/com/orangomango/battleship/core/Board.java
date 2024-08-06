package com.orangomango.battleship.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Board{
	private static final Image WATER_IMAGE = new Image(Board.class.getResourceAsStream("/water.png"));
	private static final Image INDICATOR_IMAGE = new Image(Board.class.getResourceAsStream("/indicator.png"));

	/*
	 * 0 - empty
	 * 1 - ship
	 * 2 - shot and empty
	 * 3 - shot and ship
	*/
	private int[][] board;
	private boolean hasData;
	private ArrayList<Ship> ships;

	public Board(ArrayList<Ship> ships){
		this.board = new int[10][10];
		this.ships = ships;
	}

	public void fillFromShips(){
		for (Ship ship : this.ships){
			ship.updateBoard(this);
		}
	}

	public void setData(String data){
		this.hasData = true;
		String[] lines = data.split("\n");
		for (int i = 0; i < 10; i++){
			for (int j = 0; j < 10; j++){
				int n = Integer.parseInt(String.valueOf(lines[i].charAt(j)));
				this.board[j][i] = n;
			}
		}
	}

	public void setCell(int x, int y){
		if (x >= 0 && y >= 0 && x < 10 && y < 10){
			this.board[x][y] = 1;
		}
	}

	public Ship update(int x, int y){
		int num = this.board[x][y];
		if (num == 0){
			this.board[x][y] = 2;
		} else if (num == 1){
			this.board[x][y] = 3;
		}

		// Check if a ship got destroyed
		for (Ship ship : this.ships){
			if (ship.contains(x, y) && ship.isDestroyed(this)){
				return ship;
			}
		}

		return null;
	}

	public boolean isGameOver(){
		for (Ship ship : this.ships){
			if (!ship.isDestroyed(this)){
				return false;
			}
		}

		return true;
	}

	public void render(GraphicsContext gc, int[][] enemyBoard){
		final int[][] board = enemyBoard == null ? this.board : enemyBoard;

		for (int x = 0; x < 10; x++){
			for (int y = 0; y < 10; y++){
				gc.drawImage(WATER_IMAGE, 100+x*60, 100+y*60, 60, 60);
			}
		}
	}

	public void renderIndicators(GraphicsContext gc, int[][] enemyBoard){
		final int[][] board = enemyBoard == null ? this.board : enemyBoard;

		for (int x = 0; x < 10; x++){
			for (int y = 0; y < 10; y++){
				if (board[x][y] != 0){
					final int frameIndex = board[x][y] == 3 ? 0 : (board[x][y] == 2 ? 1 : -1);
					gc.drawImage(INDICATOR_IMAGE, 1+18*frameIndex, 1, 16, 16, 100+x*60, 100+y*60, 60, 60);
				}
			}
		}
	}

	public boolean hasData(){
		return this.hasData;
	}

	public int getCell(int x, int y){
		return this.board[x][y];
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		for (int y = 0; y < 10; y++){
			for (int x = 0; x < 10; x++){
				builder.append(Integer.toString(this.board[x][y]));
			}
			if (y != 9) builder.append("\n");
		}

		return builder.toString();
	}
}