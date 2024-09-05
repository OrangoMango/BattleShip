package com.orangomango.battleship.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;

public class Ship{
	private static final Image IMAGE = new Image(Ship.class.getResourceAsStream("/ships.png"));

	private double x, y, width, height;

	public Ship(double x, double y, double w, double h){
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public boolean contains(double x, double y){
		Rectangle2D rect = new Rectangle2D(this.x, this.y, this.width, this.height);
		return rect.contains(x, y);
	}

	public void render(GraphicsContext gc){
		final int frameIndex = 5-(int)Math.max(this.width, this.height);

		if (this.width < this.height){
			gc.save();
			gc.translate(100+(this.x+1)*60, 100+this.y*60);
			gc.rotate(90);
			gc.drawImage(IMAGE, 1, 1+18*frameIndex, 16*Math.max(this.width, this.height), 16, 5, 5, this.height*60-10, this.width*60-10);
			gc.restore();
		} else {
			gc.drawImage(IMAGE, 1, 1+18*frameIndex, 16*Math.max(this.width, this.height), 16, 100+this.x*60+5, 100+this.y*60+5, this.width*60-10, this.height*60-10);
		}
	}

	public void relocate(double x, double y){
		this.x = x;
		this.y = y;
	}

	public void release(){
		this.x = (int)Math.round(this.x);
		this.y = (int)Math.round(this.y);
	}

	public void rotate(){
		double temp = this.width;
		this.width = this.height;
		this.height = temp;
	}

	public void updateBoard(Board board){
		int px = (int)this.x;
		int py = (int)this.y;
		for (int i = px; i < px+this.width; i++){
			for (int j = py; j < py+this.height; j++){
				board.setCell(i, j);
			}
		}
	}

	public boolean isDestroyed(Board board){
		int px = (int)this.x;
		int py = (int)this.y;
		for (int i = px; i < px+this.width; i++){
			for (int j = py; j < py+this.height; j++){
				if (board.getCell(i, j) != 3){
					return false;
				}
			}
		}

		return true;
	}

	public boolean isValid(ArrayList<Ship> ships){
		if (this.x >= 0 && this.y >= 0 && this.x+this.width <= 10 && this.y+this.height <= 10){
			boolean[][] map = new boolean[10][10];

			for (Ship ship : ships){
				if (ship == this) continue;
				for (int x = 0; x < ship.width; x++){
					for (int y = 0; y < ship.height; y++){
						map[(int)ship.x+x][(int)ship.y+y] = true;
					}
				}
			}

			boolean collide = false;
			for (int x = 0; x < this.width; x++){
				for (int y = 0; y < this.height; y++){
					if (map[(int)this.x+x][(int)this.y+y]){
						collide = true;
					}
				}
			}

			return !collide;
		} else {
			return false;
		}
	}

	public double getX(){
		return this.x;
	}

	public double getY(){
		return this.y;
	}

	@Override
	public String toString(){
		return String.format("%s;%s;%s;%s", this.x, this.y, this.width, this.height);
	}

	public static Ship parseShip(String text){
		if (text.equals("null")) return null;

		String[] parts = text.split(";");
		double x = Double.parseDouble(parts[0]);
		double y = Double.parseDouble(parts[1]);
		double w = Double.parseDouble(parts[2]);
		double h = Double.parseDouble(parts[3]);

		return new Ship(x, y, w, h);
	}
}