package com.orangomango.battleship.core;

public class Board{
	/*
	 * 0 - empty
	 * 1 - ship
	 * 2 - shot and empty
	 * 3 - shot and ship
	*/
	private int[][] board;
	private boolean hasData;

	public Board(){
		this.board = new int[10][10];
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

	public boolean hasData(){
		return this.hasData;
	}
}