package com.orangomango.battleship;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class UiChoice{
	private static final Font FONT = Font.loadFont(UiChoice.class.getResourceAsStream("/font.ttf"), 35);

	private double x, y;
	private String text;
	private boolean selected = false;
	private Runnable onClick;

	public UiChoice(double x, double y, String text, Runnable onClick){
		this.x = x;
		this.y = y;
		this.text = text;
		this.onClick = onClick;
	}

	public void render(GraphicsContext gc){
		gc.setFont(FONT);
		gc.setFill(Color.WHITE);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText((this.selected ? "> " : "") + this.text, this.x, this.y);
	}

	public void setSelected(boolean value){
		this.selected = value;
	}

	public void fire(){
		this.onClick.run();
	}
}