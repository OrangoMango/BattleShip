package com.orangomango.battleship.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Board{
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

	public void update(int x, int y){
		int num = this.board[x][y];
		if (num == 0){
			this.board[x][y] = 2;
		} else if (num == 1){
			this.board[x][y] = 3;
		}

		// Check if a ship got destroyed
		// TODO ...
	}

	public void render(GraphicsContext gc, int[][] enemyBoard){
		gc.setStroke(enemyBoard == null ? Color.WHITE : Color.LIME);
		gc.setLineWidth(2);
		gc.strokeRect(100, 100, 600, 600);

		final int[][] board = enemyBoard == null ? this.board : enemyBoard;

		for (int x = 0; x < 10; x++){
			for (int y = 0; y < 10; y++){
				if (board[x][y] != 0){
					Color color = null;
					switch (board[x][y]){
						case 1:
							color = Color.WHITE;
							break;
						case 2:
							color = Color.BLUE;
							break;
						case 3:
							color = Color.RED;
							break;
					}

					gc.setFill(color);
					gc.fillRect(100+x*60, 100+y*60, 60, 60);
				}
			}
		}

		for (int i = 1; i < 10; i++){
			gc.strokeLine(100+i*60, 100, 100+i*60, 700);
		}

		for (int i = 1; i < 10; i++){
			gc.strokeLine(100, 100+i*60, 700, 100+i*60);
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