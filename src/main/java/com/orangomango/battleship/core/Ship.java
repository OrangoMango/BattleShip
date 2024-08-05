package com.orangomango.battleship.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;

public class Ship{
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
		gc.setFill(Color.LIME);
		gc.fillRect(100+this.x*60+5, 100+this.y*60+5, this.width*60-10, this.height*60-10);
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

	public boolean isValid(ArrayList<Ship> ships){
		if (this.x >= 0 && this.y >= 0 && this.x+this.width < 10 && this.y+this.height < 10){
			for (Ship ship : ships){
				if (ship != this){
					Rectangle2D thisRect = new Rectangle2D(this.x, this.y, this.width, this.height);
					Rectangle2D otherRect = new Rectangle2D(ship.x, ship.y, ship.width, ship.height);
					if (thisRect.intersects(otherRect)){
						return false;
					}
				}
			}

			return true;
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
}